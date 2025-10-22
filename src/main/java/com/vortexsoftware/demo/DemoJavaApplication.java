package com.vortexsoftware.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * Main Spring Boot application for Vortex Java SDK Demo
 */
@SpringBootApplication
public class DemoJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoJavaApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        String port = System.getProperty("server.port", "8080");

        System.out.println();
        System.out.println("🚀 Demo Java server running on port " + port);
        System.out.println("📱 Visit http://localhost:" + port + " to try the demo");
        System.out.println("🔧 Vortex API routes available at http://localhost:" + port + "/api/vortex");
        System.out.println("📊 Health check: http://localhost:" + port + "/health");
        System.out.println();
        System.out.println("Demo users:");
        System.out.println("  - admin@example.com / password123 (admin role)");
        System.out.println("  - user@example.com / userpass (user role)");
        System.out.println();
    }
}