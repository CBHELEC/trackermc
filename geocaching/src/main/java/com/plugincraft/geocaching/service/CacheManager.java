package com.plugincraft.geocaching.service;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.data.DataStore;
import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.model.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CacheManager {
    private final GeoCachingPlugin plugin;
    private final DataStore data;
    private final Map<String, Cache> caches = new LinkedHashMap<>();

    public CacheManager(GeoCachingPlugin plugin, DataStore data) {
        this.plugin = plugin; this.data = data;
    }

    public void loadAll() {
        caches.clear();
        for (Cache c : data.loadCaches()) caches.put(c.getId(), c);
    }
    public void saveAll() { for (Cache c : caches.values()) data.saveCache(c); }

    public Cache create(String name, Location loc, UUID creator, Difficulty diff) {
        String id = generateId(name);
        Cache c = new Cache(id, name, loc.clone(), creator, System.currentTimeMillis(), diff, true);
        caches.put(id, c);
        data.saveCache(c);
        return c;
    }

    private String generateId(String base) {
        String clean = base.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","-");
        if (clean.isBlank()) clean = "cache";
        String id = clean;
        int i = 1;
        while (caches.containsKey(id)) id = clean + "-" + (++i);
        return id;
    }

    public Cache get(String id) { return caches.get(id); }
    public boolean remove(String id) {
        if (caches.remove(id) != null) { data.deleteCache(id); return true; }
        return false;
    }
    public int count() { return caches.size(); }

    public List<Cache> list() { return new ArrayList<>(caches.values()); }

    public List<Cache> nearest(Location loc, int limit) {
        return caches.values().stream()
            .filter(Cache::isActive)
            .sorted(Comparator.comparingDouble(c -> c.getLocation().distance(loc)))
            .limit(limit).collect(Collectors.toList());
    }

    public boolean claim(Cache c, Player p) {
        if (c.getFoundBy().contains(p.getUniqueId())) return false;
        c.getFoundBy().add(p.getUniqueId());
        plugin.data().recordFind(c.getId(), p.getUniqueId());
        return true;
    }
}