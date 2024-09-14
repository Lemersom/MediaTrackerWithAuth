package com.example.mediatracker.dto;

import jakarta.validation.constraints.NotBlank;

public record MediaTypeRecordDto(
        @NotBlank(message = "Media-Type name must not be null or empty")
        String name) {
}
