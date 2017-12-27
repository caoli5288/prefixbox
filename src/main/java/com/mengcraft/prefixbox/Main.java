package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import com.wodogs.mc.mark.Mark;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 15-11-6.
 */
@Getter
public class Main extends JavaPlugin {

    public static Plugin plugin;
    public static boolean debug;
    private Executor executor;

    @Override
    public void onEnable() {
        plugin = this;
        debug = getConfig().getBoolean("debug");

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

        executor = new Executor(this, db);
        Plugin mark = getServer().getPluginManager().getPlugin("Mark");
        if (nil(mark)) {
            executor.setMark(def -> !def.hasMark());
        } else {
            String e = Mark.DEFAULT.getMark();
            getLogger().info("获取到MARK -> " + e);
            executor.setMark(def -> {
                if (def.hasMark()) {
                    return def.getMark().equals(e);
                } else {
                    return true;
                }
            });
        }
        executor.bind();

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new BoxPlaceholderHandler(this).hook();
        }
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
