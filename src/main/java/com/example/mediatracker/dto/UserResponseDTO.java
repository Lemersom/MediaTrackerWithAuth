package com.example.mediatracker.dto;

import com.example.mediatracker.enums.UserRole;

public record UserResponseDTO(
        Long id,
        String userName,
        UserRole role
) {
}
