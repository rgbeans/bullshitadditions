package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class TargetDummyItem {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "target_dummy_item");

    private TargetDummyItem() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.TARGET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Target Dummy");
        meta.setLore(List.of(
                ChatColor.GRAY + "Place to spawn an invincible target dummy",
                ChatColor.DARK_GRAY + "Shows damage dealt when hit"
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isDummyItem(ItemStack item) {
        if (item == null || item.getType() != Material.TARGET || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
