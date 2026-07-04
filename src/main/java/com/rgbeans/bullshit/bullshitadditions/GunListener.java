package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GunListener implements Listener {

    static final NamespacedKey PROJECTILE_KEY = new NamespacedKey("bullshitadditions", "gun_projectile");
    static final NamespacedKey DAMAGE_KEY = new NamespacedKey("bullshitadditions", "gun_damage");
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
        if (!Pistol.isGun(item)) return;

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        long now = System.currentTimeMillis();
        Long last = cooldowns.get(player.getUniqueId());
        if (last != null && now - last < Pistol.COOLDOWN_MILLIS) {
            return;
        }

        if (!consumeAmmo(player)) {
            player.sendActionBar(ChatColor.RED + "You need iron nuggets for ammo!");
            return;
        }

        cooldowns.put(player.getUniqueId(), now);
        fire(player);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;
        if (!snowball.getPersistentDataContainer().has(PROJECTILE_KEY, PersistentDataType.BOOLEAN)) return;

        snowball.getWorld().playSound(snowball.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2.0f);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball snowball)) return;
        if (!snowball.getPersistentDataContainer().has(PROJECTILE_KEY, PersistentDataType.BOOLEAN)) return;

        double damage = Pistol.DAMAGE;
        if (snowball.getPersistentDataContainer().has(DAMAGE_KEY, PersistentDataType.DOUBLE)) {
            damage = snowball.getPersistentDataContainer().get(DAMAGE_KEY, PersistentDataType.DOUBLE);
        }
        event.setDamage(damage);
    }

    private boolean consumeAmmo(Player player) {
        PlayerInventory inv = player.getInventory();
        int index = inv.first(Pistol.AMMO_TYPE);
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
            if (AmmoBox.isAmmoBox(item) && AmmoBox.consumeAmmo(item, Pistol.AMMO_TYPE, 1)) {
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
        projectile.setVelocity(direction.multiply(Pistol.PROJECTILE_SPEED));
        projectile.getPersistentDataContainer().set(PROJECTILE_KEY, PersistentDataType.BOOLEAN, true);
        projectile.getPersistentDataContainer().set(DAMAGE_KEY, PersistentDataType.DOUBLE, Pistol.DAMAGE);
        projectile.setItem(new ItemStack(Pistol.AMMO_TYPE));

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.7f, 1.5f);
    }
}
