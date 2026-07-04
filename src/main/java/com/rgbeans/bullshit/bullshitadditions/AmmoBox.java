package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class AmmoBox {

    static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "ammo_box");

    private static final NamespacedKey[] SLOT_KEYS = new NamespacedKey[9];
    static {
        for (int i = 0; i < 9; i++) {
            SLOT_KEYS[i] = new NamespacedKey("bullshitadditions", "ammo_slot_" + i);
        }
    }

    static final Set<Material> AMMO_TYPES = Set.of(Material.IRON_NUGGET, Material.GOLD_NUGGET);

    static final Map<UUID, Integer> OPEN_BOX_SLOTS = new HashMap<>();

    private AmmoBox() {}

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Ammo Box");
        meta.setLore(List.of(
                ChatColor.GRAY + "Stores ammo for your guns",
                ChatColor.DARK_GRAY + "Right-click to open"
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isAmmoBox(ItemStack item) {
        if (item == null || item.getType() != Material.ENDER_CHEST || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }

    static ItemStack[] getContents(ItemStack ammoBox) {
        ItemStack[] contents = new ItemStack[9];
        if (!isAmmoBox(ammoBox)) return contents;

        PersistentDataContainer pdc = ammoBox.getItemMeta().getPersistentDataContainer();
        for (int i = 0; i < 9; i++) {
            byte[] data = pdc.get(SLOT_KEYS[i], PersistentDataType.BYTE_ARRAY);
            if (data != null && data.length > 0) {
                contents[i] = ItemStack.deserializeBytes(data);
            }
        }
        return contents;
    }

    static void setContents(ItemStack ammoBox, ItemStack[] contents) {
        ItemMeta meta = ammoBox.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        for (int i = 0; i < 9; i++) {
            if (contents[i] != null && !contents[i].isEmpty()) {
                pdc.set(SLOT_KEYS[i], PersistentDataType.BYTE_ARRAY, contents[i].serializeAsBytes());
            } else {
                pdc.remove(SLOT_KEYS[i]);
            }
        }
        ammoBox.setItemMeta(meta);
    }

    public static int countAmmo(ItemStack ammoBox, Material type) {
        int count = 0;
        for (ItemStack slot : getContents(ammoBox)) {
            if (slot != null && slot.getType() == type) {
                count += slot.getAmount();
            }
        }
        return count;
    }

    public static boolean consumeAmmo(ItemStack ammoBox, Material type, int amount) {
        ItemStack[] contents = getContents(ammoBox);
        for (int i = 0; i < contents.length; i++) {
            ItemStack slot = contents[i];
            if (slot != null && slot.getType() == type && slot.getAmount() >= amount) {
                slot.setAmount(slot.getAmount() - amount);
                if (slot.getAmount() <= 0) {
                    contents[i] = null;
                }
                setContents(ammoBox, contents);
                return true;
            }
        }
        return false;
    }

    static void open(Player player, ItemStack ammoBox, int slot) {
        ItemStack[] contents = getContents(ammoBox);
        Inventory inv = Bukkit.createInventory(null, 9, "Ammo Box");
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                inv.setItem(i, contents[i]);
            }
        }
        OPEN_BOX_SLOTS.put(player.getUniqueId(), slot);
        player.openInventory(inv);
    }

    static void saveAndClose(Player player) {
        Integer slot = OPEN_BOX_SLOTS.remove(player.getUniqueId());
        if (slot == null) return;

        ItemStack ammoBox = player.getInventory().getItem(slot);
        if (!isAmmoBox(ammoBox)) return;

        InventoryView view = player.getOpenInventory();
        if (!view.getTitle().equals("Ammo Box")) return;

        ItemStack[] contents = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            ItemStack item = view.getTopInventory().getItem(i);
            if (item != null && !item.isEmpty()) {
                contents[i] = item.clone();
            }
        }
        setContents(ammoBox, contents);
    }
}
