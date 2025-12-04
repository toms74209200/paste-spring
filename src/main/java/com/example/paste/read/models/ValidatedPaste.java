package com.example.paste.read.models;

import com.example.paste.create.data.PasteCreated;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

public record ValidatedPaste(
    String id,
    String content,
    String title,
    String language,
    Instant createdAt,
    Instant expiresAt) {

  public static Optional<ValidatedPaste> from(Optional<PasteCreated> pasteOpt, Instant now) {
    return pasteOpt.flatMap(
        paste -> {
          if (now.isAfter(paste.getExpiresAt().toInstant(ZoneOffset.UTC))) {
            return Optional.empty();
          }

          return Optional.of(
              new ValidatedPaste(
                  paste.getPasteId(),
                  paste.getContent(),
                  paste.getTitle(),
                  paste.getLanguage(),
                  paste.getCreatedAt().toInstant(ZoneOffset.UTC),
                  paste.getExpiresAt().toInstant(ZoneOffset.UTC)));
        });
  }
}
