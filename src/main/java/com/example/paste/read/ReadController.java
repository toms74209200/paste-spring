package com.example.paste.read;

import com.example.paste.api.ReadApi;
import com.example.paste.model.GetPasteByIdJson200Response;
import com.example.paste.read.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReadController implements ReadApi {

  private final ReadService readService;
  private final Logger logger = LoggerFactory.getLogger(ReadController.class);

  public ReadController(ReadService readService) {
    this.readService = readService;
  }

  @Override
  public ResponseEntity<String> getPasteById(String id) {
    throw new RuntimeException("Not implemented yet");
  }

  @Override
  public ResponseEntity<String> getPasteByIdHtml(String id) {
    throw new RuntimeException("Not implemented yet");
  }

  @Override
  public ResponseEntity<String> getPasteByIdRaw(String id) {
    ReadRawResult result = readService.readAsRaw(id);

    return switch (result) {
      case ReadRawResult.Success success ->
          ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(success.value());
      case ReadRawResult.Failure failure -> throw failure.exception();
    };
  }

  @Override
  public ResponseEntity<GetPasteByIdJson200Response> getPasteByIdJson(String id) {
    ReadJsonResult result = readService.readAsJson(id);

    return switch (result) {
      case ReadJsonResult.Success success -> ResponseEntity.ok(success.value());
      case ReadJsonResult.Failure failure -> throw failure.exception();
    };
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Void> handleNotFound(NotFoundException e) {
    logger.info("Paste not found", e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handleException(Exception e) {
    logger.warn("An unexpected error occurred", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
  }
}
