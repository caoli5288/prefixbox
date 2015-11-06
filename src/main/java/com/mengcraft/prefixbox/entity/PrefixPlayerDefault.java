package com.mengcraft.prefixbox.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created on 15-11-6.
 */
@Entity
public class PrefixPlayerDefault {

    @Id
    private int id;

    private String name;

    @OneToOne
    private PrefixPlayerDefine define;

    public PrefixPlayerDefine getDefine() {
        return define;
    }

    public void setDefine(PrefixPlayerDefine define) {
        this.define = define;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
