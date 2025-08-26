package com.plugincraft.geocaching.service;

import com.plugincraft.geocaching.model.Cache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

public class GPSManager {

    private static GPSManager instance;

    public GPSManager() {
        instance = this;
    }

    public static GPSManager getInstance() {
        return instance;
    }

    public void giveGPS(Player player, Cache cache) {
        ItemStack gps = new ItemStack(Material.COMPASS);
        ItemMeta meta = gps.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§bGPS NAVIGATOR » §e" + cache.getOwner()); 
            gps.setItemMeta(meta);
        }

        player.getInventory().addItem(gps);
        player.sendMessage("§aGPS given for cache: §e" + cache.getOwner());
    }
}
