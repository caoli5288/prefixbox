package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.prefixbox.event.PrefixChangeEvent;
import com.mengcraft.prefixbox.event.PrefixInitializedEvent;
import com.mengcraft.prefixbox.util.CollectionUtil;
import com.mengcraft.prefixbox.util.PrefixList;
import com.mengcraft.simpleorm.EbeanHandler;
import com.wodogs.mc.mark.Mark;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Created on 15-11-6.
 */
public class Executor implements Listener, CommandExecutor, Runnable {

    private final Map<String, Long> coolDownMap = new HashMap<>();
    private final Map<String, PrefixPlayerDefault> playerDefaultCache;
    private final Map<String, PrefixList> playerCache;
    private final long coolDownTime;
    private final Chat chat;
    private final Mark mark = Mark.DEFAULT;
    private final Main main;
    private final EbeanHandler db;

    public Executor(Main main, EbeanHandler db) {
        this.playerCache = PrefixManager.INSTANCE.getPlayerCache();
        this.playerDefaultCache = PrefixManager.INSTANCE.getPlayerDefaultCache();
        this.chat = main.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        this.main = main;
        this.db = db;
        this.coolDownTime = main.getConfig().getInt("coolDown", 1) * 60000L;
    }

    @Override
    public void run() {
        playerDefaultCache.forEach((name, prefix) -> {
            if (prefix.getDefine() != null && !prefix.getDefine().isOutdated()) {
                Player p = main.getServer().getPlayerExact(name);
                for (PotionEffect buff : prefix.getDefine().getDefine().getBuffList()) {
                    p.addPotionEffect(buff, true);
                }
            }
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        return label.equals("pbox") ? use(sender, arguments) : label.equals("pboxadmin") && admin(sender, arguments);
    }

    private boolean admin(CommandSender sender, String[] args) {
        if (!sender.hasPermission("prefixbox.admin")) {
            return false;
        } else if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "/pboxadmin list");
            sender.sendMessage(ChatColor.GOLD + "/pboxadmin give <player> <prefix_id> <day> [mark]");
        } else if (args[0].equals("list") && args.length == 1) {
            return list(sender);
        } else if (args[0].equals("give")) {
            if (args.length == 4) {
                return give(sender, args[1], parseInt(args[2]), parseInt(args[3]), null);
            } else if (args.length == 5) {
                return give(sender, args[1], parseInt(args[2]), parseInt(args[3]), args[4]);
            }
        }
        return true;
    }

    private boolean give(CommandSender sender, String name, int prefixId, int day, String mark) {
        PrefixDefine prefixDefine = db.find(PrefixDefine.class, prefixId);

        // Return if prefix not exists!
        if (prefixDefine == null) return false;

        PrefixPlayerDefine selected = db.find(PrefixPlayerDefine.class)
                .where()
                .eq("name", name)
                .eq("define", prefixDefine)
                .gt("outdated", new Timestamp(System.currentTimeMillis()))
                .findUnique();

        if (selected == null) {
            PrefixPlayerDefine inserted = new PrefixPlayerDefine();
            inserted.setName(name);
            inserted.setMark(mark);
            inserted.setDefine(prefixDefine);
            inserted.setOutdated(new Timestamp(System.currentTimeMillis() + day * 86400000L));

            main.execute(() -> db.insert(inserted));
        } else {
            selected.setOutdated(new Timestamp(selected.getOutdated().getTime() + day * 86400000L));

            main.execute(() -> db.save(selected));
        }

        sender.sendMessage(ChatColor.GOLD + "DONE!");

        return true;
    }

    private boolean list(CommandSender sender) {
        List<PrefixDefine> list = db.find(PrefixDefine.class).findList();

        sender.sendMessage(ChatColor.GOLD + ">>> 已定义的称号列表");
        for (PrefixDefine prefix : list) {
            sender.sendMessage("§6" + prefix.getId() + ". " + prefix.getName());
            for (String line : prefix.getLoreList()) {
                sender.sendMessage("§6-- " + line);
            }
            for (PotionEffect buff : prefix.getBuffList()) {
                sender.sendMessage("§6++ " + buff.getType().getName() + " lv" + buff.getAmplifier());
            }
        }
        sender.sendMessage(ChatColor.GOLD + "<<<");

        return list.size() != 0;
    }

