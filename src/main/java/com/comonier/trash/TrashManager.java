package com.comonier.trash;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import java.io.File;
import java.util.*;

public class TrashManager {
    private final Trash plugin;
    private final Map<UUID, Inventory> trashInventories = new HashMap<>();
    private final Map<UUID, Boolean> notifyOwn = new HashMap<>();
    private final Map<UUID, Boolean> notifyAll = new HashMap<>();
    private final Set<UUID> pendingLava = new HashSet<>();
    private FileConfiguration messages;

    public TrashManager(Trash plugin) { this.plugin = plugin; }

    public boolean isNotifyOwn(UUID id) { return notifyOwn.getOrDefault(id, true); }
    public boolean isNotifyAll(UUID id) { return notifyAll.getOrDefault(id, true); }
    public void setNotifyOwn(UUID id, boolean val) { notifyOwn.put(id, val); }
    public void setNotifyAll(UUID id, boolean val) { notifyAll.put(id, val); }

    public void toggleOwn(UUID id) { notifyOwn.put(id, isNotifyOwn(id) == false); }
    public void toggleAll(UUID id) { notifyAll.put(id, isNotifyAll(id) == false); }

    public void addPendingLava(UUID id) { pendingLava.add(id); }
    public boolean isPendingLava(UUID id) { return pendingLava.contains(id); }
    public void removePendingLava(UUID id) { pendingLava.remove(id); }

    public void loadMessages() {
        String lang = plugin.getConfig().getString("language", "pt");
        File msgFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (msgFile.exists() == false) {
            plugin.saveResource("messages_en.yml", false);
            plugin.saveResource("messages_pt.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(msgFile);
    }

    public String getRawMsg(String path) { 
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path, path)); 
    }

    public String getMsg(String path, String itemLabel) {
        String msg = messages.getString(path, "").replace("%item", itemLabel);
        return ChatColor.translateAlternateColorCodes('&', messages.getString("prefix", "") + " " + msg);
    }

    public String getComplexMsg(String path, String itemLabel, String playerName) {
        String msg = messages.getString(path, "")
            .replace("%item", itemLabel)
            .replace("%player", playerName);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void purgeOldLogs() {
        File folder = new File(plugin.getDataFolder(), "logs");
        if (folder.exists() == false) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (System.currentTimeMillis() - f.lastModified() > 604800000L) f.delete();
        }
    }

    public Map<UUID, Inventory> getTrashInventories() { return trashInventories; }
}
