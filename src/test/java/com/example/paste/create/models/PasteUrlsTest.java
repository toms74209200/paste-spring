package com.example.paste.create.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

class PasteUrlsTest {

  private static final URI BASE_URL = URI.create("http://localhost:8080");

  @Property
  void fromWithValidBaseUrlAndIdThenReturnsPresent(@ForAll("validPasteId") String pasteId) {

    Optional<PasteUrls> result = PasteUrls.from(BASE_URL, pasteId);

    assertThat(result).isPresent();
    PasteUrls urls = result.get();
    assertThat(urls.url().toString()).isEqualTo("http://localhost:8080/pastes/" + pasteId);
    assertThat(urls.htmlUrl().toString())
        .isEqualTo("http://localhost:8080/pastes/" + pasteId + ".html");
    assertThat(urls.rawUrl().toString())
        .isEqualTo("http://localhost:8080/pastes/" + pasteId + "/raw");
    assertThat(urls.jsonUrl().toString())
        .isEqualTo("http://localhost:8080/pastes/" + pasteId + ".json");
  }

  @Property
  void fromWithPasteIdContainingSpacesThenReturnsEmpty(
      @ForAll("pasteIdWithSpaces") String pasteId) {

    Optional<PasteUrls> result = PasteUrls.from(BASE_URL, pasteId);

    assertThat(result).isEmpty();
  }

  @Property
  void fromWithPasteIdContainingInvalidCharactersThenReturnsEmpty(
      @ForAll("pasteIdWithInvalidChars") String pasteId) {

    Optional<PasteUrls> result = PasteUrls.from(BASE_URL, pasteId);

    assertThat(result).isEmpty();
  }

  @Test
  void fromWithEmptyPasteIdThenReturnsPresent() {

    Optional<PasteUrls> result = PasteUrls.from(BASE_URL, "");

    assertThat(result).isPresent();
  }

  @Property
  void fromWithPasteIdContainingSlashesThenReturnsPresent(
      @ForAll("pasteIdWithSlashes") String pasteId) {

    Optional<PasteUrls> result = PasteUrls.from(BASE_URL, pasteId);

    assertThat(result).isPresent();
  }

  @Provide
  Arbitrary<String> validPasteId() {
    return Arbitraries.strings()
        .withCharRange('a', 'z')
        .withCharRange('A', 'Z')
        .withCharRange('0', '9')
        .withChars('-', '_')
        .ofMinLength(1)
        .ofMaxLength(50);
  }

  @Provide
  Arbitrary<String> pasteIdWithSpaces() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).map(s -> s + " " + s);
  }

  @Provide
  Arbitrary<String> pasteIdWithInvalidChars() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).map(s -> s + "{" + s + "}");
  }

  @Provide
  Arbitrary<String> pasteIdWithSlashes() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).map(s -> s + "/" + s);
  }
}
