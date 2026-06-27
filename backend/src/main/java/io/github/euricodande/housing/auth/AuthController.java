package io.github.euricodande.housing.auth;

import io.github.euricodande.housing.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/student")
    public ResponseEntity<AuthResponse> registerStudent(
            @Valid @RequestBody RegisterStudentRequest request
    ) {
        return ResponseEntity.ok(authService.registerStudent(request));
    }

    @PostMapping("/register/landlord")
    public ResponseEntity<AuthResponse> registerLandlord(
            @Valid @RequestBody RegisterLandlordRequest request
    ) {
        return ResponseEntity.ok(authService.registerLandlord(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }
}
