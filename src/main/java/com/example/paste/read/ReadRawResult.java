package com.example.paste.read;

public sealed interface ReadRawResult permits ReadRawResult.Success, ReadRawResult.Failure {
  record Success(String value) implements ReadRawResult {}

  record Failure(RuntimeException exception) implements ReadRawResult {}
}
