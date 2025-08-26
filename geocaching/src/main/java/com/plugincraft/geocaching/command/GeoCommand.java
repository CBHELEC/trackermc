package com.plugincraft.geocaching.command;

import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.service.CacheManager;
import com.plugincraft.geocaching.service.GPSManager;
import com.plugincraft.geocaching.ui.CacheGUI;
import com.plugincraft.geocaching.util.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeoCommand implements CommandExecutor {

    private final CacheManager cacheManager;
    private final GPSManager gpsManager;
    private final CacheGUI cacheGUI;

    public GeoCommand(CacheManager cacheManager, GPSManager gpsManager, CacheGUI cacheGUI) {
        this.cacheManager = cacheManager;
        this.gpsManager = gpsManager;
        this.cacheGUI = cacheGUI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.msg(sender, "&cOnly players can run this command.");
            return true;
        }

        if (args.length == 0) {
            Chat.msg(player, "&eUsage: /geocache <create|gps|edit|delete|info>");
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create" -> {
                // Open placement GUI for cache creation
                cacheGUI.openHidePlacementGUI(player, cacheManager);
                return true;
            }

            case "gps" -> {
                if (args.length < 2) {
                    Chat.msg(player, "&eUsage: /geocache gps <cacheName>");
                    return true;
                }

                String targetName = args[1];
                Cache targetCache = cacheManager.getCacheByOwnerName(targetName);
                if (targetCache == null) {
                    Chat.msg(player, "&cNo cache found for: " + targetName);
                    return true;
                }

                // Require player to hold a plain compass
                if (player.getInventory().getItemInMainHand().getType() != org.bukkit.Material.COMPASS) {
                    Chat.msg(player, "&cYou must hold a GPS (compass) to locate the cache.");
                    return true;
                }

                // Enhance compass and give GPS functionality
                gpsManager.giveGPS(player, targetCache);
                return true;
            }

            case "edit" -> {
                if (args.length < 2) {
                    Chat.msg(player, "&eUsage: /geocache edit <cacheName>");
                    return true;
                }
                String targetName = args[1];
                Cache targetCache = cacheManager.getCacheByOwnerName(targetName);
                if (targetCache == null || !targetCache.getOwner().equals(player.getUniqueId())) {
                    Chat.msg(player, "&cYou don't own that cache!");
                    return true;
                }

                cacheGUI.openEditGUI(player, targetCache);
                return true;
            }

            case "delete" -> {
                if (args.length < 2) {
                    Chat.msg(player, "&eUsage: /geocache delete <cacheName>");
                    return true;
                }
                String targetName = args[1];
                Cache targetCache = cacheManager.getCacheByOwnerName(targetName);
                if (targetCache == null || !targetCache.getOwner().equals(player.getUniqueId())) {
                    Chat.msg(player, "&cYou don't own that cache!");
                    return true;
                }

                cacheManager.deleteCache(targetCache);
                Chat.msg(player, "&aCache deleted: " + targetCache.getName());
                return true;
            }

            case "info" -> {
                if (args.length < 2) {
                    Chat.msg(player, "&eUsage: /geocache info <cacheName>");
                    return true;
                }
                String targetName = args[1];
                Cache targetCache = cacheManager.getCacheByOwnerName(targetName);
                if (targetCache == null) {
                    Chat.msg(player, "&cNo cache found for: " + targetName);
                    return true;
                }

                cacheGUI.showInfoGUI(player, targetCache);
                return true;
            }

            default -> {
                Chat.msg(player, "&cUnknown subcommand: " + sub);
                return true;
            }
        }
    }
}
