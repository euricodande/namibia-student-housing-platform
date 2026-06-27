package io.github.euricodande.housing.security;

import io.github.euricodande.housing.user.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    /*
     * Secret key used to sign and verify JWT tokens.
     *
     * The value comes from application.properties:
     * app.jwt.secret=...
     *
     * In production, this will come from an environment variable, not from a hardcoded value.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /*
     * Token lifetime in milliseconds.
     *
     * Example:
     * 86400000 ms = 24 hours
     */
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /*
     * Creates a JWT token after the user successfully logs in.
     *
     * The token stores:
     * - subject: user's email
     * - role: user's role
     * - issued time
     * - expiry time
     */
    public String generateToken(AppUser user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /*
     * Extracts the username from the token.
     *
     * In this application, the JWT subject is the user's email,
     * so this method returns the email address.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
     * Checks whether a token is valid for a specific user.
     *
     * A token is valid if:
     * - the email inside the token matches the authenticated user
     * - the token has not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Checks whether the token expiry date is before the current time.
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /*
     * Parses the JWT token and extracts all claims.
     *
     * Claims are the pieces of data stored inside the token,
     * such as subject, role, issued time, and expiry time.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /*
     * Converts the JWT secret string into a cryptographic signing key.
     *
     * The same key is used when creating and verifying tokens.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
