package com.comonier.trash;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Trash extends JavaPlugin implements CommandExecutor, TabCompleter {

    private TrashManager manager;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            this.manager = new TrashManager(this);
            this.manager.loadMessages();
            this.manager.purgeOldLogs();

            getCommand("trash").setExecutor(this);
            getCommand("trash").setTabCompleter(this);
            getCommand("lava").setExecutor(this);
            
            getServer().getPluginManager().registerEvents(new TrashListener(this, manager), this);
        } catch (Exception e) {
            getLogger().severe("Erro critico ao iniciar o plugin: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.manager = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (cmd.getName().equalsIgnoreCase("trash")) {
                if (!(sender instanceof Player)) return true;
                Player p = (Player) sender;

                if (args.length > 0) {
                    String sub = args[0].toLowerCase();
                    if (sub.equals("reload")) {
                        if (p.hasPermission("trash.admin") || p.isOp()) {
                            reloadConfig();
                            manager.loadMessages();
                            p.sendMessage("§a[Trash] Recarregado com sucesso!");
                        }
                        return true;
                    }
                    if (sub.equals("notify") || sub.equals("aviso")) {
                        boolean state = manager.toggleNotify(p);
                        p.sendMessage(manager.getMsg(state ? "notify-on" : "notify-off", ""));
                        return true;
                    }
                    p.sendMessage(manager.getMsg("usage", ""));
                    return true;
                }
                manager.openTrash(p);
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("lava")) {
                if (!(sender instanceof Player)) return true;
                manager.addPendingLava(((Player) sender).getUniqueId());
                sender.sendMessage(manager.getMsg("lava-confirm", ""));
                return true;
            }
        } catch (Exception e) {
            sender.sendMessage(manager.getMsg("error-occurred", ""));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("trash") && args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("aviso", "notify"));
            if (sender.hasPermission("trash.admin") || sender.isOp()) subs.add("reload");
            return subs.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
