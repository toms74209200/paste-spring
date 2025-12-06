package com.example.paste.read.models;

import java.time.Instant;

public record HtmlSafePaste(
    String id,
    String title,
    String content,
    String language,
    Instant createdAt,
    Instant expiresAt) {}
