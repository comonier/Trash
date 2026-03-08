package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Trash extends JavaPlugin implements CommandExecutor, TabCompleter {

    private TrashManager manager;
    private TrashData data;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            this.manager = new TrashManager(this);
            this.data = new TrashData(this, manager);
            this.manager.loadMessages();
            this.manager.purgeOldLogs();

            getCommand("trash").setExecutor(this);
            getCommand("trash").setTabCompleter(this);
            getCommand("lava").setExecutor(this);
            getCommand("to").setExecutor(this);
            getCommand("ta").setExecutor(this);
            
            getServer().getPluginManager().registerEvents(new TrashListener(this, manager, data), this);
            getServer().getPluginManager().registerEvents(new PickupListener(this, manager), this);

            for (Player p : Bukkit.getOnlinePlayers()) {
                data.loadPlayerData(p);
            }
        } catch (Exception e) {
            getLogger().severe("Erro critico: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (data != null && manager != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                data.savePlayerData(p);
            }
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player == false) return true;
        Player p = (Player) sender;
        UUID id = p.getUniqueId();
        String name = cmd.getName().toLowerCase();

        if (name.equals("to")) {
            manager.toggleOwn(id);
            data.savePlayerData(p);
            String key = manager.isNotifyOwn(id) ? "notify-own-on" : "notify-own-off";
            p.sendMessage(manager.getMsg(key, ""));
            return true;
        }

        if (name.equals("ta")) {
            manager.toggleAll(id);
            data.savePlayerData(p);
            String key = manager.isNotifyAll(id) ? "notify-all-on" : "notify-all-off";
            p.sendMessage(manager.getMsg(key, ""));
            return true;
        }

        if (name.equals("trash")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (p.hasPermission("trash.admin") || p.isOp()) {
                    for (Player online : Bukkit.getOnlinePlayers()) data.savePlayerData(online);
                    reloadConfig();
                    manager.loadMessages();
                    p.sendMessage(manager.getRawMsg("prefix") + " §aReload completo!");
                }
                return true;
            }
            if (manager.getTrashInventories().containsKey(id) == false) data.loadPlayerData(p);
            p.openInventory(manager.getTrashInventories().get(id));
            p.sendMessage(manager.getMsg("open-trash", ""));
            return true;
        }

        if (name.equals("lava")) {
            manager.addPendingLava(id);
            p.sendMessage(manager.getMsg("lava-confirm", ""));
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("trash") && args.length == 1) {
            List<String> subs = new ArrayList<>();
            if (sender.hasPermission("trash.admin")) subs.add("reload");
            String currentArg = args[0].toLowerCase();
            return subs.stream().filter(s -> s.startsWith(currentArg)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
