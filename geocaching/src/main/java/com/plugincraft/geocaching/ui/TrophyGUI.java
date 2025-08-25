package com.plugincraft.geocaching.ui;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TrophyGUI implements Listener {
    public TrophyGUI(org.bukkit.plugin.Plugin plugin) { Bukkit.getPluginManager().registerEvents(this, plugin); }

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, "Your Geocache Trophies");
        // Simple: show count only (full per-player trophy storage can be added later)
        ItemStack card = new ItemStack(Material.NAME_TAG);
        Items.name(card, "§eTrophies");
        Items.lore(card, "§7Find count: §b" + GeoCachingPlugin.get().data().getFindCount(p.getUniqueId()));
        inv.setItem(13, card);
        p.openInventory(inv);
    }

    @EventHandler public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Your Geocache Trophies")) e.setCancelled(true);
    }
}