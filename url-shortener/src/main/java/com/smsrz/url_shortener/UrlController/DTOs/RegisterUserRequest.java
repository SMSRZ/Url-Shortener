package com.smsrz.url_shortener.UrlController.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid Format")
        String email,
        @NotBlank(message = "Enter password")
        String password,
        @NotBlank(message = "Enter Name")
        String name) {
}