    private boolean use(CommandSender sender, String[] arguments) {
        if (sender instanceof Player) {
            if (arguments.length == 0) {
                sender.sendMessage(playerCache.get(sender.getName()).toStringArray());
            } else if (arguments.length == 1) try {
                return use(((Player) sender), parseInt(arguments[0]));
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "发生了一些问题" + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean use(Player player, int index) {
        long coolDown = getCoolDown(coolDownMap.get(player.getName()));
        if (coolDown > 0) {
            player.sendMessage(ChatColor.RED + "称号切换冷却时间剩余" + coolDown / 1000 + "秒");

            return false;
        }

        PrefixList prefixList = getPlayerCache().get(player.getName());

        if (index < 0 || index > prefixList.size()) {
            player.sendMessage(ChatColor.RED + "发生了一些问题");

            return false;
        }

        PrefixPlayerDefault prefixDefault = getPlayerDefaultCache().get(player.getName());

        if (index == 0) {
            prefixDefault.setDefine(null);
            chat.setPlayerPrefix(player, "");
        } else {
            PrefixPlayerDefine selected = prefixList.get(index - 1);
            prefixDefault.setDefine(selected);
            chat.setPlayerPrefix(player, selected.getDefine().getName());
        }
        // Update player's default prefix.
        main.execute(() -> prefixDefault.update(db));

        if (prefixDefault.getDefine() != null) {
            player.addPotionEffects(prefixDefault.getDefine().getDefine().getBuffList());
        }

        coolDownMap.put(player.getName(), System.currentTimeMillis() + coolDownTime);

        player.sendMessage(ChatColor.GOLD + "称号选择成功");

        main.getServer().getPluginManager().callEvent(new PrefixChangeEvent(player, prefixDefault.getDefine()));

        return true;
    }

    private long getCoolDown(Long time) {
        return time == null ? 0 : time - System.currentTimeMillis();
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        main.execute(() -> {
            Collection<PrefixPlayerDefine> list = process(db.find(PrefixPlayerDefine.class)
                    .where()
                    .eq("name", event.getPlayer().getName())
                    .gt("outdated", new Timestamp(System.currentTimeMillis()))
                    .findList()
            );

            getPlayerCache().put(event.getPlayer().getName(), new PrefixList(list));

            PrefixPlayerDefault prefix = db.find(PrefixPlayerDefault.class)
                    .where()
                    .eq("name", event.getPlayer().getName())
                    .findUnique();

            if (prefix == null) {
                prefix = a(event.getPlayer());
            }

            getPlayerDefaultCache().put(event.getPlayer().getName(), prefix);

            if (prefix != null && prefix.getDefine() != null && (prefix.getDefine().isOutdated() || (prefix.getDefine().hasMark() && !prefix.getDefine().getMark().equals(mark.getMark())))) {
                chat.setPlayerPrefix(event.getPlayer(), "");
            }

            main.getServer().getPluginManager().callEvent(new PrefixInitializedEvent(event.getPlayer(), prefix));
        });
    }

    private Collection<PrefixPlayerDefine> process(Collection<PrefixPlayerDefine> list) {
        Collection<PrefixPlayerDefine> reduced = CollectionUtil.reduce(list, line -> line.hasNoMark() || line.getMark().equals(mark.getMark()));
        return reduced;
    }

    private PrefixPlayerDefault a(Player player) {
        PrefixPlayerDefault prefix = db.bean(PrefixPlayerDefault.class);
        prefix.setName(player.getName());
        return prefix;
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        dropCache(event.getPlayer().getName());
    }

    private void dropCache(String name) {
        playerDefaultCache.remove(name);
        playerCache.remove(name);
        coolDownMap.remove(name);
    }

    public Map<String, PrefixPlayerDefault> getPlayerDefaultCache() {
        return playerDefaultCache;
    }

    public void bind() {
        main.getCommand("prefixbox").setExecutor(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        main.getServer().getScheduler().runTaskTimer(main, this, 900, 900);
    }

    public Map<String, PrefixList> getPlayerCache() {
        return playerCache;
    }

}
