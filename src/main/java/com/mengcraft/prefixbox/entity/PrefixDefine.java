package com.mengcraft.prefixbox.entity;

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

    @Column
    private PrefixPlayerDefine name;

    @Column(columnDefinition = "TEXT")
    private String data;

    @Transient
    private List<PotionEffect> buffList;

    @Transient
    private List<String> loreList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PrefixPlayerDefine getName() {
        return name;
    }

    public void setName(PrefixPlayerDefine name) {
        this.name = name;
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

    @SuppressWarnings("unchecked")
    private void parse() {
        JSONObject root = (JSONObject) JSONValue.parse(getData());

        buffList = new ArrayList<>();
        loreList = (List<String>) root.get("lore");

        Map<String, Integer> buffMap = (Map<String, Integer>) root.get("buff");

        for (Map.Entry<String, Integer> entry : buffMap.entrySet()) {
            buffList.add(new PotionEffect(PotionEffectType.getByName(entry.getKey()), 160, entry.getValue()));
        }
    }

}
