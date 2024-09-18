package com.example.mediatracker.dto;

import com.example.mediatracker.enums.UserRole;

public record RegisterDTO(
        String userName,
        String password,
        UserRole role
) {
}
