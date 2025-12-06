package com.example.paste.read;

import com.example.paste.create.data.PasteCreatedRepository;
import com.example.paste.model.GetPasteByIdJson200Response;
import com.example.paste.read.data.HtmlTemplateLoader;
import com.example.paste.read.exceptions.NotFoundException;
import com.example.paste.read.models.HtmlRenderer;
import com.example.paste.read.models.HtmlSafePaste;
import com.example.paste.read.models.PasteHtmlSanitizer;
import com.example.paste.read.models.ValidatedPaste;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.time.Instant;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;

@Service
public class ReadService {

  private final PasteCreatedRepository pasteCreatedRepository;

  public ReadService(PasteCreatedRepository pasteCreatedRepository) {
    this.pasteCreatedRepository = pasteCreatedRepository;
  }

  @WithSpan
  public ReadHtmlResult readAsHtml(String id) {
    return ValidatedPaste.from(pasteCreatedRepository.findById(id), Instant.now())
        .<ReadHtmlResult>map(
            paste -> {
              HtmlSafePaste safePaste = PasteHtmlSanitizer.sanitize(paste);
              String template = HtmlTemplateLoader.getPasteTemplate();
              String html = HtmlRenderer.render(template, safePaste);
              return new ReadHtmlResult.Success(html);
            })
        .orElse(new ReadHtmlResult.Failure(new NotFoundException("Paste not found")));
  }

  @WithSpan
  public ReadJsonResult readAsJson(String id) {
    return ValidatedPaste.from(pasteCreatedRepository.findById(id), Instant.now())
        .<ReadJsonResult>map(
            paste -> {
              GetPasteByIdJson200Response response =
                  new GetPasteByIdJson200Response(
                      paste.id(),
                      paste.content(),
                      paste.createdAt().atOffset(ZoneOffset.UTC),
                      paste.expiresAt().atOffset(ZoneOffset.UTC));
              if (paste.title() != null) {
                response.title(paste.title());
              }
              if (paste.language() != null) {
                response.language(paste.language());
              }
              return new ReadJsonResult.Success(response);
            })
        .orElse(new ReadJsonResult.Failure(new NotFoundException("Paste not found")));
  }

  @WithSpan
  public ReadRawResult readAsRaw(String id) {
    return ValidatedPaste.from(pasteCreatedRepository.findById(id), Instant.now())
        .<ReadRawResult>map(paste -> new ReadRawResult.Success(paste.content()))
        .orElse(new ReadRawResult.Failure(new NotFoundException("Paste not found")));
  }
}
