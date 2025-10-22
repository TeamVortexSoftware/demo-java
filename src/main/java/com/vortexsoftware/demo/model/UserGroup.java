package com.vortexsoftware.demo.model;

/**
 * User group model
 */
public class UserGroup {
    private String type;
    private String id;
    private String name;

    public UserGroup() {}

    public UserGroup(String type, String id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}