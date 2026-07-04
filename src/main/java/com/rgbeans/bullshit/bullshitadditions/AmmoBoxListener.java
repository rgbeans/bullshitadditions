package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class AmmoBoxListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = event.getItem();
        if (!AmmoBox.isAmmoBox(item)) return;

        event.setUseItemInHand(Event.Result.DENY);

        Player player = event.getPlayer();
        int slot = player.getInventory().getHeldItemSlot();

        AmmoBox.open(player, item, slot);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (AmmoBox.isAmmoBox(event.getItemInHand())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Ammo Box")) return;

        if (event.isShiftClick()) {
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && !clicked.isEmpty() && !AmmoBox.AMMO_TYPES.contains(clicked.getType())) {
                event.setCancelled(true);
            }
            return;
        }

        if (event.getRawSlot() >= 9) return;

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        boolean cursorOk = cursor == null || cursor.getType() == Material.AIR || AmmoBox.AMMO_TYPES.contains(cursor.getType());
        boolean currentOk = current == null || current.getType() == Material.AIR || AmmoBox.AMMO_TYPES.contains(current.getType());

        if (!cursorOk || !currentOk) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals("Ammo Box")) return;

        ItemStack dragged = event.getOldCursor();
        if (dragged != null && !dragged.isEmpty() && !AmmoBox.AMMO_TYPES.contains(dragged.getType())) {
            for (int slot : event.getRawSlots()) {
                if (slot < 9) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals("Ammo Box")) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        AmmoBox.saveAndClose(player);
    }
}
