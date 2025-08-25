package com.plugincraft.geocaching.model;

public enum Difficulty {
    EASY(1.0), NORMAL(1.0), HARD(1.2), EPIC(1.5);
    public final double lootMultiplier;
    Difficulty(double m) { lootMultiplier = m; }
}