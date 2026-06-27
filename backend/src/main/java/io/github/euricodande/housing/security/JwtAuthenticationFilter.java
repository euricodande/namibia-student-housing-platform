package io.github.euricodande.housing.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {
        /*
         * JWT tokens are expected in the Authorization header using this format:
         *
         * Authorization: Bearer <token>
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * If the request has no JWT token, we do not block it here.
         * We simply pass it to the next filter.
         *
         * Public endpoints will continue normally.
         * Protected endpoints will later be rejected by Spring Security.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " and keep only the actual JWT token.
        String token = authHeader.substring(7);

        try {
            /*
             * The JWT subject stores the user's email.
             * In this application, email is used as the login identifier.
             */
            String email = jwtService.extractUsername(token);

            /*
             * Only authenticate the request if:
             * 1. The token contains an email.
             * 2. No user is already authenticated in the current security context.
             */
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                /*
                 * Load the user from the database.
                 * Spring Security needs UserDetails, not our AppUser entity directly.
                 */
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                /*
                 * Validate that:
                 * 1. The token belongs to this user.
                 * 2. The token has not expired.
                 */
                if (jwtService.isTokenValid(token, userDetails)) {
                    /*
                     * Create an authenticated Spring Security object.
                     * The password is set to null because we are not logging in with a password here;
                     * the JWT has already proven the user's identity.
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Attach request details such as IP address and session information.
                     * This is useful for auditing and security context details.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    /*
                     * Store the authenticated user in Spring Security's context.
                     * After this, controllers can recognize the request as authenticated.
                     */
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (JwtException | IllegalArgumentException ignored) {
            /*
             * If the token is invalid, expired, malformed, or unsupported,
             * we do not authenticate the request.
             *
             * We do not throw an error here because Spring Security will decide
             * whether the requested endpoint requires authentication.
             */
        }

        // Continue the filter chain.
        filterChain.doFilter(request, response);
    }
}
