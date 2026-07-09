package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class MyBlicky {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "my_blicky");
    public static final Material AMMO_TYPE = Material.GOLD_INGOT;
    public static final double PROJECTILE_SPEED = 4.0;
    public static final double DAMAGE = 30.0;
    public static final long COOLDOWN_MILLIS = 2000;

    private MyBlicky() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "my blicky");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to fire a whole gold bar",
                ChatColor.GRAY + "Devastating damage, slow to fire",
                ChatColor.DARK_GRAY + "Consumes gold ingots from your inventory"
        ));
        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isBlicky(ItemStack item) {
        if (item == null || item.getType() != Material.GOLDEN_HOE || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
