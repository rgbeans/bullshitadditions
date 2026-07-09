package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class GravityGun {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "gravity_gun");
    public static final double GRAB_RANGE = 5.0;
    public static final double HOLD_DISTANCE = 4.0;
    public static final double SHOOT_SPEED = 3.5;

    private GravityGun() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Gravity Gun");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to grab an entity",
                ChatColor.GRAY + "Right-click again to launch it",
                ChatColor.GRAY + "Left-click to gently release",
                ChatColor.DARK_GRAY + "Works on players too"
        ));
        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGravityGun(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_HOE || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
