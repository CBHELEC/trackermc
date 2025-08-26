package com.plugincraft.geocaching.model;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Cache {
    private final String id;
    private String name;
    private Location location;
    private UUID creator;
    private long createdAt;
    private Difficulty difficulty;
    private boolean active;
    private final Set<UUID> foundBy = new HashSet<>();

    public Cache(String id, String name, Location location, UUID creator, long createdAt, Difficulty difficulty, boolean active) {
        this.id = id; this.name = name; this.location = location;
        this.creator = creator; this.createdAt = createdAt;
        this.difficulty = difficulty; this.active = active;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String n) { name = n; }
    public Location getLocation() { return location; }
    public void setLocation(Location l) { location = l; }
    public UUID getCreator() { return creator; }
    public long getCreatedAt() { return createdAt; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty d) { difficulty = d; }
    public boolean isActive() { return active; }
    public void setActive(boolean a) { active = a; }
    public Set<UUID> getFoundBy() { return foundBy; }

    private UUID owner;
    private ContainerType container;

    public UUID getOwner() {
        return owner != null ? owner : creator; // fallback to creator if needed
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public ContainerType getContainer() {
        return container;
    }

    public void setContainer(ContainerType container) {
        this.container = container;
    }

    // Optional no-arg constructor for CacheGUI
    public Cache() {
        this.id = UUID.randomUUID().toString();
    }

}