package com.ewyboy.blockexchange.database.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class DBItem {

    @Id
    @Column(name = "resource_location", nullable = false, unique = true)
    private String resourceLocation;

    @Column(nullable = false)
    private String namespace;

    @Column(nullable = false)
    private String path;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
