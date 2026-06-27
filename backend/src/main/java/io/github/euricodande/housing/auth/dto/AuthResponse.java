package io.github.euricodande.housing.auth.dto;

import io.github.euricodande.housing.common.enums.UserRole;

public record AuthResponse(
        Long userId,
        String email,
        UserRole role,
        String token
) {
}
