package com.example.paste.create;

public sealed interface CreateResult permits CreateResult.Success, CreateResult.Failure {
  record Success(CreateSuccess value) implements CreateResult {}

  record Failure(RuntimeException exception) implements CreateResult {}
}
