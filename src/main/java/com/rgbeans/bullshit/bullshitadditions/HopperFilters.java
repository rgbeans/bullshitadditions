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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

final class HopperFilters implements Listener {
    private static final NamespacedKey FILTER = new NamespacedKey("bullshitadditions", "hopper_filter");
    private static final int FILTER_SLOTS = 8;

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
        return filter.contains("," + item.getType().name() + ",");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND) return;
        var block = event.getClickedBlock();
        if (block == null || block.getType() != Material.HOPPER || !(block.getState(false) instanceof Hopper hopper)) return;

        event.setCancelled(true);
        Menu menu = new Menu(block.getLocation());
        menu.refresh(readSlots(hopper));
        event.getPlayer().openInventory(menu.inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;
        int slot = event.getRawSlot();
        if (slot >= menu.inventory.getSize() && !event.isShiftClick() && event.getClick() != ClickType.DOUBLE_CLICK) return;
        event.setCancelled(true);
        if (slot < 0 || slot >= FILTER_SLOTS || (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT)) return;

        Location location = menu.location;
        Location viewer = event.getWhoClicked().getLocation();
        if (!location.getWorld().equals(viewer.getWorld()) || viewer.distanceSquared(location) > 64
                || !location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)
                || !(location.getBlock().getState() instanceof Hopper hopper)) return;

        Material[] slots = readSlots(hopper);
        ItemStack cursor = event.getCursor();
        slots[slot] = event.getClick() == ClickType.RIGHT || cursor.isEmpty() ? null : cursor.getType();
        var data = hopper.getPersistentDataContainer();
        if (Arrays.stream(slots).allMatch(material -> material == null)) {
            data.remove(FILTER);
        } else {
            // Delimit both sides for exact material matching without parsing on each transfer.
            StringBuilder encoded = new StringBuilder(",");
            for (Material material : slots) encoded.append(material == null ? "" : material.name()).append(',');
            data.set(FILTER, PersistentDataType.STRING, encoded.toString());
        }
        if (hopper.update(false, false)) {
            for (var player : location.getWorld().getPlayers()) {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof Menu other
                        && other.location.equals(location)) other.refresh(slots);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu menu
                && event.getRawSlots().stream().anyMatch(slot -> slot < menu.inventory.getSize())) {
            event.setCancelled(true);
        }
    }

    private static Material[] readSlots(Hopper hopper) {
        Material[] slots = new Material[FILTER_SLOTS];
        String filter = hopper.getPersistentDataContainer().get(FILTER, PersistentDataType.STRING);
        if (filter == null) return slots;
        String[] entries = filter.split(",", -1);
        for (int i = 0; i < slots.length && i + 1 < entries.length; i++) {
            if (!entries[i + 1].isEmpty()) slots[i] = Material.getMaterial(entries[i + 1]);
        }
        return slots;
    }

    private static final class Menu implements InventoryHolder {
        private final Location location;
        private final Inventory inventory = Bukkit.createInventory(this, 9, Component.text("Hopper Filter"));

        Menu(Location location) { this.location = location; }

        @Override
        public Inventory getInventory() { return inventory; }

        void refresh(Material[] slots) {
            for (int i = 0; i < slots.length; i++) {
                inventory.setItem(i, slots[i] == null ? null : new ItemStack(slots[i]));
            }
            ItemStack info = new ItemStack(Material.HOPPER);
            info.editMeta(meta -> {
                meta.displayName(Component.text("Allowed item types", NamedTextColor.GOLD));
                meta.lore(java.util.List.of(
                        Component.text("Click a slot with an item to allow its type."),
                        Component.text("Right-click a slot to clear it."),
                        Component.text("An empty filter accepts everything.")));
            });
            inventory.setItem(FILTER_SLOTS, info);
        }
    }
}
