package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class Pistol {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "pistol");
    public static final Material AMMO_TYPE = Material.IRON_NUGGET;
    public static final double PROJECTILE_SPEED = 5.0;
    public static final double DAMAGE = 5.0;
    public static final long COOLDOWN_MILLIS = 500;

    private Pistol() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.STONE_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Pistol");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to fire iron nuggets",
                ChatColor.DARK_GRAY + "Consumes iron nuggets from your inventory"
        ));
        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isGun(ItemStack item) {
        if (item == null || item.getType() != Material.STONE_HOE || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
