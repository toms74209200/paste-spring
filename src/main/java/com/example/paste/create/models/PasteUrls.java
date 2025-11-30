package com.example.paste.create.models;

import java.net.URI;
import java.util.List;
import java.util.Optional;

public record PasteUrls(URI url, URI htmlUrl, URI rawUrl, URI jsonUrl) {
  private static final String PASTES_PATH = "/pastes/";
  private static final List<String> SUFFIXES = List.of("", ".html", "/raw", ".json");

  public static Optional<PasteUrls> from(URI baseUrl, String pasteId) {
    List<Optional<URI>> uris =
        SUFFIXES.stream()
            .map(suffix -> safeCreateUri(baseUrl.toString() + PASTES_PATH + pasteId + suffix))
            .toList();

    if (uris.stream().anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    }

    return Optional.of(
        new PasteUrls(uris.get(0).get(), uris.get(1).get(), uris.get(2).get(), uris.get(3).get()));
  }

  private static Optional<URI> safeCreateUri(String uri) {
    try {
      return Optional.of(URI.create(uri));
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }
}
