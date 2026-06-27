package io.github.euricodande.housing.security;

import io.github.euricodande.housing.user.AppUser;
import io.github.euricodande.housing.user.AppUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    /*
     * Spring Security calls this method when it needs to load a user for authentication.
     *
     * Even though the method is called loadUserByUsername, this application uses
     * email as the login identifier.
     */
    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        /*
         * Find the user in the database using their email.
         * If no user exists, Spring Security treats the login attempt as invalid.
         */
        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        /*
         * Convert our AppUser entity into Spring Security's UserDetails object.
         *
         * Spring Security uses this object to check:
         * - username/email
         * - hashed password
         * - authorities/roles
         * - whether the account is disabled
         */
        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                /*
                 * Spring Security expects role authorities to start with "ROLE_".
                 * Example: STUDENT becomes ROLE_STUDENT.
                 */
                .authorities("ROLE_" + user.getRole().name())
                // If active is false, the account is disabled and cannot authenticate.
                .disabled(!Boolean.TRUE.equals(user.getActive()))
                .build();
    }
}
