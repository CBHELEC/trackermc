package com.plugincraft.geocaching.util;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Items {
    public static void name(ItemStack it, String name) {
        it.editMeta(m -> m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name)));
    }
    
    public static void lore(ItemStack it, String... lines) {
        it.editMeta(m -> m.setLore(Arrays.stream(lines) // Stream the lines array
            .map(s -> ChatColor.translateAlternateColorCodes('&', s)) // Use each line 's' for translation
            .toList())); // Collect the translated lines back into a list
    }
}