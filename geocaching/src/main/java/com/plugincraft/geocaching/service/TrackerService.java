package com.plugincraft.geocaching.service;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TrackerService {
    private final GeoCachingPlugin plugin;
    private final CacheManager caches;
    private final GearManager gear;
    private int taskId = -1;

    private final Map<Player, String> targets = new HashMap<>();

    public TrackerService(GeoCachingPlugin plugin, CacheManager caches, GearManager gear) {
        this.plugin = plugin; this.caches = caches; this.gear = gear;
    }

    public void start() {
        int period = Math.max(1, plugin.getConfig().getInt("gps.update-ticks", 10));
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, period, period);
    }
    public void stop() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    public void setTarget(Player p, Cache c) {
        targets.put(p, c.getId());
        Chat.msg(p, plugin.getConfig().getString("messages.target-set")
            .replace("%name%", c.getName()));
        // Immediately retarget held GPS
        ItemStack main = p.getInventory().getItemInMainHand();
        if (gear.isGPS(main)) gear.retargetGPS(main, c);
        ItemStack off = p.getInventory().getItemInOffHand();
        if (gear.isGPS(off)) gear.retargetGPS(off, c);
    }

    private void tick() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack held = p.getInventory().getItemInMainHand();
            ItemStack off = p.getInventory().getItemInOffHand();
            String id = null;
            if (gear.isGPS(held)) id = gear.gpsTargetId(held);
            else if (gear.isGPS(off)) id = gear.gpsTargetId(off);
            if (id == null) continue;
            Cache c = caches.get(id);
            if (c == null || !c.isActive()) continue;

            // Retarget just in case
            if (gear.isGPS(held)) gear.retargetGPS(held, c);
            if (gear.isGPS(off)) gear.retargetGPS(off, c);

            Location pl = p.getLocation();
            double dist = pl.getWorld().equals(c.getLocation().getWorld())
                ? pl.distance(c.getLocation())
                : Double.POSITIVE_INFINITY;

            // Action bar
            if (plugin.getConfig().getBoolean("gps.actionbar", true)) {
                String bearing = "";
                if (plugin.getConfig().getBoolean("gps.show-bearing", true) && Double.isFinite(dist)) {
                    double dx = c.getLocation().getX() - pl.getX();
                    double dz = c.getLocation().getZ() - pl.getZ();
                    double angle = Math.toDegrees(Math.atan2(-dx, dz));
                    String[] dirs = {"N","NE","E","SE","S","SW","W","NW"};
                    int idx = (int)Math.round(((angle % 360) + 360) % 360 / 45.0) % 8;
                    bearing = " §7(" + dirs[idx] + ")";
                }
                p.sendActionBar("§bGeoGPS§7: §e" + c.getName() + " §f" +
                    (Double.isFinite(dist) ? String.format("%.1fm", dist) : "different world") + bearing);
            }

            // Sound feedback
            if (plugin.getConfig().getBoolean("gps.sound.enabled", true) && Double.isFinite(dist)) {
                float pitch = (float)Math.max(0.5, Math.min(2.0, 1.9 - (float)dist / 200f));
                try {
                    Sound s = Sound.valueOf(plugin.getConfig().getString("gps.sound.name","BLOCK_NOTE_BLOCK_PLING"));
                    p.playSound(p.getLocation(), s, 0.3f, pitch);
                } catch (IllegalArgumentException ignored) {}
            }

            // Claim radius
            double r = plugin.getConfig().getDouble("gps.claim-radius", 3.0);
            if (Double.isFinite(dist) && dist <= r) {
                // Auto-claim
                if (caches.claim(c, p)) {
                    // Loot drop
                    var pack = plugin.loot().get(plugin.loot().defaultPackKey());
                    var items = gear.generateLoot(pack, c.getDifficulty());
                    for (var it : items) if (it != null && it.getType() != org.bukkit.Material.AIR)
                        p.getInventory().addItem(it).values().forEach(rem -> p.getWorld().dropItemNaturally(p.getLocation(), rem));
                    p.getInventory().addItem(gear.trophy(c));
                    p.sendTitle("§aCache Found!", "§e" + c.getName(), 10, 40, 10);
                    String msg = plugin.getConfig().getString("messages.cache-claimed");
                    Chat.msg(p, msg.replace("%name%", c.getName()));
                }
            }
        }
    }
}