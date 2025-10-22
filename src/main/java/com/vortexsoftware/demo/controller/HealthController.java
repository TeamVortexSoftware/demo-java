package com.vortexsoftware.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Health check controller
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        List<String> vortexRoutes = Arrays.asList(
                "/api/vortex/jwt",
                "/api/vortex/invitations",
                "/api/vortex/invitations/:id",
                "/api/vortex/invitations/accept",
                "/api/vortex/invitations/by-group/:type/:id",
                "/api/vortex/invitations/:id/reinvite"
        );

        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "timestamp", Instant.now().toString(),
                "vortex", Map.of(
                        "configured", true,
                        "routes", vortexRoutes
                )
        ));
    }
}