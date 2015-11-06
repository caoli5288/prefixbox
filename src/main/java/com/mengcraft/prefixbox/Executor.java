package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.prefixbox.util.PrefixList;
import com.mengcraft.simpleorm.EbeanHandler;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 15-11-6.
 */
public class Executor implements Listener, CommandExecutor {

    private final Map<String, PrefixPlayerDefine> defaultCache = new ConcurrentHashMap<>();
    private final Map<String, PrefixList> playerCache = new ConcurrentHashMap<>();

    private final Chat chat;
    private final Main main;
    private final EbeanHandler db;

    public Executor(Main main, EbeanHandler db) {
        this.chat = main.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        this.main = main;
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        if (label.equals("pbox")) {
            return use(sender, arguments);
        }
        return false;
    }

    private boolean use(CommandSender sender, String[] arguments) {
        if (sender instanceof ConsoleCommandSender) {
            return false;
        } else if (arguments.length == 0) {
            sender.sendMessage(playerCache.get(sender.getName()).toStringArray());
        } else try {
            // TODO
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "发生了一些问题。" + ex.getMessage());
        }
        return true;
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        main.execute(() -> {
            List<PrefixPlayerDefine> list = db.find(PrefixPlayerDefine.class)
                    .where()
                    .eq("name", event.getPlayer().getName())
                    .findList();
            getPlayerCache().put(event.getPlayer().getName(), new PrefixList(list));

            PrefixPlayerDefault prefix = db.find(PrefixPlayerDefault.class)
                    .where()
                    .eq("name", event.getPlayer().getName())
                    .findUnique();
            if (prefix == null) {
                // main.getLogger().info("[Executor]");
            } else if (prefix.getDefine().isOutdated()) {
                getChat().setPlayerPrefix(event.getPlayer(), "§r");
            } else {
                getDefaultCache().put(event.getPlayer().getName(), prefix.getDefine());
            }
        });
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        dropCache(event.getPlayer().getUniqueId());
    }

    private void dropCache(UUID uuid) {
        defaultCache.remove(uuid);
        playerCache.remove(uuid);
    }

    public Map<String, PrefixPlayerDefine> getDefaultCache() {
        return defaultCache;
    }

    public Chat getChat() {
        return chat;
    }

    public void bind() {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    public Map<String, PrefixList> getPlayerCache() {
        return playerCache;
    }

}
