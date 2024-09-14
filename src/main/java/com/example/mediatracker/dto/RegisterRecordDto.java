package com.example.mediatracker.dto;

import com.example.mediatracker.enums.UserRole;

public record RegisterRecordDto(
        String userName,
        String password,
        UserRole role
) {
}
