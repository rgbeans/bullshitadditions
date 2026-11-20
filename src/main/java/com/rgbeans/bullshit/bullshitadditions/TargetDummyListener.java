package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public final class TargetDummyListener implements Listener {

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (!TargetDummyItem.isDummyItem(item)) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getState() instanceof InventoryHolder) return;

        BlockFace face = event.getBlockFace();
        Location spawnLoc = clicked.getRelative(face).getLocation().add(0.5, 0, 0.5);
        spawnLoc.setYaw(event.getPlayer().getLocation().getYaw() + 180);
        spawnLoc.setPitch(0);

        event.setUseItemInHand(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.DENY);

        TargetDummy.spawn(spawnLoc);

        if (item.getAmount() == 1) {
            event.getPlayer().getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof ArmorStand stand)) return;
        if (!TargetDummy.isDummy(stand)) return;

        event.setCancelled(true);

        if (event instanceof EntityDamageByEntityEvent damageEvent) {
            if (tryRemove(damageEvent, stand)) return;
            reportDamage(damageEvent, stand);
        }
    }

    private boolean tryRemove(EntityDamageByEntityEvent event, ArmorStand stand) {
        if (event.getDamager() instanceof Player player
                && player.getInventory().getItemInMainHand().getType() == Material.DIRT) {
            stand.remove();
            player.sendActionBar(ChatColor.GREEN + "Target dummy removed!");
            return true;
        }
        return false;
    }

    private void reportDamage(EntityDamageByEntityEvent event, ArmorStand stand) {
        Player attacker = null;

        if (event.getDamager() instanceof Player player) {
            attacker = player;
        } else if (event.getDamager() instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                attacker = player;
            }
        }

        if (attacker != null) {
            double damage = event.getDamage();
            attacker.sendActionBar(
                    ChatColor.GOLD + "Damage: " + ChatColor.RED + String.format("%.1f", damage)
            );
        }
    }
}
