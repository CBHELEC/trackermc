package com.plugincraft.geocaching.util;

import com.plugincraft.geocaching.GeoCachingPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Chat {
    private static String prefix = "§6[Geo] §r";

    public static void init(GeoCachingPlugin plugin) {
        String raw = plugin.getConfig().getString("messages.prefix", "&6[Geo]&r ");
        prefix = ChatColor.translateAlternateColorCodes('&', raw);
    }

    public static void msg(CommandSender s, String raw) {
        if (raw == null) return;
        s.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', raw));
    }

    public static String color(String raw) {
        if (raw == null) return "";
        return ChatColor.translateAlternateColorCodes('&', raw);
    }
}
