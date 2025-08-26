package com.plugincraft.geocaching.ui;

import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.model.Difficulty;
import com.plugincraft.geocaching.service.CacheManager;
import com.plugincraft.geocaching.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachePlacementListener implements Listener {

    private static final Map<UUID, TempCacheData> placementMap = new HashMap<>();

    public static void registerPlacement(Player p, String name, Difficulty diff) {
        placementMap.put(p.getUniqueId(), new TempCacheData(name, diff));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!placementMap.containsKey(p.getUniqueId())) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        e.setCancelled(true);
        TempCacheData data = placementMap.get(p.getUniqueId());

        Chat.msg(p, "You selected " + clicked.getType() + " as your container. Now right-click the block to place it in the world.");

        // Give player the chosen container item to place
        ItemStack placeItem = new ItemStack(clicked.getType());
        ItemMeta meta = placeItem.getItemMeta();
        if (meta != null) meta.setDisplayName(data.name);
        placeItem.setItemMeta(meta);

        p.getInventory().addItem(placeItem);
        placementMap.remove(p.getUniqueId());
    }

    // Optional: detect right-click placement if you want custom actions
    @EventHandler
    public void onPlayerPlace(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;

        // Could check meta.getDisplayName() for temporary placement logic
        // Then create cache at this block
    }

    private static class TempCacheData {
        String name;
        Difficulty diff;

        TempCacheData(String name, Difficulty diff) {
            this.name = name;
            this.diff = diff;
        }
    }
}
