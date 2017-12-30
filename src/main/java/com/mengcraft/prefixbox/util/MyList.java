package com.mengcraft.prefixbox.util;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import lombok.val;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created on 15-11-6.
 */
public class MyList extends HashMap<Integer, PrefixPlayerDefine> {

    public MyList(Collection<PrefixPlayerDefine> list) {
        list.forEach(l -> put(l.getDefine().getId(), l));
    }

    public String[] toStringArray() {
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GOLD + ">>> 您拥有的称号列表：");
        list.add(ChatColor.GOLD + "0. ( 空 )");
        list.add(ChatColor.GOLD + "-- 无限期");
        val l = new ArrayList<PrefixPlayerDefine>(values());
        for (int i = 0; i < size(); ++i) {
            PrefixPlayerDefine def = l.get(i);
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
