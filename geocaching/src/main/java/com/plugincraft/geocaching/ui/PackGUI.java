package com.plugincraft.geocaching.ui;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.LootPack;
import com.plugincraft.geocaching.service.GearManager;
import com.plugincraft.geocaching.service.LootManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PackGUI implements Listener {
    private static LootManager loot;
    private static GearManager gear;

    public PackGUI(GeoCachingPlugin plugin, LootManager lm, GearManager gm) {
        loot = lm; gear = gm;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "Choose Hide Pack");
        int i = 10;
        for (LootPack pack : loot.all()) inv.setItem(i++, gear.hidePackIcon(pack));
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("Choose Hide Pack")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        var name = e.getCurrentItem().getItemMeta().getDisplayName();
        LootPack chosen = loot.all().stream().filter(p -> name.contains(p.display.replace("§","&").replace("&","§")) || name.equals(p.display)).findFirst().orElse(null);
        if (chosen == null) return;
        ((Player)e.getWhoClicked()).sendMessage("§aChosen pack: §e" + chosen.display + "§7 (use when creating caches; loot will roll on claim)");
        e.getWhoClicked().closeInventory();
    }
}