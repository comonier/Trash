package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrashListener implements Listener {
    private final Trash plugin;
    private final TrashManager manager;
    private final TrashData data;

    public TrashListener(Trash plugin, TrashManager manager, TrashData data) {
        this.plugin = plugin; 
        this.manager = manager; 
        this.data = data;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) { 
        data.loadPlayerData(e.getPlayer()); 
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(manager.getRawMsg("gui-trash-title")) == false) return;
        Inventory top = e.getView().getTopInventory();
        ItemStack cursor = e.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) return;
        if (e.getRawSlot() >= 0 && top.getSize() > e.getRawSlot()) {
            for (ItemStack item : top.getContents()) {
                if (item != null && item.getType() == cursor.getType()) {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage(manager.getMsg("filter-duplicate", ""));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (e.getView().getTitle().equals(manager.getRawMsg("gui-trash-title"))) {
            data.savePlayerData(p);
        } else if (e.getView().getTitle().equals(manager.getRawMsg("gui-lava-title"))) {
            boolean hasItems = false;
            for (ItemStack i : e.getInventory().getContents()) {
                if (i != null && i.getType() != Material.AIR) { 
                    hasItems = true; 
                    data.logDeletion(p, i); 
                }
            }
            if (hasItems) { 
                p.playSound(p.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f); 
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
                p.sendMessage(manager.getMsg("lava-destroyed", "").replace("%date", date)); 
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (manager.isPendingLava(p.getUniqueId()) == false) return;
        String m = e.getMessage().toUpperCase();
        if (m.equals("YES") || m.equals("SIM")) {
            e.setCancelled(true); 
            manager.removePendingLava(p.getUniqueId());
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.openInventory(Bukkit.createInventory(null, 27, manager.getRawMsg("gui-lava-title")));
                p.sendMessage(manager.getMsg("lava-opened", ""));
            });
        }
    }
}
