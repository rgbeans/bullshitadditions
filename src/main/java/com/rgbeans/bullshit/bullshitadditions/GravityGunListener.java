package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class GravityGunListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, UUID> held = new HashMap<>();
    private final Map<UUID, Long> lastAction = new HashMap<>();

    private static final long ACTION_COOLDOWN_MS = 200;

    public GravityGunListener(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    private boolean onCooldown(Player player) {
        long now = System.currentTimeMillis();
        Long last = lastAction.get(player.getUniqueId());
        if (last != null && now - last < ACTION_COOLDOWN_MS) {
            return true;
        }
        lastAction.put(player.getUniqueId(), now);
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!GravityGun.isGravityGun(event.getItem())) return;

        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) return;

        event.setCancelled(true);

        boolean holding = held.containsKey(player.getUniqueId());
        if (left) {
            if (holding) release(player);
            return;
        }

        if (onCooldown(player)) return;
        if (holding) {
            shoot(player);
        } else {
            grabByRay(player);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) return;

        event.setCancelled(true);

        if (onCooldown(player)) return;
        if (held.containsKey(player.getUniqueId())) {
            shoot(player);
        } else {
            grab(player, event.getRightClicked());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) return;

        UUID targetId = held.get(player.getUniqueId());
        if (targetId != null && targetId.equals(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            release(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        held.remove(event.getPlayer().getUniqueId());
        lastAction.remove(event.getPlayer().getUniqueId());
    }

    private void grabByRay(Player player) {
        Location eye = player.getEyeLocation();
        RayTraceResult result = player.getWorld().rayTraceEntities(
                eye, eye.getDirection(), GravityGun.GRAB_RANGE, 0.5,
                entity -> !entity.equals(player) && !(entity instanceof Projectile));

        if (result == null || result.getHitEntity() == null) {
            player.sendActionBar(ChatColor.RED + "No entity within reach!");
            return;
        }
        grab(player, result.getHitEntity());
    }

    private void grab(Player player, Entity target) {
        if (target instanceof Projectile) return;
        held.put(player.getUniqueId(), target.getUniqueId());
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 1.8f);
        player.sendActionBar(ChatColor.AQUA + "Grabbed " + target.getName());
    }

    private void shoot(Player player) {
        Entity target = takeHeld(player);
        if (target == null) return;
        target.setVelocity(player.getEyeLocation().getDirection().multiply(GravityGun.SHOOT_SPEED));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.5f);
    }

    private void release(Player player) {
        Entity target = takeHeld(player);
        if (target == null) return;
        target.setVelocity(new Vector(0, 0, 0));
        target.setFallDistance(0f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.6f, 1.8f);
        player.sendActionBar(ChatColor.GRAY + "Released " + target.getName());
    }

    private Entity takeHeld(Player player) {
        UUID targetId = held.remove(player.getUniqueId());
        if (targetId == null) return null;
        Entity target = plugin.getServer().getEntity(targetId);
        if (target == null || target.isDead()) return null;
        return target;
    }

    private void tick() {
        Iterator<Map.Entry<UUID, UUID>> it = held.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, UUID> entry = it.next();
            Player player = plugin.getServer().getPlayer(entry.getKey());
            if (player == null || !player.isOnline()
                    || !GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) {
                it.remove();
                continue;
            }

            Entity target = plugin.getServer().getEntity(entry.getValue());
            if (target == null || target.isDead() || !target.getWorld().equals(player.getWorld())) {
                it.remove();
                continue;
            }

            Location hold = player.getEyeLocation().add(
                    player.getEyeLocation().getDirection().multiply(GravityGun.HOLD_DISTANCE));
            Vector pull = hold.toVector().subtract(target.getLocation().toVector()).multiply(0.5);
            target.setVelocity(pull);
            target.setFallDistance(0f);
        }
    }
}
