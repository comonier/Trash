package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class TrashData {
    private final TrashManager manager;
    private final File dataFolder;
    private final File logFolder;

    public TrashData(Trash plugin, TrashManager manager) {
        this.manager = manager;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.logFolder = new File(plugin.getDataFolder(), "logs");
        if (dataFolder.exists() == false) dataFolder.mkdirs();
        if (logFolder.exists() == false) logFolder.mkdirs();
    }

    public void logDeletion(Player p, ItemStack item) {
        try {
            File todayLog = new File(logFolder, new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".log");
            PrintWriter pw = new PrintWriter(new FileWriter(todayLog, true));
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            pw.println("[" + time + "] " + p.getName() + " incinerou: " + item.getType() + " x" + item.getAmount());
            pw.close();
        } catch (Exception ignored) {}
    }

    public void savePlayerData(Player player) {
        UUID id = player.getUniqueId();
        Inventory inv = manager.getTrashInventories().get(id);
        if (inv == null) return;
        try {
            File f = new File(dataFolder, id + ".yml");
            FileConfiguration cfg = new YamlConfiguration();
            ConfigurationSection slots = cfg.createSection("slots");
            for (int i = 0; 54 > i; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    slots.set(String.valueOf(i), item);
                }
            }
            cfg.set("notify-own", manager.isNotifyOwn(id));
            cfg.set("notify-all", manager.isNotifyAll(id));
            cfg.save(f);
        } catch (Exception ignored) {}
    }

    public void loadPlayerData(Player player) {
        UUID id = player.getUniqueId();
        File f = new File(dataFolder, id + ".yml");
        Inventory inv = Bukkit.createInventory(null, 54, manager.getRawMsg("gui-trash-title"));
        
        if (f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection section = cfg.getConfigurationSection("slots");
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    int slot = Integer.parseInt(key);
                    if (54 > slot) {
                        inv.setItem(slot, cfg.getItemStack("slots." + key));
                    }
                }
            }
            manager.setNotifyOwn(id, cfg.getBoolean("notify-own", true));
            manager.setNotifyAll(id, cfg.getBoolean("notify-all", true));
        } else {
            // Garante que novos jogadores comecem com avisos ativos
            manager.setNotifyOwn(id, true);
            manager.setNotifyAll(id, true);
            // Cria o arquivo inicial para persistir a preferencia
            savePlayerData(player);
        }
        manager.getTrashInventories().put(id, inv);
    }
}
