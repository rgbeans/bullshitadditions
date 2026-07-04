package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TransmutationTable implements Listener {

    private static final int ROWS = 6;
    private static final int SIZE = ROWS * 9;
    private static final int ITEMS_PER_PAGE = (ROWS - 1) * 9;
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;
    private static final int FURNACE_SLOT = 49;
    private static final int EMC_SLOT = 47;

    private static final ItemStack BACKGROUND;
    private static final ItemStack FURNACE_ICON;

    static {
        BACKGROUND = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = BACKGROUND.getItemMeta();
        bgMeta.setDisplayName(" ");
        BACKGROUND.setItemMeta(bgMeta);

        FURNACE_ICON = new ItemStack(Material.FURNACE);
        ItemMeta furnaceMeta = FURNACE_ICON.getItemMeta();
        furnaceMeta.setDisplayName(ChatColor.GOLD + "Burn items for EMC");
        furnaceMeta.setLore(List.of(ChatColor.GRAY + "Drag items here to learn and burn them"));
        FURNACE_ICON.setItemMeta(furnaceMeta);
    }

    private final JavaPlugin plugin;
    private final PlayerDataManager dataManager;
    private final Map<UUID, Integer> playerPages = new HashMap<>();

    public TransmutationTable(JavaPlugin plugin, PlayerDataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
    }

    public void open(Player player) {
        open(player, 0);
    }

    private void open(Player player, int page) {
        PlayerData data = dataManager.get(player.getUniqueId());

        List<Material> items = data.learned().stream()
                .filter(EMCEngine::has)
                .sorted(Comparator.comparingLong(EMCEngine::get))
                .toList();

        int totalPages = Math.max(1, (items.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE);
        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;
        playerPages.put(player.getUniqueId(), page);

        Inventory inv = Bukkit.createInventory(null, SIZE, "Transmutation Table");

        for (int i = 0; i < SIZE; i++) {
            inv.setItem(i, BACKGROUND.clone());
        }

        int start = page * ITEMS_PER_PAGE;
        for (int i = 0; i < ITEMS_PER_PAGE && (start + i) < items.size(); i++) {
            Material mat = items.get(start + i);
            long emc = EMCEngine.get(mat);
            ItemStack display = new ItemStack(mat);
            ItemMeta meta = display.getItemMeta();
            meta.setLore(List.of(
                    ChatColor.GRAY + "EMC: " + EMCEngine.formatEmc(emc),
                    ChatColor.DARK_GRAY + "Left-click: buy 1",
                    ChatColor.DARK_GRAY + "Shift-click: buy stack"
            ));
            display.setItemMeta(meta);
            inv.setItem(i, display);
        }

        inv.setItem(FURNACE_SLOT, FURNACE_ICON.clone());

        if (page > 0) {
            inv.setItem(PREV_SLOT, createArrow("§e← Previous", "§7Page " + page + "/" + totalPages));
        }
        if (page < totalPages - 1) {
            inv.setItem(NEXT_SLOT, createArrow("§eNext →", "§7Page " + (page + 2) + "/" + totalPages));
        }

        ItemStack emcDisplay = new ItemStack(Material.SUNFLOWER);
        ItemMeta emcMeta = emcDisplay.getItemMeta();
        emcMeta.setDisplayName(ChatColor.GOLD + "Your EMC: " + ChatColor.WHITE + EMCEngine.formatEmc(data.emc()));
        emcDisplay.setItemMeta(emcMeta);
        inv.setItem(EMC_SLOT, emcDisplay);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Transmutation Table")) return;
        if (event.getInventory().getHolder() != null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();
        if (slot < 0) return;

        if (slot >= SIZE) {
            if (event.isShiftClick() && event.getCurrentItem() != null) {
                event.setCancelled(true);
                burnStack(player, event.getCurrentItem());
            }
            return;
        }

        event.setCancelled(true);

        if (slot == FURNACE_SLOT) {
            handleFurnace(event, player);
            return;
        }

        if (slot < ITEMS_PER_PAGE) {
            handleBuy(event, player, slot);
            return;
        }

        Integer page = playerPages.get(player.getUniqueId());
        if (page == null) return;

        if (slot == PREV_SLOT && page > 0) {
            open(player, page - 1);
        } else if (slot == NEXT_SLOT) {
            open(player, page + 1);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Material mat = event.getItem().getItemStack().getType();
        learn(player, mat);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Material mat = event.getRecipe().getResult().getType();
        learn(player, mat);
    }

    private void learn(Player player, Material mat) {
        if (EMCEngine.has(mat)) {
            dataManager.get(player.getUniqueId()).learn(mat);
        }
    }

    private void handleFurnace(InventoryClickEvent event, Player player) {
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.isEmpty()) return;

        Material mat = cursor.getType();
        if (hasCustomMeta(cursor)) {
            player.sendActionBar(ChatColor.RED + "Custom items cannot be burned!");
            return;
        }
        Long emcValue = EMCEngine.get(mat);
        if (emcValue == null) {
            player.sendActionBar(ChatColor.RED + "This item has no EMC value!");
            return;
        }

        PlayerData data = dataManager.get(player.getUniqueId());
        int amount = cursor.getAmount();
        long earned = emcValue * amount;
        data.learn(mat);
        data.addEmc(earned);
        event.setCursor(null);

        player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 1.0f);
        player.sendActionBar(ChatColor.GREEN + "+" + EMCEngine.formatEmc(earned) + " EMC");

        refresh(player);
    }

    private void burnStack(Player player, ItemStack stack) {
        Material mat = stack.getType();
        if (hasCustomMeta(stack)) {
            player.sendActionBar(ChatColor.RED + "Renamed or custom items cannot be burned!");
            return;
        }
        Long emcValue = EMCEngine.get(mat);
        if (emcValue == null) {
            player.sendActionBar(ChatColor.RED + "This item has no EMC value!");
            return;
        }

        PlayerData data = dataManager.get(player.getUniqueId());
        int amount = stack.getAmount();
        long earned = emcValue * amount;
        data.learn(mat);
        data.addEmc(earned);
        stack.setAmount(0);

        player.playSound(player.getLocation(), Sound.BLOCK_FURNACE_FIRE_CRACKLE, 0.5f, 1.0f);
        player.sendActionBar(ChatColor.GREEN + "+" + EMCEngine.formatEmc(earned) + " EMC");
        refresh(player);
    }

    private void handleBuy(InventoryClickEvent event, Player player, int slot) {
        ItemStack clicked = event.getView().getTopInventory().getItem(slot);
        if (clicked == null || clicked.isEmpty()) return;

        Material mat = clicked.getType();
        Long emcValue = EMCEngine.get(mat);
        if (emcValue == null) return;

        PlayerData data = dataManager.get(player.getUniqueId());
        int amount = 1;

        if (event.isShiftClick()) {
            int maxStack = mat.getMaxStackSize();
            long affordable = (long) (data.emc() / emcValue);
            amount = (int) Math.min(maxStack, Math.min(affordable, Integer.MAX_VALUE));
            if (amount == 0) {
                player.sendActionBar(ChatColor.RED + "Not enough EMC!");
                return;
            }
        }

        long cost = emcValue * amount;
        if (!data.spendEmc(cost)) {
            player.sendActionBar(ChatColor.RED + "Not enough EMC!");
            return;
        }

        ItemStack toGive = new ItemStack(mat, amount);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(toGive);
        if (!leftover.isEmpty()) {
            data.addEmc(cost);
            player.sendActionBar(ChatColor.RED + "Inventory full!");
            return;
        }

        player.sendActionBar(ChatColor.GREEN + "-" + EMCEngine.formatEmc(cost) + " EMC");
        refresh(player);
    }

    private void refresh(Player player) {
        Integer page = playerPages.get(player.getUniqueId());
        open(player, page != null ? page : 0);
    }

    private ItemStack createArrow(String name, String lore) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }

    private boolean hasCustomMeta(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() || meta.hasLore() || !meta.getPersistentDataContainer().isEmpty();
    }
}
