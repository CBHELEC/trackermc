package com.plugincraft.geocaching;

import com.plugincraft.geocaching.command.GeoCommand;
import com.plugincraft.geocaching.data.DataStore;
import com.plugincraft.geocaching.data.SQLite;
import com.plugincraft.geocaching.service.CacheManager;
import com.plugincraft.geocaching.service.GearManager;
import com.plugincraft.geocaching.service.LootManager;
import com.plugincraft.geocaching.service.TrackerService;
import com.plugincraft.geocaching.ui.CacheGUI;
import com.plugincraft.geocaching.ui.PackGUI;
import com.plugincraft.geocaching.ui.TrophyGUI;
import com.plugincraft.geocaching.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class GeoCachingPlugin extends JavaPlugin {

    private static GeoCachingPlugin instance;
    private DataStore data;
    private CacheManager caches;
    private LootManager loot;
    private GearManager gear;
    private TrackerService tracker;

    // NamespacedKeys
    private NamespacedKey keyGPS;
    private NamespacedKey keyCacheId;
    private NamespacedKey keyTrophy;

    public static GeoCachingPlugin get() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Chat.init(this);

        // Keys
        keyGPS = new NamespacedKey(this, "gps");
        keyCacheId = new NamespacedKey(this, "cache-id");
        keyTrophy = new NamespacedKey(this, "trophy");

        // Data
        data = new SQLite(this);
        data.init();

        loot = new LootManager(this);
        loot.reload();

        caches = new CacheManager(this, data);
        caches.loadAll();

        gear = new GearManager(this);
        tracker = new TrackerService(this, caches, gear);
        tracker.start();

        // GUIs register (listeners are inside GUI classes)
        new CacheGUI(this);
        new PackGUI(this, loot, gear);
        new TrophyGUI(this);

        // Command
        GeoCommand cmd = new GeoCommand(this, caches, loot, gear);
        getCommand("geocache").setExecutor(cmd);
        getCommand("geocache").setTabCompleter(cmd);

        getLogger().info("PluginCraft Geocaching enabled with " + caches.count() + " caches.");
    }

    @Override
    public void onDisable() {
        if (tracker != null) tracker.stop();
        if (caches != null) caches.saveAll();
        if (data != null) data.close();
    }

    // Getter for tracker
    public TrackerService tracker() {
        return tracker;
    }

    public DataStore data() { return data; }
    public CacheManager caches() { return caches; }
    public LootManager loot() { return loot; }
    public GearManager gear() { return gear; }

    public NamespacedKey keyGPS() { return keyGPS; }
    public NamespacedKey keyCacheId() { return keyCacheId; }
    public NamespacedKey keyTrophy() { return keyTrophy; }
}