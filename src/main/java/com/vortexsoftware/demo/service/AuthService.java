package com.vortexsoftware.demo.service;

import com.vortexsoftware.demo.model.DemoUser;
import com.vortexsoftware.demo.model.UserGroup;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Authentication service for demo users
 */
@Service
public class AuthService {

    private static final String JWT_SECRET = "demo-secret-key-for-session-management";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    private static final String SESSION_COOKIE_NAME = "session";

    // Demo users with new simplified format (isAutoJoinAdmin)
    // Legacy fields (role, groups) are also included for backward compatibility demo
    private static final List<DemoUser> DEMO_USERS = Arrays.asList(
            new DemoUser(
                    "user-1",
                    "admin@example.com",
                    hashPassword("password123"),
                    true,  // isAutoJoinAdmin
                    "admin",  // legacy role
                    Arrays.asList(  // legacy groups
                            new UserGroup("team", "team-1", "Engineering"),
                            new UserGroup("organization", "org-1", "Acme Corp")
                    )
            ),
            new DemoUser(
                    "user-2",
                    "user@example.com",
                    hashPassword("userpass"),
                    false,  // isAutoJoinAdmin
                    "user",  // legacy role
                    Arrays.asList(  // legacy groups
                            new UserGroup("team", "team-1", "Engineering")
                    )
            )
    );

    /**
     * Authenticate user by email and password
     */
    public Optional<DemoUser> authenticateUser(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }

        String hashedPassword = hashPassword(password);

        return DEMO_USERS.stream()
                .filter(user -> email.equals(user.getEmail()))
                .filter(user -> hashedPassword.equals(user.getPassword()))
                .findFirst();
    }

    /**
     * Create session JWT for demo purposes
     */
    public String createSessionJWT(DemoUser user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(24, ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .claim("groups", user.getGroups())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(KEY)
                .compact();
    }

    /**
     * Verify and parse session JWT
     */
    public Optional<DemoUser> verifySessionJWT(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            // Find the full user object
            return DEMO_USERS.stream()
                    .filter(user -> userId.equals(user.getId()) && email.equals(user.getEmail()))
                    .findFirst();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Get current user from request session
     */
    public Optional<DemoUser> getCurrentUser(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> SESSION_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(this::verifySessionJWT);
    }

    /**
     * Set session cookie
     */
    public void setSessionCookie(HttpServletResponse response, String jwt) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        cookie.setSecure(false); // Set to true in production with HTTPS
        response.addCookie(cookie);
    }

    /**
     * Clear session cookie
     */
    public void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Get all demo users (without passwords)
     */
    public List<DemoUser> getDemoUsers() {
        return DEMO_USERS.stream()
                .map(DemoUser::withoutPassword)
                .toList();
    }

    /**
     * Simple password hashing using SHA-256 (for demo purposes only)
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}