package com.smsrz.url_shortener.Model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CreateShortUrlCmd(String originalUrl,
                                Boolean isPrivate,
                                @Min(1)
                                @Max(30)
                                Integer expirationInDays,
                                Long userId) {
                                }
