package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.prefixbox.util.MyList;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;

public class BoxPAPIHandler extends EZPlaceholderHook {

    public BoxPAPIHandler(Main main) {
        super(main, "pbox");
    }

    @Override
    public String onPlaceholderRequest(Player who, String label) {
        Iterator<String> itr = Arrays.asList(label.split("_")).iterator();
        try {
            return Label.valueOf(itr.next().toUpperCase()).exec(who, itr);
        } catch (Exception ignore) {
        }
        return "null";
    }

    enum Label {

        ALL {
            String exec(Player p, Iterator<String> itr) {
                return "" + Main.getPlugin().getExecutor().getAll().size();
            }
        },

        OWNER {
            String exec(Player p, Iterator<String> itr) {
                MyList list = PrefixManager.INSTANCE.list(p);
                if (list == null) return "null";
                return "" + list.size();
            }
        },

        EXPIREDATE {
            String exec(Player p, Iterator<String> itr) {
                MyList list = PrefixManager.INSTANCE.list(p);
                if (list == null) return "null";
                int id = Integer.parseInt(itr.next());
                PrefixPlayerDefine def = list.get(id);
                if (def == null) return "null";
                return "" + DatePrintHelper.format(def.getOutdated(), "yyyy-MM-dd HH:mm:ss");
            }
        },

        REMAINING {
            String exec(Player p, Iterator<String> itr) {
                MyList list = PrefixManager.INSTANCE.list(p);
                if (list == null) return "null";
                int id = Integer.parseInt(itr.next());
                PrefixPlayerDefine def = list.get(id);
                if (def == null) return "null";
                return "" + (def.getOutdated().getTime() - System.currentTimeMillis());
            }
        };

        String exec(Player p, Iterator<String> itr) {
            throw new AbstractMethodError("exec");
        }
    }
}
