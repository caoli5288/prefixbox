package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 15-11-6.
 */
public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        EbeanHandler db = EbeanManager.DEFAULT.getHandler(this);

        if (!db.isInitialized()) {
            db.define(PrefixPlayerDefault.class);
            db.define(PrefixDefine.class);
            db.define(PrefixPlayerDefine.class);

            try {
                db.initialize();
            } catch (Exception e) {
                throw new RuntimeException("Can not init db connection!", e);
            }
        }

        db.install();
        db.reflect();

        new Executor(this, db).bind();
    }

    public void execute(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public void process(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

}
