package com.vortexsoftware.demo.controller;

import com.vortexsoftware.demo.model.DemoUser;
import com.vortexsoftware.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Demo controller for testing routes
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final AuthService authService;

    @Autowired
    public DemoController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Get all demo users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getDemoUsers() {
        List<DemoUser> users = authService.getDemoUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }

    /**
     * Protected route that requires authentication
     */
    @GetMapping("/protected")
    public ResponseEntity<?> getProtectedResource(HttpServletRequest request) {
        Optional<DemoUser> userOpt = authService.getCurrentUser(request);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Not authenticated"));
        }

        DemoUser user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "message", "This is a protected route!",
                "user", user.withoutPassword(),
                "timestamp", Instant.now().toString()
        ));
    }
}