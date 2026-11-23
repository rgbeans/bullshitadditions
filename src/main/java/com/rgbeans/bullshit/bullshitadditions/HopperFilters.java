package com.rgbeans.bullshit.bullshitadditions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

final class HopperFilters implements Listener {
    private static final NamespacedKey FILTER = new NamespacedKey("bullshitadditions", "hopper_filter");
    private static final int FILTER_SLOT = 4;

    private final Plugin plugin;

    HopperFilters(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() != InventoryType.HOPPER
                || !(event.getDestination().getHolder(false) instanceof Hopper hopper)) return;
        String filter = filter(hopper);
        if (filter == null) return;
        ItemStack item = event.getItem();
        if (matches(filter, item)) return;

        event.setCancelled(true);
        // Paper ends the pull on cancellation. Try a later allowed slot after it restores the source stack.
        if (event.getInitiator().equals(event.getDestination()) && supportsSlotSearch(event.getSource())) {
            Location location = hopper.getLocation();
            var sourceLocation = event.getSource().getLocation();
            if (sourceLocation != null && sourceLocation.getBlockY() == location.getBlockY() + 1) {
                Bukkit.getScheduler().runTask(plugin, () -> pullAllowed(location));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getInventory().getType() != InventoryType.HOPPER
                || !(event.getInventory().getHolder(false) instanceof Hopper hopper)) return;
        String filter = filter(hopper);
        if (filter != null && !matches(filter, event.getItem().getItemStack())) event.setCancelled(true);
    }

    private static boolean supportsSlotSearch(Inventory inventory) {
        return switch (inventory.getType()) {
            case CHEST, BARREL, HOPPER, DROPPER, DISPENSER, SHULKER_BOX -> true;
            default -> false;
        };
    }

