package com.example.paste.create;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.paste.create.data.PasteCreated;
import com.example.paste.create.data.PasteCreatedRepository;
import com.example.paste.create.exceptions.InvalidInputException;
import com.example.paste.model.PastesPostRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CreateServiceIntegrationTest {

  @Autowired private CreateService createService;

  @Autowired private PasteCreatedRepository eventStore;

  @Test
  void createWithValidRequestThenSavesToDatabase() {
    String expectedContent = "console.log('Hello World');";
    String expectedTitle = "My Code Snippet";
    String expectedLanguage = "javascript";

    PastesPostRequest request = new PastesPostRequest();
    request.setContent(expectedContent);
    request.setTitle(expectedTitle);
    request.setExpiresIn(86400);
    request.setLanguage(expectedLanguage);

    CreateResult result = createService.create(request);

    assertThat(result).isInstanceOf(CreateResult.Success.class);
    CreateResult.Success success = (CreateResult.Success) result;

    assertThat(success.value()).isNotNull();
    assertThat(success.value().id()).isNotBlank();
    assertThat(success.value().id()).hasSize(32);
    assertThat(success.value().urls().url().toString()).contains(success.value().id());
    assertThat(success.value().urls().htmlUrl().toString()).contains(success.value().id());
    assertThat(success.value().urls().rawUrl().toString()).contains(success.value().id());
    assertThat(success.value().urls().jsonUrl().toString()).contains(success.value().id());
    assertThat(success.value().createdAt()).isNotNull();
    assertThat(success.value().expiresAt()).isNotNull();
    assertThat(success.value().expiresAt()).isAfter(success.value().createdAt());

    PasteCreated saved = eventStore.findById(success.value().id()).orElseThrow();
    assertThat(saved.getPasteId()).isEqualTo(success.value().id());
    assertThat(saved.getContent()).isEqualTo(expectedContent);
    assertThat(saved.getTitle()).isEqualTo(expectedTitle);
    assertThat(saved.getLanguage()).isEqualTo(expectedLanguage);
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getExpiresAt()).isNotNull();
    assertThat(saved.getExpiresAt()).isAfter(saved.getCreatedAt());
  }

  @Test
  void createWithMinimalRequestThenSavesToDatabase() {
    String expectedContent = "Test content";

    PastesPostRequest request = new PastesPostRequest();
    request.setContent(expectedContent);
    request.setExpiresIn(3600);
    CreateResult result = createService.create(request);

    assertThat(result).isInstanceOf(CreateResult.Success.class);
    CreateResult.Success success = (CreateResult.Success) result;

    assertThat(success.value()).isNotNull();
    assertThat(success.value().id()).isNotBlank();

    PasteCreated saved = eventStore.findById(success.value().id()).orElseThrow();
    assertThat(saved.getContent()).isEqualTo(expectedContent);
    assertThat(saved.getTitle()).isNull();
    assertThat(saved.getLanguage()).isNull();
  }

  @Test
  void createWithInvalidContentThenReturnsFailure() {
    PastesPostRequest request = new PastesPostRequest();
    request.setContent("");
    request.setExpiresIn(3600);

    CreateResult result = createService.create(request);

    assertThat(result).isInstanceOf(CreateResult.Failure.class);
    CreateResult.Failure failure = (CreateResult.Failure) result;
    assertThat(failure.exception()).isInstanceOf(InvalidInputException.class);
  }

  @Test
  void createWithNullExpiresInThenReturnsFailure() {
    PastesPostRequest request = new PastesPostRequest();
    request.setContent("content");
    request.setExpiresIn(null);

    CreateResult result = createService.create(request);

    assertThat(result).isInstanceOf(CreateResult.Failure.class);
    CreateResult.Failure failure = (CreateResult.Failure) result;
    assertThat(failure.exception()).isInstanceOf(InvalidInputException.class);
  }

  @Test
  void createMultipleTimesThenGeneratesUniqueIds() {
    PastesPostRequest request1 = new PastesPostRequest();
    request1.setContent("First paste");
    request1.setExpiresIn(3600);

    PastesPostRequest request2 = new PastesPostRequest();
    request2.setContent("Second paste");
    request2.setExpiresIn(3600);

    CreateResult result1 = createService.create(request1);
    CreateResult result2 = createService.create(request2);

    assertThat(result1).isInstanceOf(CreateResult.Success.class);
    assertThat(result2).isInstanceOf(CreateResult.Success.class);

    String id1 = ((CreateResult.Success) result1).value().id();
    String id2 = ((CreateResult.Success) result2).value().id();

    assertThat(id1).isNotEqualTo(id2);
    assertThat(eventStore.findById(id1)).isPresent();
    assertThat(eventStore.findById(id2)).isPresent();
  }
}
