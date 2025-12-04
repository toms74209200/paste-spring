package com.example.paste.read.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.paste.create.data.PasteCreated;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.Test;

class ValidatedPasteTest {

  @Property
  void fromWithValidPasteThenReturnsPresent(
      @ForAll("pasteId") String id,
      @ForAll("content") String content,
      @ForAll("optionalTitle") String title,
      @ForAll("optionalLanguage") String language,
      @ForAll("futureDateTime") LocalDateTime expiresAt) {

    LocalDateTime createdAt = LocalDateTime.now();
    PasteCreated paste = new PasteCreated(id, content, title, language, createdAt, expiresAt);

    Optional<ValidatedPaste> result = ValidatedPaste.from(Optional.of(paste), Instant.now());

    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(id);
    assertThat(result.get().content()).isEqualTo(content);
    assertThat(result.get().title()).isEqualTo(title);
    assertThat(result.get().language()).isEqualTo(language);
  }

  @Property
  void fromWithExpiredPasteThenReturnsEmpty(
      @ForAll("pasteId") String id,
      @ForAll("content") String content,
      @ForAll("pastDateTime") LocalDateTime expiresAt) {

    LocalDateTime createdAt = expiresAt.minusDays(1);
    PasteCreated paste = new PasteCreated(id, content, null, null, createdAt, expiresAt);

    Optional<ValidatedPaste> result = ValidatedPaste.from(Optional.of(paste), Instant.now());

    assertThat(result).isEmpty();
  }

  @Test
  void fromWithNonExistentPasteThenReturnsEmpty() {
    Optional<ValidatedPaste> result = ValidatedPaste.from(Optional.empty(), Instant.now());

    assertThat(result).isEmpty();
  }

  @Provide
  Arbitrary<String> pasteId() {
    return Arbitraries.strings().alpha().ofLength(10);
  }

  @Provide
  Arbitrary<String> content() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100);
  }

  @Provide
  Arbitrary<String> optionalTitle() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50).injectNull(0.3);
  }

  @Provide
  Arbitrary<String> optionalLanguage() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).injectNull(0.3);
  }

  @Provide
  Arbitrary<LocalDateTime> futureDateTime() {
    return Arbitraries.integers().between(1, 365).map(days -> LocalDateTime.now().plusDays(days));
  }

  @Provide
  Arbitrary<LocalDateTime> pastDateTime() {
    return Arbitraries.integers().between(1, 365).map(days -> LocalDateTime.now().minusDays(days));
  }
}
