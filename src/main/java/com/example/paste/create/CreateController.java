package com.example.paste.create;

import com.example.paste.api.CreateApi;
import com.example.paste.create.exceptions.InvalidInputException;
import com.example.paste.model.PostPastes201Response;
import com.example.paste.model.PostPastesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreateController implements CreateApi {

  private final CreateService createService;

  public CreateController(CreateService createService) {
    this.createService = createService;
  }

  private final Logger logger = LoggerFactory.getLogger(CreateController.class);

  @Override
  public ResponseEntity<PostPastes201Response> postPastes(PostPastesRequest request) {
    CreateResult result = createService.create(request);

    return switch (result) {
      case CreateResult.Success success ->
          ResponseEntity.status(201)
              .body(
                  new PostPastes201Response(
                      success.value().id(),
                      success.value().urls().url().toString(),
                      success.value().urls().htmlUrl().toString(),
                      success.value().urls().rawUrl().toString(),
                      success.value().urls().jsonUrl().toString(),
                      success.value().createdAt().atOffset(java.time.ZoneOffset.UTC),
                      success.value().expiresAt().atOffset(java.time.ZoneOffset.UTC)));
      case CreateResult.Failure failure -> throw failure.exception();
    };
  }

  @ExceptionHandler({
    InvalidInputException.class,
    HttpMessageNotReadableException.class,
    MethodArgumentNotValidException.class
  })
  public ResponseEntity<Void> handleBadRequest(Exception e) {
    logger.info("Invalid request", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handleException(Exception e) {
    logger.warn("An unexpected error occurred", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
