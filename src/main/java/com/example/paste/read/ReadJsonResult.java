package com.example.paste.read;

import com.example.paste.model.GetPasteByIdJson200Response;

public sealed interface ReadJsonResult permits ReadJsonResult.Success, ReadJsonResult.Failure {
  record Success(GetPasteByIdJson200Response value) implements ReadJsonResult {}

  record Failure(RuntimeException exception) implements ReadJsonResult {}
}
