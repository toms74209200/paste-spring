package com.example.paste.read.data;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;

public class HtmlTemplateLoader {

  private static final String PASTE_TEMPLATE;

  static {
    try (InputStream stream = new ClassPathResource("templates/paste.html").getInputStream()) {
      PASTE_TEMPLATE = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load template: templates/paste.html", e);
    }
  }

  private HtmlTemplateLoader() {}

  public static String getPasteTemplate() {
    return PASTE_TEMPLATE;
  }
}
