package com.plugincraft.geocaching.model;

import org.bukkit.Material;

public enum ContainerType {
    SMALL_CHEST(Material.CHEST, "§6Small Chest"),
    BARREL(Material.BARREL, "§6Barrel"),
    PLAYER_HEAD(Material.PLAYER_HEAD, "§6Custom Head");

    private final Material display;
    private final String displayName;

    ContainerType(Material display, String displayName) {
        this.display = display;
        this.displayName = displayName;
    }

    public Material getMaterial() {
        return display;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ContainerType fromMaterial(Material material) {
        for (ContainerType type : values()) {
            if (type.getMaterial() == material) return type;
        }
        return null;
    }
}
