package com.example.paste.read;

public sealed interface ReadHtmlResult permits ReadHtmlResult.Success, ReadHtmlResult.Failure {
  record Success(String value) implements ReadHtmlResult {}

  record Failure(RuntimeException exception) implements ReadHtmlResult {}
}
