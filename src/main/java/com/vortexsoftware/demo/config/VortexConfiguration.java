package com.vortexsoftware.demo.config;

import com.vortexsoftware.demo.model.DemoUser;
import com.vortexsoftware.demo.model.UserGroup;
import com.vortexsoftware.demo.service.AuthService;
import com.vortexsoftware.sdk.VortexClient;
import com.vortexsoftware.sdk.spring.VortexConfig;
import com.vortexsoftware.sdk.spring.VortexController;
import com.vortexsoftware.sdk.types.InvitationGroup;
import com.vortexsoftware.sdk.types.InvitationTarget;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Vortex SDK configuration for the demo application
 */
@Configuration
public class VortexConfiguration {

    @Autowired
    private AuthService authService;

    /**
     * Create VortexClient bean
     */
    @Bean
    public VortexClient vortexClient(@Value("${vortex.api.key:demo-api-key}") String apiKey,
                                    @Value("${vortex.api.base-url:#{null}}") String baseUrl) {
        return baseUrl != null ? new VortexClient(apiKey, baseUrl) : new VortexClient(apiKey);
    }

    /**
     * Vortex configuration that integrates with our demo auth system
     *
     * This demo shows BOTH formats for educational purposes:
     * 1. New simplified format (recommended): userEmail + userIsAutojoinAdmin
     * 2. Legacy format (deprecated): identifiers + groups + role
     *
     * In production, choose one format based on your needs.
     */
    @Bean
    public VortexConfig vortexConfig() {
        return new VortexConfig() {
            @Override
            public VortexUser authenticateUser() {
                // Get current request
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpServletRequest request = attrs.getRequest();

                // Get current user from session
                Optional<DemoUser> userOpt = authService.getCurrentUser(request);
                if (userOpt.isEmpty()) {
                    return null;
                }

                DemoUser demoUser = userOpt.get();

                // NEW SIMPLIFIED FORMAT (Recommended)
                // This is the preferred way to create VortexUser objects
                // Comment out the legacy format below to use only this
                return new VortexUser(
                        demoUser.getId(),
                        demoUser.getEmail(),
                        demoUser.isAutojoinAdmin()
                );

                /* LEGACY FORMAT (Deprecated but still supported)
                // Convert to legacy VortexUser format
                List<InvitationTarget> identifiers = List.of(
                        new InvitationTarget("email", demoUser.getEmail())
                );

                List<InvitationGroup> groups = demoUser.getGroups().stream()
                        .map(g -> new InvitationGroup(g.getId(), g.getType(), g.getName()))
                        .toList();

                return new VortexUser(
                        demoUser.getId(),
                        identifiers,
                        groups,
                        demoUser.getRole()
                );
                */
            }

            @Override
            public boolean authorizeOperation(String operation, VortexUser user) {
                // For demo purposes, allow all operations if user is authenticated
                // In production, you'd implement proper access control
                return user != null;
            }
        };
    }

    /**
     * Create VortexController bean to register the API routes
     */
    @Bean
    public VortexController vortexController(VortexClient vortexClient, VortexConfig vortexConfig) {
        return new VortexController(vortexClient, vortexConfig);
    }
}