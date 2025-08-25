package com.plugincraft.geocaching.data;

import com.plugincraft.geocaching.model.Cache;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataStore {
    void init();
    void close();

    // caches
    void saveCache(Cache c);
    void deleteCache(String id);
    List<Cache> loadCaches();

    // finds
    void recordFind(String cacheId, UUID player);
    Set<UUID> loadFinds(String cacheId);

    // leaderboards
    int getFindCount(UUID player);
    List<UUID> topFinders(int limit);
}