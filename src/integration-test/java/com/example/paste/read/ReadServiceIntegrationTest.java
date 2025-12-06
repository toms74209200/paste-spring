package com.example.paste.read;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.paste.create.data.PasteCreated;
import com.example.paste.create.data.PasteCreatedRepository;
import com.example.paste.read.exceptions.NotFoundException;
import java.time.LocalDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReadServiceIntegrationTest {

  @Autowired private ReadService readService;

  @Autowired private PasteCreatedRepository repository;

  @Test
  void readAsHtmlWithExistingPasteThenReturnsSuccess() {
    String pasteId = RandomStringUtils.insecure().nextAlphanumeric(10);
    String content = RandomStringUtils.insecure().nextAlphanumeric(10);
    String title = RandomStringUtils.insecure().nextAlphanumeric(10);
    String language = "java";
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expiresAt = createdAt.plusDays(1);

    repository.save(new PasteCreated(pasteId, content, title, language, createdAt, expiresAt));

    ReadHtmlResult result = readService.readAsHtml(pasteId);

    assertThat(result).isInstanceOf(ReadHtmlResult.Success.class);
    ReadHtmlResult.Success success = (ReadHtmlResult.Success) result;
    assertThat(success.value()).contains(content);
    assertThat(success.value()).contains(title);
  }

  @Test
  void readAsHtmlWithNonExistentPasteThenReturnsFailure() {
    String nonExistentId = RandomStringUtils.insecure().nextAlphanumeric(10);

    ReadHtmlResult result = readService.readAsHtml(nonExistentId);

    assertThat(result).isInstanceOf(ReadHtmlResult.Failure.class);
    ReadHtmlResult.Failure failure = (ReadHtmlResult.Failure) result;
    assertThat(failure.exception()).isInstanceOf(NotFoundException.class);
    assertThat(failure.exception().getMessage()).isEqualTo("Paste not found");
  }

  @Test
  void readAsHtmlWithExpiredPasteThenReturnsFailure() {
    String pasteId = RandomStringUtils.insecure().nextAlphanumeric(10);
    String content = RandomStringUtils.insecure().nextAlphanumeric(10);
    LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
    LocalDateTime expiresAt = LocalDateTime.now().minusDays(1);

    PasteCreated paste = new PasteCreated(pasteId, content, null, null, createdAt, expiresAt);
    repository.save(paste);

    ReadHtmlResult result = readService.readAsHtml(pasteId);

    assertThat(result).isInstanceOf(ReadHtmlResult.Failure.class);
    ReadHtmlResult.Failure failure = (ReadHtmlResult.Failure) result;
    assertThat(failure.exception()).isInstanceOf(NotFoundException.class);
    assertThat(failure.exception().getMessage()).isEqualTo("Paste not found");
  }

  @Test
  void readAsJsonWithExistingPasteThenReturnsSuccess() {
    String pasteId = RandomStringUtils.insecure().nextAlphanumeric(10);
    String content = RandomStringUtils.insecure().nextAlphanumeric(10);
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expiresAt = createdAt.plusHours(1);

    PasteCreated paste = new PasteCreated(pasteId, content, null, null, createdAt, expiresAt);
    repository.save(paste);

    ReadJsonResult result = readService.readAsJson(pasteId);

    assertThat(result).isInstanceOf(ReadJsonResult.Success.class);
    ReadJsonResult.Success success = (ReadJsonResult.Success) result;
    assertThat(success.value().getId()).isEqualTo(pasteId);
    assertThat(success.value().getContent()).isEqualTo(content);
    assertThat(success.value().getTitle().isPresent()).isFalse();
    assertThat(success.value().getLanguage().isPresent()).isFalse();
  }

  @Test
  void readAsRawMultipleTimesThenReturnsConsistentResults() {
    String pasteId = RandomStringUtils.insecure().nextAlphanumeric(10);
    String content = RandomStringUtils.insecure().nextAlphanumeric(10);
    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime expiresAt = createdAt.plusDays(7);

    PasteCreated paste =
        new PasteCreated(
            pasteId,
            content,
            RandomStringUtils.insecure().nextAlphanumeric(10),
            "python",
            createdAt,
            expiresAt);
    repository.save(paste);

    ReadRawResult result1 = readService.readAsRaw(pasteId);
    ReadRawResult result2 = readService.readAsRaw(pasteId);

    assertThat(result1).isInstanceOf(ReadRawResult.Success.class);
    assertThat(result2).isInstanceOf(ReadRawResult.Success.class);

    String content1 = ((ReadRawResult.Success) result1).value();
    String content2 = ((ReadRawResult.Success) result2).value();

    assertThat(content1).isEqualTo(content2);
    assertThat(content1).isEqualTo(content);
  }
}
