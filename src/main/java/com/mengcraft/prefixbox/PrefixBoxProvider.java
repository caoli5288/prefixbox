package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import org.bukkit.entity.Player;

/**
 * Created on 15-12-10.
 */
public interface PrefixBoxProvider {

    PrefixDefine getPlayerCurrentPrefix(Player player);

}
