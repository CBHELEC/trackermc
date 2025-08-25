package com.plugincraft.geocaching.model;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootPack {
    public final String key;
    public final String display;
    public final Material icon;
    public final List<LootEntry> entries = new ArrayList<>();

    public LootPack(String key, String display, Material icon) {
        this.key = key; this.display = display; this.icon = icon;
    }

    public List<LootEntry> getRandomEntries(int rolls, Random rng) {
        List<LootEntry> out = new ArrayList<>();
        int totalWeight = entries.stream().mapToInt(e -> e.weight).sum();
        if (totalWeight <= 0) return out;
        for (int i = 0; i < rolls; i++) {
            int r = rng.nextInt(totalWeight) + 1;
            int acc = 0;
            for (LootEntry e : entries) {
                acc += e.weight;
                if (r <= acc) { out.add(e); break; }
            }
        }
        return out;
    }
}