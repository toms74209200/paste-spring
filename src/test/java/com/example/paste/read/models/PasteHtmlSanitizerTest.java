package com.example.paste.read.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class PasteHtmlSanitizerTest {

  @Property
  void sanitizeWithLessThanThenEscapesToAmpLt(@ForAll("stringWithLessThan") String input) {
    ValidatedPaste paste =
        new ValidatedPaste(
            "id", input, "title", "lang", Instant.EPOCH, Instant.EPOCH.plusSeconds(3600));

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    long inputCount = input.chars().filter(ch -> ch == '<').count();
    long outputCount = countOccurrences(result.content(), "&lt;");
    assertThat(outputCount).isEqualTo(inputCount);
  }

  @Property
  void sanitizeWithGreaterThanThenEscapesToAmpGt(@ForAll("stringWithGreaterThan") String input) {
    ValidatedPaste paste =
        new ValidatedPaste(
            "id", input, "title", "lang", Instant.EPOCH, Instant.EPOCH.plusSeconds(3600));

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    long inputCount = input.chars().filter(ch -> ch == '>').count();
    long outputCount = countOccurrences(result.content(), "&gt;");
    assertThat(outputCount).isEqualTo(inputCount);
  }

  @Property
  void sanitizeWithAmpersandThenEscapesToAmpAmp(@ForAll("stringWithAmpersand") String input) {
    ValidatedPaste paste =
        new ValidatedPaste(
            "id", input, "title", "lang", Instant.EPOCH, Instant.EPOCH.plusSeconds(3600));

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    long inputCount = input.chars().filter(ch -> ch == '&').count();
    long outputCount = countOccurrences(result.content(), "&amp;");
    assertThat(outputCount).isEqualTo(inputCount);
  }

  @Property
  void sanitizeWithDoubleQuoteThenEscapesToAmpQuot(@ForAll("stringWithDoubleQuote") String input) {
    ValidatedPaste paste =
        new ValidatedPaste(
            "id", input, "title", "lang", Instant.EPOCH, Instant.EPOCH.plusSeconds(3600));

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    long inputCount = input.chars().filter(ch -> ch == '"').count();
    long outputCount = countOccurrences(result.content(), "&quot;");
    assertThat(outputCount).isEqualTo(inputCount);
  }

  @Property
  void sanitizeWithSingleQuoteThenEscapesToAmpX27(@ForAll("stringWithSingleQuote") String input) {
    ValidatedPaste paste =
        new ValidatedPaste(
            "id", input, "title", "lang", Instant.EPOCH, Instant.EPOCH.plusSeconds(3600));

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    long inputCount = input.chars().filter(ch -> ch == '\'').count();
    long outputCount = countOccurrences(result.content(), "&#x27;");
    assertThat(outputCount).isEqualTo(inputCount);
  }

  private long countOccurrences(String str, String substring) {
    int count = 0;
    int index = 0;
    while ((index = str.indexOf(substring, index)) != -1) {
      count++;
      index += substring.length();
    }
    return count;
  }

  @Property
  void sanitizeWithNullTitleThenReturnsUntitled(
      @ForAll("pasteId") String id,
      @ForAll("content") String content,
      @ForAll("optionalLanguage") String language,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    ValidatedPaste paste = new ValidatedPaste(id, content, null, language, createdAt, expiresAt);

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    assertThat(result.title()).isEqualTo("Untitled");
  }

  @Property
  void sanitizeWithNullLanguageThenReturnsEmptyString(
      @ForAll("pasteId") String id,
      @ForAll("content") String content,
      @ForAll("optionalTitle") String title,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    ValidatedPaste paste = new ValidatedPaste(id, content, title, null, createdAt, expiresAt);

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    assertThat(result.language()).isEmpty();
  }

  @Property
  void sanitizeWithAnyPasteThenPreservesTimestamps(
      @ForAll("pasteId") String id,
      @ForAll("content") String content,
      @ForAll("optionalTitle") String title,
      @ForAll("optionalLanguage") String language,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    ValidatedPaste paste = new ValidatedPaste(id, content, title, language, createdAt, expiresAt);

    HtmlSafePaste result = PasteHtmlSanitizer.sanitize(paste);

    assertThat(result.createdAt()).isEqualTo(createdAt);
    assertThat(result.expiresAt()).isEqualTo(expiresAt);
  }

  @Provide
  Arbitrary<String> stringWithLessThan() {
    return Arbitraries.strings().ascii().ofMinLength(0).ofMaxLength(50).map(s -> s + "<" + s);
  }

  @Provide
  Arbitrary<String> stringWithGreaterThan() {
    return Arbitraries.strings().ascii().ofMinLength(0).ofMaxLength(50).map(s -> s + ">" + s);
  }

  @Provide
  Arbitrary<String> stringWithAmpersand() {
    return Arbitraries.strings().ascii().ofMinLength(0).ofMaxLength(50).map(s -> s + "&" + s);
  }

  @Provide
  Arbitrary<String> stringWithDoubleQuote() {
    return Arbitraries.strings().ascii().ofMinLength(0).ofMaxLength(50).map(s -> s + "\"" + s);
  }

  @Provide
  Arbitrary<String> stringWithSingleQuote() {
    return Arbitraries.strings().ascii().ofMinLength(0).ofMaxLength(50).map(s -> s + "'" + s);
  }

  @Provide
  Arbitrary<String> pasteId() {
    return Arbitraries.strings().withCharRange('a', 'z').ofMinLength(1).ofMaxLength(32);
  }

  @Provide
  Arbitrary<String> content() {
    return Arbitraries.strings().ascii().ofMinLength(1).ofMaxLength(200);
  }

  @Provide
  Arbitrary<String> optionalTitle() {
    return Arbitraries.strings().ascii().ofMinLength(1).ofMaxLength(100).injectNull(0.3);
  }

  @Provide
  Arbitrary<String> optionalLanguage() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).injectNull(0.3);
  }

  @Provide
  Arbitrary<Instant> timestamp() {
    return Arbitraries.longs()
        .between(0, Instant.now().getEpochSecond())
        .map(Instant::ofEpochSecond);
  }
}
