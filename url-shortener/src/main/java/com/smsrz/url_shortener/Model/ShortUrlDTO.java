package com.smsrz.url_shortener.Model;

import java.io.Serializable;
import java.time.Instant;

public record ShortUrlDTO(Long id, String shortKey,String originalUrl, Boolean isPrivate, Instant expiresAt,
                          UserDTO createdBy,long clickCount,Instant createdAt) implements Serializable {
}
