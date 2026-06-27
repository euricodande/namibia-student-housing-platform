package io.github.euricodande.housing.auth;

import io.github.euricodande.housing.auth.dto.*;
import io.github.euricodande.housing.common.enums.UserRole;
import io.github.euricodande.housing.landlord.LandlordProfile;
import io.github.euricodande.housing.landlord.LandlordProfileRepository;
import io.github.euricodande.housing.security.JwtService;
import io.github.euricodande.housing.student.StudentProfile;
import io.github.euricodande.housing.student.StudentProfileRepository;
import io.github.euricodande.housing.user.AppUser;
import io.github.euricodande.housing.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final LandlordProfileRepository landlordProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /*
     * Registers a new student account.
     *
     * @Transactional ensures that the user and student profile are saved together.
     * If one insert fails, the whole registration is rolled back.
     */
    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        String email = normalizeEmail(request.email());

        validateEmailAndPhone(email, request.phoneNumber());

        if (studentProfileRepository.existsByStudentNumber(request.studentNumber())) {
            throw new IllegalArgumentException("Student number already exists");
        }

        /*
         * Create the base user account.
         * Passwords are never stored as plain text; they are stored as Bcrypt hashes.
         */
        AppUser user = AppUser.builder()
                .role(UserRole.STUDENT)
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .phoneNumber(cleanOptional(request.phoneNumber()))
                .active(true)
                .build();

        AppUser savedUser = appUserRepository.save(user);

        /*
         * Student-specific data is stored separately from login data.
         * This keeps the users table clean and role-neutral.
         */
        StudentProfile profile = StudentProfile.builder()
                .user(savedUser)
                .studentNumber(request.studentNumber())
                .institutionName(request.institutionName())
                .preferredArea(request.preferredArea())
                .build();

        studentProfileRepository.save(profile);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    /*
     * Registers a new landlord account.
     *
     * Landlords are created with a landlord profile, but their verification status
     * should remain PENDING by default until an admin approves them.
     */
    @Transactional
    public AuthResponse registerLandlord(RegisterLandlordRequest request) {
        String email = normalizeEmail(request.email());

        validateEmailAndPhone(email, request.phoneNumber());

        AppUser user = AppUser.builder()
                .role(UserRole.LANDLORD)
                .firstName(request.firstName())
                .middleName(request.middleName())
                .lastName(request.lastName())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .phoneNumber(cleanOptional(request.phoneNumber()))
                .active(true)
                .build();

        AppUser savedUser = appUserRepository.save(user);

        /*
         * Landlord-specific information is stored in a separate profile table.
         * This allows admin verification without mixing landlord fields into users.
         */
        LandlordProfile profile = LandlordProfile.builder()
                .user(savedUser)
                .businessName(request.businessName())
                .idDocument(request.idDocument())
                .build();

        landlordProfileRepository.save(profile);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                token
        );
    }

    /*
     * Logs in an existing user.
     *
     * AuthenticationManager checks the email and password using:
     * - CustomUserDetailsService
     * - PasswordEncoder
     */
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }

    /*
     * Returns the currently authenticated user's basic profile data.
     * The email usually comes from the JWT subject.
     */
    public CurrentUserResponse getCurrentUser(String email) {
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new CurrentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.getActive()
        );
    }

    // Prevents duplicate accounts by checking unique email and phone number.
    private void validateEmailAndPhone(String email, String phoneNumber) {
        if (appUserRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String cleanPhone = cleanOptional(phoneNumber);

        if (cleanPhone != null && appUserRepository.existsByPhoneNumber(cleanPhone)) {
            throw new IllegalArgumentException("Phone number already exists");
        }
    }

    /*
     * Normalizes emails before saving or searching.
     *
     * This prevents duplicate accounts such as:
     * Student@Test.com
     * student@test.com
     */
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /*
     * Converts blank optional fields into null.
     * This keeps the database cleaner than storing empty strings.
     */
    private String cleanOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
