package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class EnergyCondenser implements Listener {

    static final NamespacedKey BLOCK_KEY = new NamespacedKey("bullshitadditions", "energy_condenser");
    private static final NamespacedKey TARGET_KEY = new NamespacedKey("bullshitadditions", "condenser_target");
    private static final NamespacedKey EMC_KEY = new NamespacedKey("bullshitadditions", "condenser_emc");
    private static final NamespacedKey ITEM_KEY = new NamespacedKey("bullshitadditions", "energy_condenser_item");

    static final long MAX_EMC = Long.MAX_VALUE;
    static final long EMC_VALUE = 42_011L;

    private static final int CONTROL_SIZE = 9;
    private static final int C_TARGET = 0;
    private static final int[] C_PROGRESS = {1, 2, 3, 4, 5, 6, 7};
    private static final int C_INFO = 8;

    private static final String CONTROL_TITLE = "Energy Condenser";

    private final JavaPlugin plugin;
    private final Map<UUID, Location> openControls = new HashMap<>();
    private final Set<Location> condenserLocations = ConcurrentHashMap.newKeySet();
    private BukkitTask processingTask;

    public EnergyCondenser(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startProcessing() {
        if (processingTask != null) return;
        processingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickAllCondensers, 6L, 6L);
    }

    public void stopProcessing() {
        if (processingTask != null) {
            processingTask.cancel();
            processingTask = null;
        }
    }

    private void tickAllCondensers() {
        var stale = new java.util.ArrayList<Location>();
        for (Location loc : condenserLocations) {
            World world = loc.getWorld();
            if (world == null) { stale.add(loc); continue; }
            if (!world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) continue;
            Block block = loc.getBlock();
            if (!isCondenserBlock(block)) { stale.add(loc); continue; }
            if (!(block.getState() instanceof Barrel barrel)) continue;

            processBarrel(barrel);
        }
        condenserLocations.removeAll(stale);
    }

    private void processBarrel(Barrel barrel) {
        Inventory inv = barrel.getSnapshotInventory();

        String targetName = barrel.getPersistentDataContainer()
                .get(TARGET_KEY, PersistentDataType.STRING);
        Material targetMat = targetName != null && !targetName.isEmpty() && !targetName.equals("none")
                ? Material.getMaterial(targetName) : null;
        long stored = barrel.getPersistentDataContainer()
                .getOrDefault(EMC_KEY, PersistentDataType.LONG, 0L);

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.isEmpty()) continue;
            if (targetMat != null && item.getType() == targetMat) continue;
            if (hasCustomMeta(item)) { inv.setItem(i, null); continue; }
            Long value = EMCEngine.get(item.getType());
            if (value != null) {
                stored = Math.min(MAX_EMC, stored + value * item.getAmount());
                inv.setItem(i, null);
            } else {
                inv.setItem(i, null);
            }
        }

        if (targetMat != null && stored > 0) {
            Long targetEmc = EMCEngine.get(targetMat);
            if (targetEmc != null && targetEmc > 0 && stored >= targetEmc) {
                while (stored >= targetEmc) {
                    int slot = findSpaceFor(inv, targetMat);
                    if (slot < 0) break;
                    ItemStack existing = inv.getItem(slot);
                    if (existing == null || existing.isEmpty()) {
                        inv.setItem(slot, new ItemStack(targetMat, 1));
                    } else {
                        existing.setAmount(existing.getAmount() + 1);
                    }
                    stored -= targetEmc;
                }
            }
        }

        if (stored > MAX_EMC) stored = MAX_EMC;
        barrel.getPersistentDataContainer().set(EMC_KEY, PersistentDataType.LONG, stored);
        updateBarrelName(barrel, targetMat);
        barrel.update(true, false);

        refreshOpenControls(barrel);
    }

    private void updateBarrelName(Barrel barrel, Material targetMat) {
        String name = ChatColor.DARK_PURPLE + "Energy Condenser";
        if (targetMat != null) {
            name += " " + ChatColor.GRAY + "(Target: " + ChatColor.WHITE
                    + formatMaterialName(targetMat) + ChatColor.GRAY + ")";
        }
        barrel.setCustomName(name);
    }

    private int findSpaceFor(Inventory inv, Material mat) {
        int firstEmpty = -1;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.isEmpty()) {
                if (firstEmpty < 0) firstEmpty = i;
                continue;
            }
            if (item.getType() == mat && item.getAmount() < mat.getMaxStackSize()) {
                return i;
            }
        }
        return firstEmpty;
    }

    private void refreshOpenControls(Barrel barrel) {
        Location loc = barrel.getLocation();
        if (loc == null) return;

        String targetName = barrel.getPersistentDataContainer()
                .get(TARGET_KEY, PersistentDataType.STRING);
        long stored = barrel.getPersistentDataContainer()
                .getOrDefault(EMC_KEY, PersistentDataType.LONG, 0L);

        for (var entry : openControls.entrySet()) {
            if (!entry.getValue().equals(loc)) continue;
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) continue;
            InventoryView view = player.getOpenInventory();
            if (view == null || !view.getTitle().equals(CONTROL_TITLE)) continue;
            fillControlPanel(view.getTopInventory(), stored, targetName);
        }
    }

    private void fillControlPanel(Inventory inv, long stored, String targetName) {
        Material targetMat = null;
        long targetEmc = 0;
        if (targetName != null && !targetName.isEmpty() && !targetName.equals("none")) {
            targetMat = Material.getMaterial(targetName);
        }
        if (targetMat != null) {
            Long val = EMCEngine.get(targetMat);
            if (val != null) targetEmc = val;
        }

        if (targetMat != null) {
            inv.setItem(C_TARGET, new ItemStack(targetMat));
        } else {
            inv.setItem(C_TARGET, null);
        }

        int filled = 0;
        int partial = -1;
        if (targetEmc > 0 && stored > 0) {
            double frac = (double) stored / targetEmc;
            filled = (int) (frac * C_PROGRESS.length);
            if (filled >= C_PROGRESS.length) {
                filled = C_PROGRESS.length;
            } else {
                double rem = (frac * C_PROGRESS.length) - filled;
                if (rem > 0.01) partial = filled;
            }
        }

        for (int i = 0; i < C_PROGRESS.length; i++) {
            Material mat;
            String pctText;
            if (i < filled) {
                mat = Material.LIME_STAINED_GLASS_PANE;
                pctText = ChatColor.GREEN + "Full";
            } else if (i == partial) {
                mat = Material.YELLOW_STAINED_GLASS_PANE;
                pctText = ChatColor.YELLOW + String.format("%.1f%%",
                        Math.min(100.0, (double) stored / targetEmc * 100.0));
            } else {
                mat = Material.GRAY_STAINED_GLASS_PANE;
                pctText = ChatColor.GRAY + "Empty";
            }
            ItemStack pane = new ItemStack(mat);
            ItemMeta meta = pane.getItemMeta();
            if (targetEmc > 0) {
                meta.setDisplayName(pctText);
                meta.setLore(List.of(
                        ChatColor.GRAY + EMCEngine.formatEmc(stored) + " / "
                                + EMCEngine.formatEmc(targetEmc) + " EMC"
                ));
            } else {
                meta.setDisplayName(ChatColor.GRAY + "No target set");
            }
            pane.setItemMeta(meta);
            inv.setItem(C_PROGRESS[i], pane);
        }

        ItemStack info = new ItemStack(Material.SUNFLOWER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + "Stored: " + ChatColor.WHITE + EMCEngine.formatEmc(stored));
        infoMeta.setLore(targetMat != null
                ? List.of(ChatColor.GRAY + "Target: " + ChatColor.WHITE + formatMaterialName(targetMat)
                    + " (" + EMCEngine.formatEmc(targetEmc) + " EMC)")
                : List.of(ChatColor.GRAY + "Target: " + ChatColor.RED + "Not set"));
        info.setItemMeta(infoMeta);
        inv.setItem(C_INFO, info);
    }

    public static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.BARREL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Energy Condenser");
        meta.setLore(List.of(
                ChatColor.GRAY + "Right-click to open storage",
                ChatColor.GRAY + "Sneak + right-click for controls",
                ChatColor.GRAY + "Works with hoppers for automation",
                ChatColor.DARK_GRAY + "Own EMC: " + EMCEngine.formatEmc(EMC_VALUE)
        ));
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARREL || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(ITEM_KEY, PersistentDataType.BOOLEAN);
    }

    private boolean isCondenserBlock(Block block) {
        if (block.getType() != Material.BARREL) return false;
        if (!(block.getState() instanceof Barrel barrel)) return false;
        return barrel.getPersistentDataContainer().has(BLOCK_KEY, PersistentDataType.BOOLEAN);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() != Material.BARREL) return;
        ItemStack placed = event.getItemInHand();
        if (isItem(placed) && block.getState() instanceof Barrel barrel) {
            barrel.setCustomName(ChatColor.DARK_PURPLE + "Energy Condenser");
            barrel.getPersistentDataContainer().set(BLOCK_KEY, PersistentDataType.BOOLEAN, true);
            barrel.getPersistentDataContainer().set(EMC_KEY, PersistentDataType.LONG, 0L);
            barrel.getPersistentDataContainer().set(TARGET_KEY, PersistentDataType.STRING, "");
            barrel.update(true, false);
            condenserLocations.add(block.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!isCondenserBlock(block)) return;
        condenserLocations.remove(block.getLocation());
        openControls.values().removeIf(block.getLocation()::equals);

        Barrel barrel = (Barrel) block.getState();
        barrel.getPersistentDataContainer().remove(BLOCK_KEY);
        barrel.getPersistentDataContainer().remove(EMC_KEY);
        barrel.getPersistentDataContainer().remove(TARGET_KEY);
        barrel.update(true, false);

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), createItem());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || !isCondenserBlock(block)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        condenserLocations.add(block.getLocation());

        if (player.isSneaking()) {
            openControlPanel(player, block);
        } else {
            if (!(block.getState() instanceof Barrel barrel)) return;
            player.openInventory(barrel.getInventory());
            long stored = barrel.getPersistentDataContainer()
                    .getOrDefault(EMC_KEY, PersistentDataType.LONG, 0L);
            player.sendActionBar(ChatColor.GOLD + "EMC: " + ChatColor.WHITE + EMCEngine.formatEmc(stored));
        }
    }

    private void openControlPanel(Player player, Block block) {
        if (!(block.getState() instanceof Barrel barrel)) return;

        String targetName = barrel.getPersistentDataContainer()
                .get(TARGET_KEY, PersistentDataType.STRING);
        long stored = barrel.getPersistentDataContainer()
                .getOrDefault(EMC_KEY, PersistentDataType.LONG, 0L);

        openControls.put(player.getUniqueId(), block.getLocation());

        Inventory inv = Bukkit.createInventory(null, CONTROL_SIZE, CONTROL_TITLE);
        fillControlPanel(inv, stored, targetName);
        player.openInventory(inv);
    }

    @EventHandler
    public void onControlDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().equals(CONTROL_TITLE)) return;
        for (int slot : event.getRawSlots()) {
            if (slot < CONTROL_SIZE) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onControlClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(CONTROL_TITLE)) return;
        if (!(event.getPlayer() instanceof Player player)) return;
        openControls.remove(player.getUniqueId());
    }

    @EventHandler
    public void onControlClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(CONTROL_TITLE)) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getRawSlot();
        if (slot >= CONTROL_SIZE) return;

        event.setCancelled(true);

        Location loc = openControls.get(player.getUniqueId());
        if (loc == null) return;
        Block block = loc.getBlock();
        if (!isCondenserBlock(block)) return;

        if (slot == C_TARGET) {
            handleControlTarget(player, block, event);
        }
    }

    private void handleControlTarget(Player player, Block block, InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.isEmpty()) return;
        if (hasCustomMeta(cursor)) {
            player.sendActionBar(ChatColor.RED + "Custom items cannot be targets!");
            return;
        }
        Long val = EMCEngine.get(cursor.getType());
        if (val == null) {
            player.sendActionBar(ChatColor.RED + "This item has no EMC value!");
            return;
        }

        String prev = getTargetString(block);
        setTargetString(block, cursor.getType().name());
        if (!cursor.getType().name().equals(prev)) {
            setEmc(block, 0L);
        }

        player.sendActionBar(ChatColor.GREEN + "Target: " + formatMaterialName(cursor.getType())
                + " (" + EMCEngine.formatEmc(val) + " EMC)");
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.2f);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (openControls.get(player.getUniqueId()) == null) return;
            if (!isCondenserBlock(block)) return;
            if (!(block.getState() instanceof Barrel barrel)) return;
            String tn = barrel.getPersistentDataContainer().get(TARGET_KEY, PersistentDataType.STRING);
            long emc = barrel.getPersistentDataContainer().getOrDefault(EMC_KEY, PersistentDataType.LONG, 0L);
            fillControlPanel(event.getView().getTopInventory(), emc, tn);
        });
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getDestination().getHolder() instanceof Barrel barrel
                && barrel.getPersistentDataContainer().has(BLOCK_KEY, PersistentDataType.BOOLEAN)) {
            ItemStack item = event.getItem();
            if (item != null && !item.isEmpty() && (hasCustomMeta(item) || !EMCEngine.has(item.getType()))) {
                event.setCancelled(true);
            }
        }
    }

    private String getTargetString(Block block) {
        if (!(block.getState() instanceof Barrel barrel)) return "none";
        String val = barrel.getPersistentDataContainer().get(TARGET_KEY, PersistentDataType.STRING);
        return val == null || val.isEmpty() ? "none" : val;
    }

    private void setTargetString(Block block, String target) {
        if (!(block.getState() instanceof Barrel barrel)) return;
        barrel.getPersistentDataContainer().set(TARGET_KEY, PersistentDataType.STRING, target);
        barrel.update(true, false);
    }

    private void setEmc(Block block, long emc) {
        if (!(block.getState() instanceof Barrel barrel)) return;
        if (emc > MAX_EMC) emc = MAX_EMC;
        barrel.getPersistentDataContainer().set(EMC_KEY, PersistentDataType.LONG, emc);
        barrel.update(true, false);
    }

    private boolean hasCustomMeta(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() || meta.hasLore() || !meta.getPersistentDataContainer().isEmpty();
    }

    private String formatMaterialName(Material mat) {
        String name = mat.name().toLowerCase().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
                sb.append(' ');
            }
        }
        return sb.toString().trim();
    }
}
