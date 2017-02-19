package com.mengcraft.prefixbox.util;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on 15-11-6.
 */
public class MyList extends ArrayList<PrefixPlayerDefine> {

    public MyList(Collection<PrefixPlayerDefine> context) {
        super(context);
    }

    public String[] toStringArray() {
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + ">>> 您拥有的称号列表：");
        list.add(ChatColor.GOLD + "0. ( 空 )");
        list.add(ChatColor.GOLD + "-- 无限期");
        for (int i = 0; i < this.size(); ++i) {
            PrefixPlayerDefine def = get(i);
            list.add("" + ChatColor.GOLD + (i + 1) + ". " + def.getDefine().getName());
            list.add(ChatColor.GOLD + "-- " + (def.isOutdated() ? "已过期" : "剩" + getRemainDay(def.getOutdated().getTime()) + "天"));
            for (String lore : def.getDefine().getLoreList()) {
                list.add(ChatColor.GOLD + "-- " + lore);
            }
        }
        list.add(ChatColor.GOLD + "<<<");
        return list.toArray(new String[list.size()]);
    }

    public long getRemainDay(long time) {
        return (time - System.currentTimeMillis()) / 86400000;
    }

}
