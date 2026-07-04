package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public final class HeavyStickListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!HeavyStick.isHeavyStick(item)) return;

        event.setCancelled(true);

        Entity target = event.getEntity();
        Location dest = target.getLocation().subtract(0, 2, 0);
        dest.setX(Math.floor(dest.getX()) + 0.5);
        dest.setZ(Math.floor(dest.getZ()) + 0.5);

        target.teleport(dest);

        if (!HeavyStick.damage(item)) {
            player.getInventory().setItemInMainHand(null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
        }
    }
}
