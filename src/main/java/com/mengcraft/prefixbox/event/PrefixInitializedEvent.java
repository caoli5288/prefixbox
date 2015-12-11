package com.mengcraft.prefixbox.event;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

/**
 * Created on 15-12-11.
 */
public class PrefixInitializedEvent extends Event {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final PrefixPlayerDefault prefix;
    private final Player              player;

    public PrefixInitializedEvent(Player player, PrefixPlayerDefault prefix) {
        this.player = player;
        this.prefix = prefix;
    }

    public PrefixPlayerDefault getPrefix() {
        return prefix;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
