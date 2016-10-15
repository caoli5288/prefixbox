package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.util.PrefixList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 15-12-10.
 */
public class PrefixManager {

    public static final PrefixManager INSTANCE = new PrefixManager();

    private final Map<String, PrefixPlayerDefault> playerDefaultCache = new ConcurrentHashMap<>();
    private final Map<String, PrefixList> playerCache = new ConcurrentHashMap<>();

    public Map<String, PrefixPlayerDefault> getPlayerDefaultCache() {
        return playerDefaultCache;
    }

    public Map<String, PrefixList> getPlayerCache() {
        return playerCache;
    }

}
