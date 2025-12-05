package com.vortexsoftware.demo.model;

import java.util.List;

/**
 * Demo user model for the Java demo application
 *
 * Supports both new simplified format (isAutojoinAdmin) and legacy format (role, groups)
 * for demonstrating Vortex SDK compatibility.
 */
public class DemoUser {
    private String id;
    private String email;
    private String password;

    // New simplified field (preferred)
    private boolean isAutojoinAdmin;

    // Legacy fields (deprecated but still supported)
    private String role;
    private List<UserGroup> groups;

    public DemoUser() {}

    public DemoUser(String id, String email, String password, boolean isAutojoinAdmin, String role, List<UserGroup> groups) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.isAutojoinAdmin = isAutojoinAdmin;
        this.role = role;
        this.groups = groups;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isAutojoinAdmin() {
        return isAutojoinAdmin;
    }

    public void setAutojoinAdmin(boolean autoJoinAdmin) {
        isAutojoinAdmin = autoJoinAdmin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<UserGroup> groups) {
        this.groups = groups;
    }

    /**
     * Create a copy without password for API responses
     */
    public DemoUser withoutPassword() {
        return new DemoUser(id, email, null, isAutojoinAdmin, role, groups);
    }

    @Override
    public String toString() {
        return "DemoUser{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", isAutojoinAdmin=" + isAutojoinAdmin +
                ", role='" + role + '\'' +
                ", groups=" + groups +
                '}';
    }
}