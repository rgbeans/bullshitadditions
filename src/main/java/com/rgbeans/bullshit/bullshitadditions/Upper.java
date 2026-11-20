package com.rgbeans.bullshit.bullshitadditions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class Upper implements Listener {
    private static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "upper");
    private static final NamespacedKey OUTPUT = new NamespacedKey("bullshitadditions", "upper_output");
    private static final List<BlockFace> OUTPUTS = List.of(
            BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST);
    private static final long TRANSFER_INTERVAL = 8;

    private final JavaPlugin plugin;
    private final Map<Location, BlockDisplay> uppers = new HashMap<>();

    Upper(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    static ItemStack createItem() {
        ItemStack item = new ItemStack(Material.BARREL);
        var meta = item.getItemMeta();
        meta.displayName(Component.text("Upper", NamedTextColor.GOLD));
        meta.lore(List.of(
                Component.text("Pulls items from below", NamedTextColor.GRAY),
                Component.text("Sneak + right-click to change output", NamedTextColor.GRAY)));
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    void start() {
        for (var world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) register(chunk);
        }
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, TRANSFER_INTERVAL, TRANSFER_INTERVAL);
    }

    void stop() {
        uppers.values().forEach(BlockDisplay::remove);
        uppers.clear();
    }

    private static boolean isUpper(BlockState state) {
        return state instanceof Barrel barrel
                && barrel.getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }

    private Barrel upper(Block block) {
        if (block == null || block.getType() != Material.BARREL) return null;
        BlockState state = block.getState(false);
        return isUpper(state) ? (Barrel) state : null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!event.getItemInHand().getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN)
                || !(event.getBlockPlaced().getState() instanceof Barrel barrel)) return;
        barrel.customName(Component.text("Upper", NamedTextColor.GOLD));
        barrel.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);
        if (barrel.update(false, false)) showMarker(barrel.getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(BlockDropItemEvent event) {
        if (!isUpper(event.getBlockState())) return;
        forget(event.getBlockState().getLocation());
        for (Item drop : event.getItems()) {
            if (drop.getItemStack().getType() == Material.BARREL && drop.getItemStack().getAmount() == 1) {
                drop.setItemStack(createItem());
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK
                || event.useInteractedBlock() == Event.Result.DENY) return;
        Barrel barrel = upper(event.getClickedBlock());
        if (barrel == null || !event.getPlayer().isSneaking()) return;

        event.setCancelled(true);
        BlockFace next = OUTPUTS.get((OUTPUTS.indexOf(output(barrel)) + 1) % OUTPUTS.size());
        barrel.getPersistentDataContainer().set(OUTPUT, PersistentDataType.STRING, next.name());
        if (barrel.update(false, false)) {
            event.getPlayer().sendActionBar(Component.text("Upper output: " + next.name().toLowerCase(Locale.ROOT),
                    NamedTextColor.GOLD));
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        register(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();
        for (Location location : List.copyOf(uppers.keySet())) {
            if (location.getWorld().equals(chunk.getWorld())
                    && location.getBlockX() >> 4 == chunk.getX() && location.getBlockZ() >> 4 == chunk.getZ()) {
                forget(location);
            }
        }
    }

    private void register(Chunk chunk) {
        for (BlockState state : chunk.getTileEntities()) {
            if (isUpper(state)) showMarker(state.getLocation());
        }
    }

    private void showMarker(Location location) {
        if (uppers.containsKey(location)) return;
        // A copper band around the barrel stays visible even with a container on top.
        BlockDisplay marker = location.getWorld().spawn(location.clone().add(-0.015, 0.36, -0.015),
                BlockDisplay.class, display -> {
                    display.setBlock(Material.WAXED_CUT_COPPER.createBlockData());
                    display.setTransformation(new Transformation(new Vector3f(), new Quaternionf(),
                            new Vector3f(1.03f, 0.28f, 1.03f), new Quaternionf()));
                    display.setPersistent(false);
                    display.setInvulnerable(true);
                });
        uppers.put(location, marker);
    }

    private void forget(Location location) {
        BlockDisplay marker = uppers.remove(location);
        if (marker != null) marker.remove();
    }

    private void tick() {
        for (Location location : List.copyOf(uppers.keySet())) {
            if (!location.isWorldLoaded() || !location.getWorld().isChunkLoaded(
                    location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                forget(location);
                continue;
            }
            Block block = location.getBlock();
            Barrel barrel = upper(block);
            if (barrel == null) {
                forget(location);
                continue;
            }
            if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) continue;
            Inventory storage = barrel.getInventory();
            Inventory destination = inventoryAt(block.getRelative(output(barrel)));
            if (destination != null) moveOne(storage, destination, true);

            Block below = block.getRelative(BlockFace.DOWN);
            Inventory source = inventoryAt(below);
            if (source != null) moveOne(source, storage, false);
            else if (withinWorld(below)) collectLooseItem(below, storage);
        }
    }

    private static BlockFace output(Barrel barrel) {
        String name = barrel.getPersistentDataContainer().getOrDefault(OUTPUT, PersistentDataType.STRING, "UP");
        for (BlockFace face : OUTPUTS) {
            if (face.name().equals(name)) return face;
        }
        return BlockFace.UP;
    }

    private static boolean withinWorld(Block block) {
        var world = block.getWorld();
        return block.getY() >= world.getMinHeight() && block.getY() < world.getMaxHeight()
                && world.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4);
    }

    private static Inventory inventoryAt(Block block) {
        if (!withinWorld(block) || !(block.getState(false) instanceof InventoryHolder holder)) return null;
        Inventory inventory = holder.getInventory();
        return switch (inventory.getType()) {
            case CHEST, BARREL, HOPPER, DROPPER, DISPENSER, SHULKER_BOX -> inventory;
            default -> null;
        };
    }

    private void moveOne(Inventory source, Inventory destination, boolean push) {
        ItemStack[] items = source.getStorageContents();
        for (int slot = 0; slot < items.length; slot++) {
            ItemStack stack = items[slot];
            if (stack == null || stack.isEmpty()) continue;
            ItemStack one = stack.asOne();
            if (!hasRoom(destination, one)) continue;

            var move = new InventoryMoveItemEvent(source, one.clone(), destination, push);
            if (!move.callEvent() || !move.getItem().equals(one)) return;
            ItemStack current = source.getItem(slot);
            if (current == null || current.isEmpty() || !current.isSimilar(one)) return;
            if (destination.addItem(one).isEmpty()) {
                source.setItem(slot, remainder(current));
            }
            return;
        }
    }

    private void collectLooseItem(Block below, Inventory destination) {
        BoundingBox area = BoundingBox.of(below, below);
        for (var entity : below.getWorld().getNearbyEntities(area, candidate -> candidate instanceof Item)) {
            Item item = (Item) entity;
            ItemStack one = item.getItemStack().asOne();
            if (!hasRoom(destination, one)) continue;
            if (!new InventoryPickupItemEvent(destination, item).callEvent() || !item.isValid()) continue;
            ItemStack current = item.getItemStack();
            if (current.isEmpty() || !current.isSimilar(one) || !destination.addItem(one).isEmpty()) continue;
            ItemStack remaining = remainder(current);
            if (remaining == null) item.remove();
            else item.setItemStack(remaining);
            return;
        }
    }

    private static boolean hasRoom(Inventory inventory, ItemStack item) {
        if (inventory.getType() == InventoryType.SHULKER_BOX && Tag.SHULKER_BOXES.isTagged(item.getType())) return false;
        int maxStack = Math.min(inventory.getMaxStackSize(), item.getMaxStackSize());
        for (ItemStack slot : inventory.getStorageContents()) {
            if (slot == null || slot.isEmpty() || slot.isSimilar(item) && slot.getAmount() < maxStack) return true;
        }
        return false;
    }

    private static ItemStack remainder(ItemStack stack) {
        return stack.getAmount() == 1 ? null : stack.asQuantity(stack.getAmount() - 1);
    }
}
