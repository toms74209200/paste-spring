package com.example.paste.read.models;

public class HtmlRenderer {

  private HtmlRenderer() {}

  public static String render(String template, HtmlSafePaste paste) {
    String languageMeta = paste.language().isEmpty() ? "" : " | Language: " + paste.language();

    return template
        .replace("{{title}}", paste.title())
        .replace("{{id}}", paste.id())
        .replace("{{languageMeta}}", languageMeta)
        .replace("{{createdAt}}", paste.createdAt().toString())
        .replace("{{expiresAt}}", paste.expiresAt().toString())
        .replace("{{content}}", paste.content());
  }
}
