package com.smsrz.url_shortener.UrlController.DTOs;

import jakarta.validation.constraints.NotBlank;

public record CreateShortUrlForm(
@NotBlank(message = "Original Url is required")
        String originalUrl) {
}
