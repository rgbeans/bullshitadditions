package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class HeavyStick {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "heavy_stick");
    private static final NamespacedKey DURABILITY_KEY = new NamespacedKey("bullshitadditions", "heavy_stick_durability");
    public static final int MAX_DURABILITY = 16;
    public static final String DISPLAY_NAME = ChatColor.DARK_GRAY + "Heavy Stick";

    private HeavyStick() {}

    public static ItemStack create() {
        return create(MAX_DURABILITY);
    }

    private static ItemStack create(int durability) {
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(DISPLAY_NAME);
        meta.setLore(List.of(
                ChatColor.GRAY + "Hits teleport targets downward",
                ChatColor.DARK_GRAY + "Durability: " + durability + "/" + MAX_DURABILITY
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(DURABILITY_KEY, PersistentDataType.INTEGER, durability);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isHeavyStick(ItemStack item) {
        if (item == null || item.getType() != Material.STICK || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }

    private static int getDurability(ItemStack item) {
        if (!isHeavyStick(item)) return 0;
        Integer dur = item.getItemMeta().getPersistentDataContainer().get(DURABILITY_KEY, PersistentDataType.INTEGER);
        return dur != null ? dur : 0;
    }

    private static void setDurability(ItemStack item, int durability) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DURABILITY_KEY, PersistentDataType.INTEGER, durability);
        List<String> lore = meta.getLore();
        if (lore != null && lore.size() > 1) {
            lore.set(1, ChatColor.DARK_GRAY + "Durability: " + durability + "/" + MAX_DURABILITY);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public static boolean damage(ItemStack item) {
        int dur = getDurability(item);
        if (dur <= 1) {
            return false;
        }
        setDurability(item, dur - 1);
        return true;
    }
}
