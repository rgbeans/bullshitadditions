package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RecipeGui implements Listener {

    private static final int DETAIL_SIZE = 27;
    private static final int OVERVIEW_SIZE = 54;
    private static final String OVERVIEW_TITLE = "Custom Recipes";
    private static final String DETAIL_TITLE_PREFIX = "Recipe: ";

    private static final int[] GRID_SLOTS = {1, 2, 3, 10, 11, 12, 19, 20, 21};
    private static final int ARROW_SLOT = 14;
    private static final int RESULT_SLOT = 16;
    private static final int PREV_SLOT = 18;
    private static final int NEXT_SLOT = 26;
    private static final int BACK_SLOT = 4;

    private static final ItemStack BACKGROUND;
    private static final ItemStack CRAFTING_ARROW;
    private static final ItemStack BACK_BUTTON;

    static {
        BACKGROUND = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = BACKGROUND.getItemMeta();
        bgMeta.setDisplayName(" ");
        BACKGROUND.setItemMeta(bgMeta);

        CRAFTING_ARROW = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta arrowMeta = CRAFTING_ARROW.getItemMeta();
        arrowMeta.setDisplayName("§a→");
        CRAFTING_ARROW.setItemMeta(arrowMeta);

        BACK_BUTTON = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta backMeta = BACK_BUTTON.getItemMeta();
        backMeta.setDisplayName("§e← All Recipes");
        backMeta.setLore(List.of("§7Return to the item list"));
        BACK_BUTTON.setItemMeta(backMeta);
    }

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> detailPages = new HashMap<>();

    public RecipeGui(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        openOverview(player);
    }

    public void openOverview(Player player) {
        List<RecipeInfo> recipes = RecipeRegistry.getRecipes();
        if (recipes.isEmpty()) {
            player.sendMessage("§cNo custom recipes registered.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, OVERVIEW_SIZE, OVERVIEW_TITLE);

        for (int i = 0; i < OVERVIEW_SIZE; i++) {
            inv.setItem(i, BACKGROUND.clone());
        }

        int col = 1;
        int row = 1;
        for (RecipeInfo recipe : recipes) {
            int slot = row * 9 + col;
            if (slot < OVERVIEW_SIZE) {
                inv.setItem(slot, recipe.result().clone());
            }
            col++;
            if (col > 7) {
                col = 1;
                row++;
            }
        }

        player.openInventory(inv);
    }

    private void openDetail(Player player, int recipeIndex) {
        List<RecipeInfo> recipes = RecipeRegistry.getRecipes();
        if (recipeIndex < 0) recipeIndex = 0;
        if (recipeIndex >= recipes.size()) recipeIndex = recipes.size() - 1;
        detailPages.put(player.getUniqueId(), recipeIndex);

        RecipeInfo recipe = recipes.get(recipeIndex);
        Inventory inv = Bukkit.createInventory(null, DETAIL_SIZE, DETAIL_TITLE_PREFIX + recipe.displayName());

        for (int i = 0; i < DETAIL_SIZE; i++) {
            inv.setItem(i, BACKGROUND.clone());
        }

        inv.setItem(BACK_SLOT, BACK_BUTTON.clone());

        ItemStack[] ingredients = recipe.ingredients();
        for (int i = 0; i < 9 && i < ingredients.length; i++) {
            ItemStack ing = ingredients[i];
            if (ing != null && ing.getType() != Material.AIR) {
                inv.setItem(GRID_SLOTS[i], ing.clone());
            }
        }

        inv.setItem(ARROW_SLOT, CRAFTING_ARROW.clone());
        inv.setItem(RESULT_SLOT, recipe.result().clone());

        if (recipeIndex > 0) {
            inv.setItem(PREV_SLOT, createNavArrow("§e← Previous", "§7Page " + recipeIndex + "/" + recipes.size()));
        }
        if (recipeIndex < recipes.size() - 1) {
            inv.setItem(NEXT_SLOT, createNavArrow("§eNext →", "§7Page " + (recipeIndex + 2) + "/" + recipes.size()));
        }

        ItemStack info = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName("§7[" + (recipeIndex + 1) + "/" + recipes.size() + "]");
        info.setItemMeta(infoMeta);
        inv.setItem(22, info);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null) return;

        String title = event.getView().getTitle();

        if (!title.equals(OVERVIEW_TITLE) && !title.startsWith(DETAIL_TITLE_PREFIX)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        int slot = event.getRawSlot();

        if (title.equals(OVERVIEW_TITLE)) {
            handleOverviewClick(player, slot);
        } else if (title.startsWith(DETAIL_TITLE_PREFIX)) {
            handleDetailClick(player, slot);
        }
    }

    private void handleOverviewClick(Player player, int slot) {
        if (slot < 0 || slot >= OVERVIEW_SIZE) return;

        ItemStack clicked = player.getOpenInventory().getItem(slot);
        if (clicked == null || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE || clicked.getType() == Material.AIR) {
            return;
        }

        List<RecipeInfo> recipes = RecipeRegistry.getRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).result().isSimilar(clicked)) {
                openDetail(player, i);
                return;
            }
        }
    }

    private void handleDetailClick(Player player, int slot) {
        if (slot < 0 || slot >= DETAIL_SIZE) return;

        Integer page = detailPages.get(player.getUniqueId());
        if (page == null) return;

        if (slot == BACK_SLOT) {
            openOverview(player);
            return;
        }

        List<RecipeInfo> recipes = RecipeRegistry.getRecipes();

        if (slot == PREV_SLOT && page > 0) {
            openDetail(player, page - 1);
        } else if (slot == NEXT_SLOT && page < recipes.size() - 1) {
            openDetail(player, page + 1);
        }
    }

    private ItemStack createNavArrow(String name, String lore) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }
}
