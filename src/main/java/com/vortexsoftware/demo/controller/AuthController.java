package com.vortexsoftware.demo.controller;

import com.vortexsoftware.demo.model.DemoUser;
import com.vortexsoftware.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Authentication controller for demo users
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Login with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and password required"));
        }

        Optional<DemoUser> userOpt = authService.authenticateUser(request.getEmail(), request.getPassword());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
        }

        DemoUser user = userOpt.get();

        // Create session JWT and set as cookie
        String sessionToken = authService.createSessionJWT(user);
        authService.setSessionCookie(response, sessionToken);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "user", user.withoutPassword()
        ));
    }

    /**
     * Logout (clear session)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.clearSessionCookie(response);
        return ResponseEntity.ok(Map.of("success", true));
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Optional<DemoUser> userOpt = authService.getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of("user", userOpt.get().withoutPassword()));
    }

    /**
     * Login request model
     */
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}