package com.example.paste.create;

import com.example.paste.create.data.PasteCreated;
import com.example.paste.create.data.PasteCreatedRepository;
import com.example.paste.create.exceptions.InvalidInputException;
import com.example.paste.create.exceptions.SystemErrorException;
import com.example.paste.create.models.PasteContent;
import com.example.paste.create.models.PasteUrls;
import com.example.paste.model.PostPastesRequest;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class CreateService {

  private final PasteCreatedRepository pasteCreatedRepository;
  private final URI baseUrl;

  public CreateService(PasteCreatedRepository pasteCreatedRepository, CreateConfig config) {
    this.pasteCreatedRepository = pasteCreatedRepository;
    this.baseUrl = config.getBaseUrl();
  }

  @WithSpan
  public CreateResult create(PostPastesRequest request) {
    Optional<PasteContent> contentOpt =
        PasteContent.parse(
            request.getContent(),
            request.getTitle(),
            request.getExpiresIn(),
            request.getLanguage());

    if (contentOpt.isEmpty()) {
      return new CreateResult.Failure(new InvalidInputException("Invalid request"));
    }

    PasteContent content = contentOpt.get();
    String pasteId = UUID.randomUUID().toString().replace("-", "");
    Instant createdAt = Instant.now();
    Instant expiresAt = createdAt.plusSeconds(content.expiresIn());

    PasteCreated event =
        new PasteCreated(
            pasteId,
            content.content(),
            content.title(),
            content.language(),
            LocalDateTime.ofInstant(createdAt, java.time.ZoneOffset.UTC),
            LocalDateTime.ofInstant(expiresAt, java.time.ZoneOffset.UTC));

    try {
      pasteCreatedRepository.save(event);
    } catch (IllegalArgumentException e) {
      return new CreateResult.Failure(new SystemErrorException("Failed to save paste event"));
    } catch (OptimisticLockingFailureException e) {
      return new CreateResult.Failure(new SystemErrorException("Failed to save paste event"));
    } catch (DataIntegrityViolationException e) {
      return new CreateResult.Failure(new SystemErrorException("Failed to save paste event"));
    }
    Optional<PasteUrls> urlsOpt = PasteUrls.from(baseUrl, pasteId);
    if (urlsOpt.isEmpty()) {
      return new CreateResult.Failure(new SystemErrorException("Failed to construct URLs"));
    }

    return new CreateResult.Success(
        new CreateSuccess(event.getPasteId(), urlsOpt.get(), createdAt, expiresAt));
  }
}
