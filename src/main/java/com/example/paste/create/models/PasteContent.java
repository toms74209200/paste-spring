package com.example.paste.create.models;

import java.util.Optional;

public record PasteContent(String content, String title, Integer expiresIn, String language) {
  private static final int MAX_EXPIRES_IN = 31536000;

  public static Optional<PasteContent> parse(
      String content, String title, Integer expiresIn, String language) {
    if (content == null || content.isBlank()) {
      return Optional.empty();
    }
    if (expiresIn == null || expiresIn <= 0 || expiresIn > MAX_EXPIRES_IN) {
      return Optional.empty();
    }
    return Optional.of(new PasteContent(content, title, expiresIn, language));
  }
}
