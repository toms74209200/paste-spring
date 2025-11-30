package com.example.paste.create.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "paste_created")
public class PasteCreated {

  @Id
  @Column(length = 32)
  private String pasteId;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Column(length = 255)
  private String title;

  @Column(length = 50)
  private String language;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime expiresAt;

  protected PasteCreated() {}

  public PasteCreated(
      String pasteId,
      String content,
      String title,
      String language,
      LocalDateTime createdAt,
      LocalDateTime expiresAt) {
    this.pasteId = pasteId;
    this.content = content;
    this.title = title;
    this.language = language;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
  }

  public String getPasteId() {
    return pasteId;
  }

  public String getContent() {
    return content;
  }

  public String getTitle() {
    return title;
  }

  public String getLanguage() {
    return language;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }
}
