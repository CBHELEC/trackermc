package com.plugincraft.geocaching.model;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LootEntry {
    public final Material material;
    public final int amount;
    public final int weight;

    public LootEntry(Material m, int amount, int weight) {
        this.material = m; this.amount = amount; this.weight = weight;
    }

    public ItemStack toItem(double multiplier) {
        int amt = Math.max(1, (int)Math.round(amount * multiplier));
        return new ItemStack(material, Math.min(amt, material.getMaxStackSize()));
    }
}