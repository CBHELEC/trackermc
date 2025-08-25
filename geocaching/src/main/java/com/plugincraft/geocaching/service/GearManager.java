package com.plugincraft.geocaching.service;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.model.Difficulty;
import com.plugincraft.geocaching.model.LootEntry;
import com.plugincraft.geocaching.model.LootPack;
import com.plugincraft.geocaching.util.Items;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public class GearManager {
    private final GeoCachingPlugin plugin;

    public GearManager(GeoCachingPlugin plugin) { this.plugin = plugin; }

    public ItemStack gps(Cache target) {
        ItemStack item = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "GeoGPS » " + ChatColor.YELLOW + target.getName());
        meta.setLodestone(target.getLocation());
        meta.setLodestoneTracked(false); // points even without actual lodestone
        meta.getPersistentDataContainer().set(plugin.keyGPS(), PersistentDataType.STRING, target.getId());
        item.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1); // dummy enchantment
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); // hide the enchantment
        item.setItemMeta(meta);
        return item;
    }

    public void retargetGPS(ItemStack item, Cache target) {
        if (item == null || item.getType() != Material.COMPASS) return;
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(plugin.keyGPS(), PersistentDataType.STRING)) return;
        meta.setLodestone(target.getLocation());
        meta.setLodestoneTracked(false);
        meta.setDisplayName("" + ChatColor.BOLD + ChatColor.AQUA + "GPS NAVIGATOR » " + ChatColor.YELLOW + target.getName());
        item.setItemMeta(meta);
    }

    public boolean isGPS(ItemStack item) {
        if (item == null) return false;
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return false;
        return meta.getPersistentDataContainer().has(plugin.keyGPS(), PersistentDataType.STRING);
    }

    public String gpsTargetId(ItemStack item) {
        if (!(item.getItemMeta() instanceof CompassMeta meta)) return null;
        return meta.getPersistentDataContainer().get(plugin.keyGPS(), PersistentDataType.STRING);
    }

    public ItemStack trophy(Cache cache) {
        ItemStack head = new ItemStack(Material.FILLED_MAP);
        Items.name(head, ChatColor.GOLD + "Geocache Trophy: " + cache.getName());
        Items.lore(head,
            ChatColor.GRAY + "ID: " + cache.getId(),
            ChatColor.GRAY + "World: " + cache.getLocation().getWorld().getName(),
            ChatColor.GRAY + "Coords: " + cache.getLocation().getBlockX() + ", " + cache.getLocation().getBlockY() + ", " + cache.getLocation().getBlockZ(),
            ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Found it!"
        );
        head.editMeta(m -> m.getPersistentDataContainer().set(plugin.keyTrophy(), PersistentDataType.STRING, cache.getId()));
        return head;
    }

    public ItemStack hidePackIcon(LootPack pack) {
        ItemStack icon = new ItemStack(pack.icon);
        Items.name(icon, pack.display);
        return icon;
    }

    public ItemStack[] generateLoot(LootPack pack, Difficulty diff) {
        if (pack == null) return new ItemStack[0];
        var rng = plugin.loot().rng();
        double mult = diff.lootMultiplier;
        int rolls = 3 + (diff == Difficulty.EPIC ? 2 : (diff == Difficulty.HARD ? 1 : 0));
        List<LootEntry> entries = pack.getRandomEntries(rolls, rng);
        return entries.stream().map(e -> e.toItem(mult)).toArray(ItemStack[]::new);
    }
}