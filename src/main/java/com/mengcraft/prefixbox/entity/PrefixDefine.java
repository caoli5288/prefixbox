package com.mengcraft.prefixbox.entity;

import com.mengcraft.prefixbox.Main;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created on 15-11-6.
 */
@Entity
public class PrefixDefine {

    @Id
    private int id;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Transient
    private String name;

    @Transient
    private List<PotionEffect> buffList;

    @Transient
    private List<String> loreList;

    @Transient
    private JSONObject root;

    private static class Permission {
        private String hold;
        private String use;

        private Permission(Map<String, String> map) {
            if (!Main.nil(map)) {
                hold = map.get("hold");
                use = map.get("use");
            }
        }
    }

    @Transient
    private Permission permission;

    public String getPermissionHold() {
        return permission.hold;
    }

    public String getPermissionUse() {
        return permission.use;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setBuffList(List<PotionEffect> buffList) {
        this.buffList = buffList;
    }

    public List<PotionEffect> getBuffList() {
        if (buffList == null) {
            parse();
        }
        return buffList;
    }

    public List<String> getLoreList() {
        if (loreList == null) {
            parse();
        }
        return loreList;
    }

    public String getName() {
        if (name == null) {
            parse();
        }
        return name;
    }

    public JSONObject getRoot() {
        if (root == null) {
            parse();
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private void parse() {
        if (getData() != null) {
            root = (JSONObject) JSONValue.parse(getData());

            buffList = new ArrayList<>();
            loreList = (List<String>) root.get("lore");
            name = root.get("name").toString();

            permission = new Permission((Map) root.get("permission"));

            Map<String, Long> buffMap = (Map<String, Long>) root.get("buff");

            for (Map.Entry<String, Long> entry : buffMap.entrySet()) {
                buffList.add(new PotionEffect(PotionEffectType.getByName(entry.getKey()), 1200, entry.getValue().intValue()));
            }
        }
    }

}
