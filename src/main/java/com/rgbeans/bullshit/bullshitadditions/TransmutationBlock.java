package com.rgbeans.bullshit.bullshitadditions;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public final class TransmutationBlock implements Listener {

    static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "transmutation_table");

    private static final String BASE64 =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTlkOWVm" +
            "MTU5YmMyMjkxNjdmYjg3ODA3NDJjZWU4ZmVkMDhiMDIxZTg1ZjBkMzRmMzlkNDM4MDVlZTNmNjM0OSJ9fX0=";

    private final TransmutationTable table;

    public TransmutationBlock(TransmutationTable table) {
        this.table = table;
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.getProperties().add(new ProfileProperty("textures", BASE64, null));
        meta.setPlayerProfile(profile);

        meta.setDisplayName(ChatColor.DARK_PURPLE + "Transmutation Table");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to open",
                ChatColor.DARK_GRAY + "Convert items using EMC"
        ));
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isItem(ItemStack item) {
        if (item == null || item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isItem(event.getItemInHand())) {
            Block block = event.getBlockPlaced();
            if (block.getState() instanceof Skull skull) {
                skull.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
                skull.update();
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) return;
        if (!(block.getState() instanceof Skull skull)) return;
        if (!skull.getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN)) return;

        event.setDropItems(false);
        skull.getPersistentDataContainer().remove(KEY);
        skull.update();
        block.getWorld().dropItemNaturally(block.getLocation(), createItem());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) return;
        if (!(block.getState() instanceof Skull skull)) return;
        if (!skull.getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN)) return;

        event.setCancelled(true);
        table.open(event.getPlayer());
    }
}
