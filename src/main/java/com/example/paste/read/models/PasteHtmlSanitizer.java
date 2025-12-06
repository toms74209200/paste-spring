package com.example.paste.read.models;

public class PasteHtmlSanitizer {

  private PasteHtmlSanitizer() {}

  public static HtmlSafePaste sanitize(ValidatedPaste paste) {
    String title = paste.title() != null ? paste.title() : "Untitled";
    String language = paste.language() != null ? paste.language() : "";

    return new HtmlSafePaste(
        escapeHtml(paste.id()),
        escapeHtml(title),
        escapeHtml(paste.content()),
        escapeHtml(language),
        paste.createdAt(),
        paste.expiresAt());
  }

  private static String escapeHtml(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;");
  }
}
