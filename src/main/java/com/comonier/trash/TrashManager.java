package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrashManager {

    private final Trash plugin;
    private final Map<UUID, Inventory> trashInventories = new HashMap<>();
    private final Set<UUID> pendingLava = new HashSet<>();
    private final Map<UUID, Boolean> playerNotifications = new HashMap<>();

    private FileConfiguration messages;
    private final File dataFolder;
    private final File logFolder;

    public TrashManager(Trash plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "data");
        this.logFolder = new File(plugin.getDataFolder(), "logs");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        if (!logFolder.exists()) logFolder.mkdirs();
    }

    public Set<Material> getMaterialSet(Inventory inv) {
        Set<Material> materials = new HashSet<>();
        if (inv == null) return materials;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                materials.add(item.getType());
            }
        }
        return materials;
    }

    public boolean isFiltered(Player player, Material type) {
        Inventory trashInv = getTrashInventory(player.getUniqueId());
        if (trashInv == null) return false;
        for (ItemStack filterItem : trashInv.getContents()) {
            if (filterItem != null && filterItem.getType() == type) return true;
        }
        return false;
    }

    public boolean toggleNotify(Player p) {
        boolean current = playerNotifications.getOrDefault(p.getUniqueId(), true);
        playerNotifications.put(p.getUniqueId(), !current);
        savePlayerData(p);
        return !current;
    }

    public void loadMessages() {
        String lang = plugin.getConfig().getString("language", "en");
        File msgFile = new File(plugin.getDataFolder(), "messages_" + lang + ".yml");
        if (!msgFile.exists()) {
            plugin.saveResource("messages_en.yml", false);
            plugin.saveResource("messages_pt.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(msgFile);
    }

    public String getRawMsg(String path) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(path, path));
    }

    public String getMsg(String path, String label) {
        String prefix = messages.getString("prefix", "");
        String msg = messages.getString(path, "");
        if (label != null && !label.isEmpty()) msg = msg.replace("%item", label);
        if (msg.contains("%date")) {
            msg = msg.replace("%date", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now()));
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    public void openTrash(Player player) {
        if (!trashInventories.containsKey(player.getUniqueId())) loadPlayerData(player);
        player.openInventory(trashInventories.get(player.getUniqueId()));
        player.sendMessage(getMsg("open-trash", ""));
    }

    public void savePlayerData(Player player) {
        try {
            File f = new File(dataFolder, player.getUniqueId() + ".yml");
            FileConfiguration cfg = new YamlConfiguration();
            Inventory inv = trashInventories.get(player.getUniqueId());
            if (inv != null) cfg.set("items", inv.getContents());
            cfg.set("notifications", playerNotifications.getOrDefault(player.getUniqueId(), true));
            cfg.save(f);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao salvar dados de " + player.getName());
        }
    }

    public void loadPlayerData(Player player) {
        File f = new File(dataFolder, player.getUniqueId() + ".yml");
        Inventory inv = Bukkit.createInventory(null, 54, getRawMsg("gui-trash-title"));
        if (f.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            List<?> list = cfg.getList("items");
            if (list != null) {
                ItemStack[] content = list.toArray(new ItemStack[0]);
                inv.setContents(content);
            }
            playerNotifications.put(player.getUniqueId(), cfg.getBoolean("notifications", true));
        }
        trashInventories.put(player.getUniqueId(), inv);
    }

    public void logDeletion(Player p, ItemStack item) {
        File logFile = new File(logFolder, DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDateTime.now()) + ".log");
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
            pw.println("[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "] " + p.getName() + " deletou: " + item.getAmount() + "x " + item.getType());
        } catch (IOException ignored) {}
    }

    public void purgeOldLogs() {
        File[] files = logFolder.listFiles();
        if (files == null) return;
        long limit = System.currentTimeMillis() - (5L * 24 * 60 * 60 * 1000);
        for (File f : files) { if (f.lastModified() < limit) f.delete(); }
    }

    public Inventory getTrashInventory(UUID uuid) { return trashInventories.get(uuid); }
    public boolean isPendingLava(UUID uuid) { return pendingLava.contains(uuid); }
    public void addPendingLava(UUID uuid) {
        pendingLava.add(uuid);
        Bukkit.getScheduler().runTaskLater(plugin, () -> pendingLava.remove(uuid), 600L);
    }
    public void removePendingLava(UUID uuid) { pendingLava.remove(uuid); }
}
