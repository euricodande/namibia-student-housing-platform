package io.github.euricodande.housing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterStudentRequest(
        @NotBlank @Size(max = 40)
        String firstName,

        @Size(max = 40)
        String middleName,

        @NotBlank @Size(max = 40)
        String lastName,

        @NotBlank @Email @Size(max = 255)
        String email,

        @NotBlank @Size(min = 8, max = 100)
        String password,

        @Size(max = 20)
        String phoneNumber,

        @NotBlank @Size(max = 20)
        String studentNumber,

        @NotBlank @Size(max = 70)
        String institutionName,

        @Size(max = 100)
        String preferredArea
) {
}
