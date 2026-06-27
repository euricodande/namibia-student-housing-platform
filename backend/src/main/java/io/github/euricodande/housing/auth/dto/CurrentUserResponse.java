package io.github.euricodande.housing.auth.dto;

import io.github.euricodande.housing.common.enums.UserRole;

public record CurrentUserResponse(
        Long userId,
        String firstName,
        String lastName,
        String email,
        UserRole role,
        Boolean active
) {
}