    private void pullAllowed(Location location) {
        if (!location.isWorldLoaded() || !location.getWorld().isChunkLoaded(
                location.getBlockX() >> 4, location.getBlockZ() >> 4)) return;
        var block = location.getBlock();
        if (block.getType() != Material.HOPPER || !(block.getState(false) instanceof Hopper hopper)
                || !(block.getBlockData() instanceof org.bukkit.block.data.type.Hopper data) || !data.isEnabled()) return;
        String filter = filter(hopper);
        if (filter == null || !(block.getRelative(BlockFace.UP).getState(false) instanceof Container container)) return;
        Inventory source = container.getInventory();
        if (!supportsSlotSearch(source)) return;
        Inventory destination = hopper.getInventory();
        ItemStack[] items = source.getStorageContents();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack stack = items[slot];
            if (stack == null || stack.isEmpty() || !matches(filter, stack)) continue;
            ItemStack one = stack.asOne();
            var move = new InventoryMoveItemEvent(source, one.clone(), destination, false);
            if (!move.callEvent() || !move.getItem().equals(one)) return;
            ItemStack current = source.getItem(slot);
            if (current == null || current.isEmpty() || !current.isSimilar(one)) return;
            if (destination.addItem(one).isEmpty()) {
                source.setItem(slot, current.getAmount() == 1 ? null : current.asQuantity(current.getAmount() - 1));
            }
            return;
        }
    }

    private static String filter(Hopper hopper) {
        return hopper.getPersistentDataContainer().get(FILTER, PersistentDataType.STRING);
    }

    private static boolean matches(String filter, ItemStack item) {
        String material = item.getType().name();
        if (!filter.startsWith(",")) return filter.equals(material);
        // Accept the first populated slot from the earlier multi-slot build.
        int start = 1;
        while (start < filter.length() && filter.charAt(start) == ',') start++;
        int end = filter.indexOf(',', start);
        return end - start == material.length() && filter.startsWith(material, start);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND) return;
        var block = event.getClickedBlock();
        if (block == null || block.getType() != Material.HOPPER || !(block.getState(false) instanceof Hopper hopper)) return;

        event.setCancelled(true);
        Menu menu = new Menu(block.getLocation());
        menu.refresh(readMaterial(hopper));
        event.getPlayer().openInventory(menu.inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getType() == InventoryType.HOPPER && top.getHolder(false) instanceof Hopper hopper) {
            checkManualDeposit(event, top, filter(hopper));
            return;
        }
        if (!(top.getHolder() instanceof Menu menu)) return;
        int slot = event.getRawSlot();
        if (slot >= menu.inventory.getSize() && !event.isShiftClick() && event.getClick() != ClickType.DOUBLE_CLICK) return;
        event.setCancelled(true);
        if (slot != FILTER_SLOT || (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT)) return;

        Location location = menu.location;
        Location viewer = event.getWhoClicked().getLocation();
        if (!location.getWorld().equals(viewer.getWorld()) || viewer.distanceSquared(location) > 64
                || !location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)
                || !(location.getBlock().getState() instanceof Hopper hopper)) return;

        ItemStack cursor = event.getCursor();
        Material allowed = event.getClick() == ClickType.RIGHT || cursor.isEmpty() ? null : cursor.getType();
        if (allowed != null && Arrays.stream(hopper.getInventory().getStorageContents())
                .anyMatch(item -> item != null && !item.isEmpty() && item.getType() != allowed)) {
            event.getWhoClicked().sendMessage(Component.text("Remove other item types from this hopper first.", NamedTextColor.RED));
            return;
        }
        var data = hopper.getPersistentDataContainer();
        if (allowed == null) data.remove(FILTER);
        else data.set(FILTER, PersistentDataType.STRING, allowed.name());
        if (hopper.update(false, false)) {
            for (var player : location.getWorld().getPlayers()) {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu other
                        && other.location.equals(location)) other.refresh(allowed);
            }
        }
    }

    private void checkManualDeposit(InventoryClickEvent event, Inventory top, String filter) {
        if (filter == null || event.getRawSlot() < 0) return;
        ItemStack incoming;
        if (event.getRawSlot() >= top.getSize()) {
            if (event.getAction() != org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY) return;
            incoming = event.getCurrentItem();
        } else {
            incoming = switch (event.getAction()) {
                case PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> event.getCursor();
                case HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> {
                    if (event.getClick() == ClickType.SWAP_OFFHAND && event.getWhoClicked() instanceof Player player) {
                        yield player.getInventory().getItemInOffHand();
                    }
                    int button = event.getHotbarButton();
                    yield button >= 0 ? event.getWhoClicked().getInventory().getItem(button) : null;
                }
                default -> null;
            };
        }
        if (incoming != null && !incoming.isEmpty() && !matches(filter, incoming)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder() instanceof Menu) {
            if (event.getRawSlots().stream().anyMatch(slot -> slot < top.getSize())) event.setCancelled(true);
        } else if (top.getType() == InventoryType.HOPPER && top.getHolder(false) instanceof Hopper hopper) {
            String filter = filter(hopper);
            if (filter != null && event.getNewItems().entrySet().stream()
                    .anyMatch(entry -> entry.getKey() < top.getSize() && !matches(filter, entry.getValue()))) {
                event.setCancelled(true);
            }
        }
    }

    private static Material readMaterial(Hopper hopper) {
        String saved = filter(hopper);
        if (saved == null) return null;
        if (!saved.startsWith(",")) return Material.getMaterial(saved);
        for (String entry : saved.split(",")) {
            if (!entry.isEmpty()) return Material.getMaterial(entry);
        }
        return null;
    }

    private static final class Menu implements InventoryHolder {
        private final Location location;
        private final Inventory inventory = Bukkit.createInventory(this, 9, Component.text("Hopper Filter"));

        Menu(Location location) { this.location = location; }

        @Override
        public Inventory getInventory() { return inventory; }

        void refresh(Material allowed) {
            ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            filler.editMeta(meta -> meta.displayName(Component.empty()));
            for (int slot = 0; slot < inventory.getSize(); slot++) inventory.setItem(slot, filler);
            ItemStack sample = new ItemStack(allowed == null ? Material.HOPPER : allowed);
            sample.editMeta(meta -> {
                meta.displayName(Component.text("Allow this one item only", NamedTextColor.GOLD));
                meta.lore(java.util.List.of(
                        Component.text(allowed == null ? "Currently accepts everything." : "Allowed: " + allowed.name()),
                        Component.text("Click with an item to set the filter."),
                        Component.text("Right-click to clear it.")));
            });
            inventory.setItem(FILTER_SLOT, sample);
        }
    }
}
