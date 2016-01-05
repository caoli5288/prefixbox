package com.mengcraft.prefixbox.event;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 15-12-11.
 */
public class PrefixChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player       player;
    private final PrefixDefine prefix;

    public PrefixChangeEvent(Player player, PrefixPlayerDefine prefix) {
        this.player = player;
        this.prefix = prefix == null ? null : prefix.getDefine();
    }

    public Player getPlayer() {
        return player;
    }

    public PrefixDefine getPrefix() {
        return prefix;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
