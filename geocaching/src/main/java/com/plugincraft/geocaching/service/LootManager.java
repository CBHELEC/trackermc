package com.plugincraft.geocaching.service;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.LootEntry;
import com.plugincraft.geocaching.model.LootPack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LootManager {
    private final GeoCachingPlugin plugin;
    private final Map<String, LootPack> packs = new LinkedHashMap<>();
    private final Random rng = new Random();

    public LootManager(GeoCachingPlugin plugin) { this.plugin = plugin; }

    public void reload() {
        packs.clear();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("loot.packs");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            ConfigurationSection p = sec.getConfigurationSection(key);
            String display = ChatColor.translateAlternateColorCodes('&', p.getString("display", key));
            Material icon = Material.matchMaterial(p.getString("icon", "CHEST"));
            if (icon == null) icon = Material.CHEST;
            LootPack pack = new LootPack(key, display, icon);
            for (Map<?, ?> raw : p.getMapList("entries")) {
                String mat = Objects.toString(raw.get("material"), "BREAD");
                int amount = Integer.parseInt(Objects.toString(raw.get("amount"), "1"));
                int weight = Integer.parseInt(Objects.toString(raw.get("weight"), "1"));
                Material m = Material.matchMaterial(mat);
                if (m != null) pack.entries.add(new LootEntry(m, amount, weight));
            }
            packs.put(key.toLowerCase(Locale.ROOT), pack);
        }
    }

    public LootPack get(String key) { return key == null ? null : packs.get(key.toLowerCase(Locale.ROOT)); }
    public Collection<LootPack> all() { return Collections.unmodifiableCollection(packs.values()); }
    public String defaultPackKey() { return plugin.getConfig().getString("loot.default-pack", "starter").toLowerCase(Locale.ROOT); }
    public Random rng() { return rng; }
}