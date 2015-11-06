package com.mengcraft.prefixbox.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

/**
 * Created on 15-11-6.
 */
@Entity
public class PrefixPlayerDefine {

    @Id
    private int id;

    @Column
    private java.lang.String name;

    @OneToOne
    private PrefixDefine define;

    @Column
    private Timestamp outdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public PrefixDefine getDefine() {
        return define;
    }

    public void setDefine(PrefixDefine define) {
        this.define = define;
    }

    public Timestamp getOutdated() {
        return outdated;
    }

    public void setOutdated(Timestamp outdated) {
        this.outdated = outdated;
    }

    public boolean isOutdated() {
        return getOutdated().getTime() < System.currentTimeMillis();
    }

}
