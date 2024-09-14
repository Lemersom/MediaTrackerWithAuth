package com.example.mediatracker.dto;

import com.example.mediatracker.enums.MediaStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

public record MediaItemRecordDto(
        @NotBlank(message = "Title must not be null or empty")
        String title,
        @Range(min = 1, max = 10, message = "Rating must be between 1 and 10")
        Integer rating,
        LocalDate startDate,
        LocalDate finishDate,
        @NotNull(message = "Invalid status, must be one of WISHLIST, IN_PROGRESS, COMPLETED, ON_HOLD, DROPPED")
        MediaStatus status,
        @NotNull(message = "Invalid media type")
        Long mediaTypeId,
        String notes) {
}
