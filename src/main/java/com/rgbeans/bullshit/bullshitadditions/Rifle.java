package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class Rifle {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "rifle");
    public static final Material AMMO_TYPE = Material.GOLD_NUGGET;
    public static final double PROJECTILE_SPEED = 9.0;
    public static final double DAMAGE = 6.0;
    public static final long COOLDOWN_MILLIS = 150;

    private Rifle() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Rifle");
        meta.setLore(List.of(
                ChatColor.GRAY + "Hold right-click to fire gold nuggets",
                ChatColor.DARK_GRAY + "Consumes gold nuggets from your inventory"
        ));
        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isRifle(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_HOE || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
