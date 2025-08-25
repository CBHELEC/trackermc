package com.plugincraft.geocaching.data;

import com.plugincraft.geocaching.GeoCachingPlugin;
import com.plugincraft.geocaching.model.Cache;
import com.plugincraft.geocaching.model.Difficulty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.*;

public class SQLite implements DataStore {
    private final GeoCachingPlugin plugin;
    private Connection conn;

    public SQLite(GeoCachingPlugin plugin) { this.plugin = plugin; }

    @Override
    public void init() {
        try {
            File dbFile = new File(plugin.getDataFolder(), plugin.getConfig().getString("database.file", "data.db"));
            if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement st = conn.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS caches(
                      id TEXT PRIMARY KEY,
                      name TEXT,
                      world TEXT,
                      x REAL, y REAL, z REAL,
                      creator TEXT,
                      created_at INTEGER,
                      difficulty TEXT,
                      active INTEGER
                    );
                """);
                st.execute("""
                    CREATE TABLE IF NOT EXISTS finds(
                      cache_id TEXT,
                      player TEXT,
                      found_at INTEGER,
                      PRIMARY KEY(cache_id, player)
                    );
                """);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void close() {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }

    @Override
    public void saveCache(Cache c) {
        String sql = """
            INSERT INTO caches(id,name,world,x,y,z,creator,created_at,difficulty,active)
            VALUES(?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
              name=excluded.name, world=excluded.world, x=excluded.x, y=excluded.y, z=excluded.z,
              creator=excluded.creator, created_at=excluded.created_at,
              difficulty=excluded.difficulty, active=excluded.active
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getLocation().getWorld().getName());
            ps.setDouble(4, c.getLocation().getX());
            ps.setDouble(5, c.getLocation().getY());
            ps.setDouble(6, c.getLocation().getZ());
            ps.setString(7, c.getCreator() != null ? c.getCreator().toString() : null);
            ps.setLong(8, c.getCreatedAt());
            ps.setString(9, c.getDifficulty().name());
            ps.setInt(10, c.isActive() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteCache(String id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM caches WHERE id=?")) {
            ps.setString(1, id); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM finds WHERE cache_id=?")) {
            ps.setString(1, id); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Cache> loadCaches() {
        List<Cache> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM caches")) {
            while (rs.next()) {
                String id = rs.getString("id");
                String worldName = rs.getString("world");
                World w = Bukkit.getWorld(worldName);
                if (w == null) continue;
                Location loc = new Location(w, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"));
                Cache c = new Cache(
                    id,
                    rs.getString("name"),
                    loc,
                    rs.getString("creator") != null ? UUID.fromString(rs.getString("creator")) : null,
                    rs.getLong("created_at"),
                    Difficulty.valueOf(rs.getString("difficulty")),
                    rs.getInt("active") == 1
                );
                c.getFoundBy().addAll(loadFinds(id));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void recordFind(String cacheId, UUID player) {
        try (PreparedStatement ps = conn.prepareStatement(
            "INSERT OR IGNORE INTO finds(cache_id,player,found_at) VALUES(?,?,?)"
        )) {
            ps.setString(1, cacheId);
            ps.setString(2, player.toString());
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public Set<UUID> loadFinds(String cacheId) {
        Set<UUID> set = new HashSet<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT player FROM finds WHERE cache_id=?")) {
            ps.setString(1, cacheId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) set.add(UUID.fromString(rs.getString(1)));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return set;
    }

    @Override
    public int getFindCount(UUID player) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM finds WHERE player=?")) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public List<UUID> topFinders(int limit) {
        List<UUID> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
            "SELECT player, COUNT(*) c FROM finds GROUP BY player ORDER BY c DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(UUID.fromString(rs.getString("player")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}