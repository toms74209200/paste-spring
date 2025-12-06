package com.example.paste.read.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

class HtmlRendererTest {

  @Property
  void renderWithAnyPasteThenReplacesAllPlaceholders(
      @ForAll("safeString") String id,
      @ForAll("safeString") String title,
      @ForAll("safeString") String content,
      @ForAll("safeString") String language,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    String template = "{{title}}|{{id}}|{{languageMeta}}|{{createdAt}}|{{expiresAt}}|{{content}}";
    HtmlSafePaste paste = new HtmlSafePaste(id, title, content, language, createdAt, expiresAt);

    String result = HtmlRenderer.render(template, paste);

    assertThat(result).doesNotContain("{{title}}");
    assertThat(result).doesNotContain("{{id}}");
    assertThat(result).doesNotContain("{{languageMeta}}");
    assertThat(result).doesNotContain("{{createdAt}}");
    assertThat(result).doesNotContain("{{expiresAt}}");
    assertThat(result).doesNotContain("{{content}}");
    assertThat(result).contains(title);
    assertThat(result).contains(id);
    assertThat(result).contains(content);
  }

  @Property
  void renderWithAnyPasteThenPreservesNonPlaceholderContent(
      @ForAll("safeString") String id,
      @ForAll("safeString") String title,
      @ForAll("safeString") String content,
      @ForAll("safeString") String language,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    String template = "PREFIX {{title}} MIDDLE {{content}} SUFFIX";
    HtmlSafePaste paste = new HtmlSafePaste(id, title, content, language, createdAt, expiresAt);

    String result = HtmlRenderer.render(template, paste);

    assertThat(result).startsWith("PREFIX ");
    assertThat(result).contains(" MIDDLE ");
    assertThat(result).endsWith(" SUFFIX");
  }

  @Property
  void renderWithEmptyLanguageThenOmitsLanguageMeta(
      @ForAll("safeString") String id,
      @ForAll("safeString") String title,
      @ForAll("safeString") String content,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    String template = "{{languageMeta}}";
    HtmlSafePaste paste = new HtmlSafePaste(id, title, content, "", createdAt, expiresAt);

    String result = HtmlRenderer.render(template, paste);

    assertThat(result).isEmpty();
  }

  @Property
  void renderWithNonEmptyLanguageThenIncludesLanguageMeta(
      @ForAll("safeString") String id,
      @ForAll("safeString") String title,
      @ForAll("safeString") String content,
      @ForAll("safeString") String language,
      @ForAll("timestamp") Instant createdAt,
      @ForAll("timestamp") Instant expiresAt) {

    String template = "{{languageMeta}}";
    HtmlSafePaste paste = new HtmlSafePaste(id, title, content, language, createdAt, expiresAt);

    String result = HtmlRenderer.render(template, paste);

    assertThat(result).isEqualTo(" | Language: " + language);
  }

  @Provide
  Arbitrary<String> safeString() {
    return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
  }

  @Provide
  Arbitrary<Instant> timestamp() {
    return Arbitraries.longs()
        .between(0, Instant.now().getEpochSecond())
        .map(Instant::ofEpochSecond);
  }
}
