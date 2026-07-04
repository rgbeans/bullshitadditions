package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class RifleListener implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!Rifle.isRifle(item)) return;

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        long now = System.currentTimeMillis();
        Long last = cooldowns.get(player.getUniqueId());
        if (last != null && now - last < Rifle.COOLDOWN_MILLIS) {
            return;
        }

        if (!consumeAmmo(player)) {
            player.sendActionBar(ChatColor.RED + "You need gold nuggets for ammo!");
            return;
        }

        cooldowns.put(player.getUniqueId(), now);
        fire(player);
    }

    private boolean consumeAmmo(Player player) {
        PlayerInventory inv = player.getInventory();
        int index = inv.first(Rifle.AMMO_TYPE);
        if (index != -1) {
            ItemStack slot = inv.getItem(index);
            if (slot.getAmount() == 1) {
                inv.setItem(index, null);
            } else {
                slot.setAmount(slot.getAmount() - 1);
            }
            return true;
        }

        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (AmmoBox.isAmmoBox(item) && AmmoBox.consumeAmmo(item, Rifle.AMMO_TYPE, 1)) {
                return true;
            }
        }

        return false;
    }

    private void fire(Player player) {
        Location eye = player.getEyeLocation();
        Vector direction = eye.getDirection();

        Snowball projectile = player.getWorld().spawn(eye, Snowball.class);
        projectile.setShooter(player);
        projectile.setVelocity(direction.multiply(Rifle.PROJECTILE_SPEED));
        projectile.getPersistentDataContainer().set(GunListener.PROJECTILE_KEY, PersistentDataType.BOOLEAN, true);
        projectile.getPersistentDataContainer().set(GunListener.DAMAGE_KEY, PersistentDataType.DOUBLE, Rifle.DAMAGE);
        projectile.setItem(new ItemStack(Rifle.AMMO_TYPE));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.5f, 1.8f);
    }
}
