package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.util.MyList;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mengcraft.prefixbox.Main.nil;

/**
 * Created on 16-10-16.
 */
public class PermissionHolder {

    private final static Map<UUID, PermissionHolder> HOLDER = new HashMap<>();
    private final Player player;
    private final PermissionAttachment hold;
    private PermissionAttachment activity;

    private PermissionHolder(Player player) {
        this.player = player;
        hold = player.addAttachment(Main.getPlugin());
        HOLDER.put(player.getUniqueId(), this);
    }

    public void setActivity(String permission) {
        if (nil(permission)) {
            unsetActivity();
        } else {
            if (!nil(activity)) {
                activity.remove();
            }
            activity = player.addAttachment(Main.getPlugin(), permission, true);
        }
    }

    public void unsetActivity() {
        if (!nil(activity)) {
            activity.remove();
            activity = null;
        }
    }

    public void addHold(MyList list) {
        list.forEach(def -> {
            String i = def.getDefine().getPermissionHold();
            if (!nil(i)) {
                hold.setPermission(i, true);
            }
        });
    }

    public static PermissionHolder getHolder(Player player) {
        PermissionHolder holder = HOLDER.get(player.getUniqueId());
        if (nil(holder)) {
            holder = new PermissionHolder(player);
        }
        return holder;
    }

}
