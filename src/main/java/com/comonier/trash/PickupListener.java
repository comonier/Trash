package com.comonier.trash;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.*;

public class PickupListener implements Listener {
    private final Trash plugin;
    private final TrashManager manager;
    private final Map<UUID, Set<Material>> alerted = new HashMap<>();
    private final Random random = new Random();

    public PickupListener(Trash plugin, TrashManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player == false) return;
        Player p = (Player) e.getEntity();
        Item itemEnt = e.getItem();
        ItemStack stack = itemEnt.getItemStack();

        Inventory inv = manager.getTrashInventories().get(p.getUniqueId());
        if (inv == null) return;

        boolean blocked = false;
        for (ItemStack filter : inv.getContents()) {
            if (filter != null && filter.getType() == stack.getType()) {
                blocked = true;
                break;
            }
        }

        if (blocked) {
            e.setCancelled(true);
            itemEnt.setPickupDelay(40);
            p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 0.6f, 1.2f);

            double vx = (random.nextDouble() - 0.5) * 0.8;
            double vz = (random.nextDouble() - 0.5) * 0.8;
            Vector launch = new Vector(vx, 0.5, vz).multiply(1.2);
            itemEnt.setVelocity(launch);

            Location ejectLoc = p.getLocation().add(launch.normalize().multiply(1.0));
            if (ejectLoc.getBlock().getType().isSolid() == false) {
                itemEnt.teleport(ejectLoc);
            }

            sendMsg(p, stack.getType());
        }
    }

    private void sendMsg(Player p, Material mat) {
        Set<Material> pAlerts = alerted.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        if (pAlerts.contains(mat)) return;
        pAlerts.add(mat);

        String itemLabel = mat.toString().toLowerCase().replace("_", " ");
        
        // Mensagem para o próprio jogador
        if (manager.isNotifyOwn(p.getUniqueId())) {
            String msgSelf = manager.getComplexMsg("blocked-msg-self", itemLabel, p.getName());
            p.sendMessage(msgSelf);
        }

        // Mensagem para jogadores próximos
        for (Entity nearby : p.getNearbyEntities(10, 10, 10)) {
            if (nearby instanceof Player && nearby.getUniqueId() != p.getUniqueId()) {
                Player other = (Player) nearby;
                if (manager.isNotifyAll(other.getUniqueId())) {
                    String msgOthers = manager.getComplexMsg("blocked-msg-others", itemLabel, p.getName());
                    other.sendMessage(msgOthers);
                }
            }
        }
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> pAlerts.remove(mat), 100L);
    }
}
