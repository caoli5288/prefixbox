package com.mengcraft.prefixbox;

import com.mengcraft.prefixbox.entity.PrefixDefine;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefault;
import com.mengcraft.prefixbox.entity.PrefixPlayerDefine;
import com.mengcraft.prefixbox.event.PrefixChangeEvent;
import com.mengcraft.prefixbox.event.PrefixInitializedEvent;
import com.mengcraft.prefixbox.util.ListHelper;
import com.mengcraft.prefixbox.util.MyList;
import com.mengcraft.simpleorm.EbeanHandler;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.mengcraft.prefixbox.Main.nil;
import static java.lang.Integer.parseInt;

/**
 * Created on 15-11-6.
 */
public class Executor implements Listener, CommandExecutor, Runnable {

    private final Map<String, Long> time = new HashMap<>();
    private final Map<String, PrefixPlayerDefault> playerDefaultCache;
    private final Map<String, MyList> playerCache;
    private final long coolTime;
    private final Chat chat;
    private final EbeanHandler db;
    private final Main main;

    private List<PrefixDefine> all;
    private LibMark mark;

    public Executor(Main main, EbeanHandler db) {
        this.playerCache = PrefixManager.INSTANCE.getPlayerCache();
        this.playerDefaultCache = PrefixManager.INSTANCE.getPlayerDefaultCache();
        this.chat = main.getServer().getServicesManager().getRegistration(Chat.class).getProvider();
        this.main = main;
        this.db = db;
        this.coolTime = main.getConfig().getInt("coolDown", 1) * 60000L;
        all = db.find(PrefixDefine.class).findList();
    }

    public void setMark(LibMark mark) {
        this.mark = mark;
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
    public boolean onCommand(CommandSender sender, Command i, String label, String[] j) {
        if (label.equals("pbox")) {
            return use(sender, j);
        } else if (label.equals("prefixbox")) {
            return execute(sender, j);
        } else if (label.equals("pboxadmin")) {
            return admin(sender, j);
        }
        return false;
    }

    private boolean execute(CommandSender sender, String[] i) {
        if (sender instanceof Player) {
            Iterator<String> it = Arrays.asList(i).iterator();
            if (it.hasNext()) {
                int id = Integer.parseInt(it.next());
                if (it.hasNext()) throw new IllegalArgumentException();
                PrefixPlayerDefine def = find(sender, id);
                if (nil(def)) {
                    sender.sendMessage("§4您未拥有该称号或所选称号无法在当前区域使用");
                } else {
                    PrefixPlayerDefault j = playerDefaultCache.get(sender.getName());
                    if (nil(j)) throw new RuntimeException();
                    j.setDefine(def);
                    main.execute(() -> {
                        j.update(db);
                        main.process(() -> {
                            Player p = Player.class.cast(sender);
                            chat.setPlayerPrefix(p, def.getDefine().getName());
                            sender.sendMessage("§6称号已选择");
                            PermissionHolder.getHolder(p).setActivity(def.getDefine().getPermissionUse());
                            PrefixChangeEvent.call(p, def);
                        });
                    });
                }
            } else {
                sender.sendMessage("§6*** 全称号列表");
                for (PrefixDefine def : all) {
                    sender.sendMessage("§6> " + def.getId() + " " + def.getName());
                }
            }
        }
        return false;
    }

    private PrefixPlayerDefine find(CommandSender sender, int id) {
        MyList list = playerCache.get(sender.getName());
        if (list == null) return null;
        for (PrefixPlayerDefine def : list) {
            if (def.getDefine().getId() == id) return def;
        }
        return null;
    }

    private boolean admin(CommandSender sender, String[] i) {
        if (!sender.hasPermission("prefixbox.admin")) {
            return false;
        } else if (i.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "/pboxadmin list");
            sender.sendMessage(ChatColor.GOLD + "/pboxadmin reload");
            sender.sendMessage(ChatColor.GOLD + "/pboxadmin give <player> <prefix_id> <day> [mark]");
        } else if (i[0].equals("list") && i.length == 1) {
            return list(sender);
        } else if (i[0].equals("give")) {
            if (i.length == 4) {
                return give(sender, i[1], parseInt(i[2]), parseInt(i[3]), null);
            } else if (i.length == 5) {
                return give(sender, i[1], parseInt(i[2]), parseInt(i[3]), i[4]);
            }
        } else if (i[0].equals("reload")) {
            all = db.find(PrefixDefine.class).findList();
            sender.sendMessage("§a指令已完成");
            return true;
        }
        return false;
    }

