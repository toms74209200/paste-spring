package com.example.paste.create;

import com.example.paste.create.models.PasteUrls;
import java.time.Instant;

public record CreateSuccess(String id, PasteUrls urls, Instant createdAt, Instant expiresAt) {}
