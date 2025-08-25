package com.plugincraft.geocaching.ui;

import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CacheGUI implements Listener {
    public CacheGUI(org.bukkit.plugin.Plugin plugin) { Bukkit.getPluginManager().registerEvents(this, plugin); }

    public static void open(Player p, Cache c) {
        Inventory inv = Bukkit.createInventory(null, 27, "Geocache » " + c.getName());
        ItemStack info = new ItemStack(Material.PAPER);
        Items.name(info, ChatColor.GOLD + "Info");
        Items.lore(info,
            ChatColor.GRAY + "ID: " + c.getId(),
            ChatColor.GRAY + "World: " + c.getLocation().getWorld().getName(),
            ChatColor.GRAY + "Coords: " + c.getLocation().getBlockX() + "," + c.getLocation().getBlockY() + "," + c.getLocation().getBlockZ(),
            ChatColor.GRAY + "Found by: " + c.getFoundBy().size()
        );
        inv.setItem(13, info);
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().startsWith("Geocache »")) e.setCancelled(true);
    }
}