    private boolean give(CommandSender sender, String name, int prefixId, int day, String mark) {
        PrefixDefine def = db.find(PrefixDefine.class, prefixId);

        // Return if prefix not exists!
        if (nil(def)) return false;

        PrefixPlayerDefine selected = db.find(PrefixPlayerDefine.class)
                .where()
                .eq("name", name)
                .eq("define", def)
                .gt("outdated", new Timestamp(System.currentTimeMillis()))
                .findUnique();

        if (nil(selected)) {
            PrefixPlayerDefine inserted = new PrefixPlayerDefine();
            inserted.setName(name);
            inserted.setMark(mark);
            inserted.setDefine(def);
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
        if (all.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "未找到已定义的称号项目");
        } else {
            sender.sendMessage(ChatColor.GOLD + ">>> 已定义的称号列表");
            for (PrefixDefine i : all) {
                sender.sendMessage("§6" + i.getId() + ". " + i.getName());
                for (String line : i.getLoreList()) {
                    sender.sendMessage("§6-- " + line);
                }
                for (PotionEffect buff : i.getBuffList()) {
                    sender.sendMessage("§6++ " + buff.getType().getName() + " lv" + buff.getAmplifier());
                }
                String hold = i.getPermissionHold();
                if (!nil(hold)) {
                    sender.sendMessage("§6** Hold permission " + hold);
                }
                String use = i.getPermissionUse();
                if (!nil(use)) {
                    sender.sendMessage("§6** Activity permission " + use);
                }
            }
            return true;
        }
        return false;
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
        long coolDown = getCoolDown(time.get(player.getName()));
        if (coolDown > 0) {
            player.sendMessage(ChatColor.RED + "称号切换冷却时间剩余" + coolDown / 1000 + "秒");

            return false;
        }

        MyList prefixList = getPlayerCache().get(player.getName());

        if (index < 0 || index > prefixList.size()) {
            player.sendMessage(ChatColor.RED + "参数输入有问题");

            return false;
        }

        PrefixPlayerDefault prefixDefault = playerDefaultCache.get(player.getName());
        PermissionHolder holder = PermissionHolder.getHolder(player);

        if (index == 0) {
            prefixDefault.setDefine(null);
            chat.setPlayerPrefix(player, "");
            holder.unsetActivity();
        } else {
            PrefixPlayerDefine selected = prefixList.get(index - 1);
            prefixDefault.setDefine(selected);
            chat.setPlayerPrefix(player, selected.getDefine().getName());
            player.addPotionEffects(prefixDefault.getDefine().getDefine().getBuffList());
            holder.setActivity(selected.getDefine().getPermissionUse());
        }
        // Update player's default prefix.
        main.execute(() -> prefixDefault.update(db));
        time.put(player.getName(), System.currentTimeMillis() + coolTime);

        PrefixChangeEvent.call(player, prefixDefault.getDefine());
        player.sendMessage(ChatColor.GOLD + "称号选择成功");

        return true;
    }

    private long getCoolDown(Long time) {
        return time == null ? 0 : time - System.currentTimeMillis();
    }

    private void setPrefix(Player player, String prefix) {
        chat.setPlayerPrefix(player, prefix);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        main.execute(() -> {
            Player player = event.getPlayer();

            List<PrefixPlayerDefine> l = db.find(PrefixPlayerDefine.class)
                    .where()
                    .eq("name", player.getName())
                    .gt("outdated", new Timestamp(System.currentTimeMillis()))
                    .findList();

            if (Main.debug) {
                main.getLogger().log(Level.INFO, "" + l);
            }

            MyList list = new MyList(process(l));

            PermissionHolder holder = PermissionHolder.getHolder(player);
            holder.addHold(list);

            getPlayerCache().put(player.getName(), list);

            PrefixPlayerDefault prefix = db.find(PrefixPlayerDefault.class)
                    .where()
                    .eq("name", player.getName())
                    .findUnique();

            if (prefix == null) {
                prefix = db.bean(PrefixPlayerDefault.class);
                prefix.setName(player.getName());
            } else {
                PrefixPlayerDefine def = prefix.getDefine();
                if (!nil(def)) {
                    if (def.isOutdated()) {
                        setPrefix(player, "");
                    } else if (!mark.match(def)) {
                        setPrefix(player, "");
                    } else {
                        holder.setActivity(def.getDefine().getPermissionUse());
                        setPrefix(player, def.getDefine().getName());
                    }
                }
            }

            playerDefaultCache.put(player.getName(), prefix);
            main.getServer().getPluginManager().callEvent(new PrefixInitializedEvent(player, prefix));
        });
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        dropCache(event.getPlayer().getName());
    }

    private Collection<PrefixPlayerDefine> process(Collection<PrefixPlayerDefine> list) {
        return ListHelper.reduce(list, line -> mark.match(line));
    }

    private void dropCache(String name) {
        playerDefaultCache.remove(name);
        playerCache.remove(name);
        time.remove(name);
    }

    public void bind() {
        main.getCommand("prefixbox").setExecutor(this);
        main.getServer().getPluginManager().registerEvents(this, main);
        main.getServer().getScheduler().runTaskTimer(main, this, 900, 900);
    }

    public Map<String, MyList> getPlayerCache() {
        return playerCache;
    }

}
