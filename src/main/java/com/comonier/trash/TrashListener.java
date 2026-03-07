package com.comonier.trash;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrashListener implements Listener {

    private final Trash plugin;
    private final TrashManager manager;
    private final Map<UUID, Set<Material>> beforeEdit = new HashMap<>();

    public TrashListener(Trash plugin, TrashManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        
        // TAREFA DE LIMPEZA CONTÍNUA (Para barrar Auto-Loot e Injeções diretas)
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                cleanPlayerInventory(player);
            }
        }, 5L, 5L);
    }

    private void cleanPlayerInventory(Player player) {
        Inventory trashInv = manager.getTrashInventory(player.getUniqueId());
        if (trashInv == null) return;

        Set<Material> filtered = manager.getMaterialSet(trashInv);
        if (filtered.isEmpty()) return;

        Inventory playerInv = player.getInventory();
        boolean removed = false;

        for (int i = 0; i < playerInv.getSize(); i++) {
            ItemStack item = playerInv.getItem(i);
            if (item != null && filtered.contains(item.getType())) {
                playerInv.setItem(i, null);
                removed = true;
            }
        }
        // Se removeu algo que entrou via Auto-Loot, podemos avisar (opcional)
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.loadPlayerData(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Material type = event.getItem().getItemStack().getType();

        if (manager.isFiltered(player, type)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        String trashTitle = manager.getRawMsg("gui-trash-title");
        if (event.getView().getTitle().equals(trashTitle)) {
            Inventory top = event.getView().getTopInventory();
            ItemStack toCheck = null;

            if (event.getRawSlot() > -1 && event.getRawSlot() < top.getSize()) {
                toCheck = event.getCursor();
            } else if (event.isShiftClick()) {
                toCheck = event.getCurrentItem();
            }

            if (toCheck == null || toCheck.getType() == Material.AIR) return;

            for (ItemStack item : top.getContents()) {
                if (item != null && item.getType() == toCheck.getType()) {
                    event.setCancelled(true);
                    if (event.getWhoClicked() instanceof Player) {
                        ((Player) event.getWhoClicked()).sendMessage(manager.getMsg("filter-duplicate", ""));
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getView().getTitle().equals(manager.getRawMsg("gui-trash-title"))) {
            beforeEdit.put(event.getPlayer().getUniqueId(), manager.getMaterialSet(event.getInventory()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String title = event.getView().getTitle();

        if (title.equals(manager.getRawMsg("gui-trash-title"))) {
            Set<Material> after = manager.getMaterialSet(event.getInventory());
            Set<Material> before = beforeEdit.remove(player.getUniqueId());

            if (before != null) {
                for (Material m : after) {
                    if (!before.contains(m)) {
                        player.sendMessage(manager.getMsg("filter-added", m.toString().toLowerCase().replace("_", " ")));
                    }
                }
                for (Material m : before) {
                    if (!after.contains(m)) {
                        player.sendMessage(manager.getMsg("filter-removed", m.toString().toLowerCase().replace("_", " ")));
                    }
                }
            }
            manager.savePlayerData(player);
        } else if (title.equals(manager.getRawMsg("gui-lava-title"))) {
            processLava(player, event.getInventory());
        }
    }

    private void processLava(Player player, Inventory inv) {
        boolean hasItems = false;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                hasItems = true;
                manager.logDeletion(player, item);
            }
        }
        if (hasItems) {
            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.0f, 1.0f);
            player.sendMessage(manager.getMsg("lava-destroyed", ""));
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage().toUpperCase();
        if (manager.isPendingLava(player.getUniqueId())) {
            if (msg.equals("YES") || msg.equals("SIM")) {
                event.setCancelled(true);
                manager.removePendingLava(player.getUniqueId());
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.openInventory(Bukkit.createInventory(null, 27, manager.getRawMsg("gui-lava-title")));
                    player.sendMessage(manager.getMsg("lava-opened", ""));
                });
            }
        }
    }
}
