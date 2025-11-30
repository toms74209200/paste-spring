package com.example.paste.create.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class PasteContentTest {

  @Property
  void parseWithValidContentThenReturnsPresent(
      @ForAll("validContent") String content,
      @ForAll("optionalTitle") String title,
      @ForAll("validExpiresIn") Integer expiresIn,
      @ForAll("optionalLanguage") String language) {

    Optional<PasteContent> result = PasteContent.parse(content, title, expiresIn, language);

    assertThat(result).isPresent();
    PasteContent pasteContent = result.get();
    assertThat(pasteContent.content()).isEqualTo(content);
    assertThat(pasteContent.title()).isEqualTo(title);
    assertThat(pasteContent.expiresIn()).isEqualTo(expiresIn);
    assertThat(pasteContent.language()).isEqualTo(language);
  }

  @Property
  void parseWithNullContentThenReturnsEmpty(@ForAll("validExpiresIn") Integer expiresIn) {

    Optional<PasteContent> result = PasteContent.parse(null, "Title", expiresIn, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithEmptyContentThenReturnsEmpty(@ForAll("validExpiresIn") Integer expiresIn) {

    Optional<PasteContent> result = PasteContent.parse("", "Title", expiresIn, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithBlankContentThenReturnsEmpty(
      @ForAll("blankContent") String content, @ForAll("validExpiresIn") Integer expiresIn) {

    Optional<PasteContent> result = PasteContent.parse(content, "Title", expiresIn, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithNullExpiresInThenReturnsEmpty(@ForAll("validContent") String content) {

    Optional<PasteContent> result = PasteContent.parse(content, "Title", null, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithZeroExpiresInThenReturnsEmpty(@ForAll("validContent") String content) {

    Optional<PasteContent> result = PasteContent.parse(content, "Title", 0, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithNegativeExpiresInThenReturnsEmpty(
      @ForAll("validContent") String content, @ForAll("negativeExpiresIn") Integer expiresIn) {

    Optional<PasteContent> result = PasteContent.parse(content, "Title", expiresIn, "javascript");

    assertThat(result).isEmpty();
  }

  @Property
  void parseWithExcessiveExpiresInThenReturnsEmpty(
      @ForAll("validContent") String content, @ForAll("excessiveExpiresIn") Integer expiresIn) {

    Optional<PasteContent> result = PasteContent.parse(content, "Title", expiresIn, "javascript");

    assertThat(result).isEmpty();
  }

  @Provide
  Arbitrary<String> validContent() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(1000);
  }

  @Provide
  Arbitrary<String> blankContent() {
    return Arbitraries.strings().whitespace().ofMinLength(1).ofMaxLength(10);
  }

  @Provide
  Arbitrary<Integer> validExpiresIn() {
    return Arbitraries.integers().greaterOrEqual(1).lessOrEqual(31536000);
  }

  @Provide
  Arbitrary<Integer> negativeExpiresIn() {
    return Arbitraries.integers().lessOrEqual(-1);
  }

  @Provide
  Arbitrary<Integer> excessiveExpiresIn() {
    return Arbitraries.integers().greaterOrEqual(31536001);
  }

  @Provide
  Arbitrary<String> optionalTitle() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100).injectNull(0.3);
  }

  @Provide
  Arbitrary<String> optionalLanguage() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20).injectNull(0.3);
  }
}
