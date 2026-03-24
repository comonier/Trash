package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Trash extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("lava").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        int size = 27; // Tamanho fixo ou via config
        String title = color(getConfig().getString("gui-lava-title"));
        
        Inventory lavaGui = Bukkit.createInventory(null, size, title);
        player.openInventory(lavaGui);
        player.sendMessage(getMsg("lava-opened"));
        
        return true;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        String title = color(getConfig().getString("gui-lava-title"));
        
        if (event.getView().getTitle().equals(title)) {
            boolean hasItems = false;
            for (ItemStack item : event.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    hasItems = true;
                    break;
                }
            }
            
            if (hasItems) {
                event.getInventory().clear();
                event.getPlayer().sendMessage(getMsg("lava-destroyed"));
            }
        }
    }

    private String getMsg(String path) {
        String prefix = getConfig().getString("prefix");
        String msg = getConfig().getString(path);
        return color(prefix + " " + msg);
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
