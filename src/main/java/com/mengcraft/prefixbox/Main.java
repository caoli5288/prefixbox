package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import com.wodogs.mc.mark.Mark;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 15-11-6.
 */
public class Main extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

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

        Executor executor = new Executor(this, db);
        Plugin mark = getServer().getPluginManager().getPlugin("mark");
        if (nil(mark)) {
            executor.setMark(def -> !def.hasMark());
        } else {
            executor.setMark(def -> !def.hasMark() || Mark.DEFAULT.getMark().equals(def.getMark()));
        }
        executor.bind();
    }

    public void execute(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public void process(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

    public static boolean nil(Object i) {
        return i == null;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

}
