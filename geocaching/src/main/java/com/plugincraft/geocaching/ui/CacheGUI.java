package com.plugincraft.geocaching.ui;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.model.ContainerType;
import com.plugincraft.geocaching.service.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CacheGUI {

    private final GeoCachingPlugin plugin;

    public CacheGUI(GeoCachingPlugin plugin) {
        this.plugin = plugin;
    }

    public void openHidePlacementGUI(Player player, CacheManager cacheManager) {
        Inventory inv = Bukkit.createInventory(null, 9, "Place your cache container");

        for (ContainerType type : ContainerType.values()) {
            ItemStack item = new ItemStack(type.getMaterial());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(type.getDisplayName());
                item.setItemMeta(meta);
            }
            inv.addItem(item);
        }

        player.openInventory(inv);

        plugin.getServer().getPluginManager().registerEvents(new InventoryClickHandler(cacheManager), plugin);
    }

    public void openEditGUI(Player player, Cache cache) {
        player.sendMessage("§eEditing cache: " + cache.getName());
    }

    public void showInfoGUI(Player player, Cache cache) {
        player.sendMessage("§eCache info: " + cache.getName());
    }

    private class InventoryClickHandler implements org.bukkit.event.Listener {
        private final CacheManager cacheManager;

        public InventoryClickHandler(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        @org.bukkit.event.EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (!(e.getWhoClicked() instanceof Player player)) return;
            if (!e.getView().getTitle().equals("Place your cache container")) return;
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;

            ContainerType chosen = ContainerType.fromMaterial(clicked.getType());
            if (chosen == null) return;

            Cache cache = new Cache();
            cache.setContainer(chosen);
            cache.setOwner(player.getUniqueId());

            cacheManager.addCache(cache);

            player.closeInventory();
            player.sendMessage("§aCache container placed: " + chosen.getDisplayName());
            org.bukkit.event.HandlerList.unregisterAll(this);
        }
    }
}
