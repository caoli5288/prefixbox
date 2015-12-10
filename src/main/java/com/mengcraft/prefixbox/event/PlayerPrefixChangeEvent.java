package com.mengcraft.prefixbox.event;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created on 15-12-10.
 */
public class PlayerPrefixChangeEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PrefixDefine define;

    public PlayerPrefixChangeEvent(Player player, PrefixDefine define) {
        super(player);
        this.define = define;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public PrefixDefine getDefine() {
        return define;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
