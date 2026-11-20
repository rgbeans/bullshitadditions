package com.rgbeans.bullshit.bullshitadditions;

import io.papermc.paper.event.block.BlockPreDispenseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

final class BlockMachines implements Listener {
    private static final NamespacedKey MACHINE = key("block_machine");
    private static final NamespacedKey INSTANCE = key("machine_instance");
    private static final NamespacedKey FUEL = key("machine_fuel");
    private static final NamespacedKey INTERVAL = key("machine_interval");
    private static final NamespacedKey REMAINING = key("machine_remaining");
    private static final NamespacedKey TOOL = key("machine_pickaxe");
    private static final int DEFAULT_INTERVAL = 20;
    private static final int MAX_INTERVAL = 1200;
    private static final int MENDING_COST = 1;

    private static final int INTERVAL_SLOT = 2;
    private static final int FUEL_SLOT = 4;
    private static final int TOOL_SLOT = 6;

    enum Kind {
        PLACER(Material.DROPPER, "Block Placer"),
        BREAKER(Material.DISPENSER, "Block Breaker");

        final Material material;
        final String title;

        Kind(Material material, String title) {
            this.material = material;
            this.title = title;
        }

        static Kind of(Material material) {
            for (Kind kind : values()) {
                if (kind.material == material) return kind;
            }
            return null;
        }
    }

    private final Set<Location> machines = new HashSet<>();
    private final Set<Controls> openControls = new HashSet<>();
    private final JavaPlugin plugin;

    BlockMachines(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    void start() {
        for (var world : plugin.getServer().getWorlds()) {
            for (var chunk : world.getLoadedChunks()) register(chunk.getTileEntities());
        }
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::refreshControls, 10L, 10L);
    }

    static ItemStack createItem(Kind kind) {
        ItemStack item = new ItemStack(kind.material);
        var meta = item.getItemMeta();
        meta.displayName(Component.text(kind.title, NamedTextColor.AQUA));
        meta.lore(List.of(
                Component.text("Right-click for storage", NamedTextColor.GRAY),
                Component.text("Sneak + right-click for fuel and controls", NamedTextColor.GRAY)));
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(MACHINE, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    private static NamespacedKey key(String path) {
        return new NamespacedKey("bullshitadditions", path);
    }

    private static Container machine(Block block) {
        if (Kind.of(block.getType()) != null && block.getState() instanceof Container container
                && container.getPersistentDataContainer().has(MACHINE, PersistentDataType.BOOLEAN)) {
            return container;
        }
        return null;
    }

    private void register(BlockState[] states) {
        for (BlockState state : states) {
            if (Kind.of(state.getType()) != null && state instanceof Container container
                    && container.getPersistentDataContainer().has(MACHINE, PersistentDataType.BOOLEAN)) {
                machines.add(state.getLocation());
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        register(event.getChunk().getTileEntities());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (Kind.of(block.getType()) == null || !event.getItemInHand().getPersistentDataContainer()
                .has(MACHINE, PersistentDataType.BOOLEAN) || !(block.getState() instanceof Container container)) return;
        container.customName(Component.text(Kind.of(block.getType()).title, NamedTextColor.AQUA));
        container.getPersistentDataContainer().set(MACHINE, PersistentDataType.BOOLEAN, true);
        container.getPersistentDataContainer().set(INSTANCE, PersistentDataType.STRING, UUID.randomUUID().toString());
        container.update(false, false);
        machines.add(block.getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Container machine = machine(event.getBlock());
        if (machine == null) return;
        machines.remove(machine.getLocation());
        if (!event.isDropItems()) return;
        event.setDropItems(false);
        var world = event.getBlock().getWorld();
        Location position = machine.getLocation().add(0.5, 0.5, 0.5);
        world.dropItemNaturally(position, createItem(Kind.of(machine.getType())));
        ItemStack pickaxe = tool(machine);
        if (pickaxe != null) world.dropItemNaturally(position, pickaxe);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPreDispense(BlockPreDispenseEvent event) {
        if (machine(event.getBlock()) != null) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (event.getInitiator().equals(event.getSource())
                && event.getSource().getHolder() instanceof Container source
                && source.getType() == Material.DROPPER
                && source.getPersistentDataContainer().has(MACHINE, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
        }
        if (event.getDestination().getHolder() instanceof Container destination
                && destination.getType() == Material.DISPENSER
                && destination.getPersistentDataContainer().has(MACHINE, PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Container machine = machine(event.getClickedBlock());
        if (machine == null) return;
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        if (event.getPlayer().isSneaking()) {
            Controls controls = new Controls(machine);
            controls.refresh(machine);
            event.getPlayer().openInventory(controls.inventory);
            openControls.add(controls);
        } else {
            event.getPlayer().openInventory(machine.getInventory());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onControlClick(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Controls controls)) return;
        int slot = event.getRawSlot();
        if (slot >= controls.inventory.getSize() && !event.isShiftClick()
                && event.getClick() != ClickType.DOUBLE_CLICK) return;
        event.setCancelled(true);
        ClickType click = event.getClick();
        if (click != ClickType.LEFT && click != ClickType.RIGHT
                && click != ClickType.SHIFT_LEFT && click != ClickType.SHIFT_RIGHT) return;
        Container machine = controls.machine();
        if (machine == null) return;
        var data = machine.getPersistentDataContainer();
        ItemStack cursor = event.getCursor();
        ItemStack newCursor = cursor;
        switch (slot) {
            case INTERVAL_SLOT -> {
                int step = click.isShiftClick() ? 20 : 1;
                int interval = Math.clamp(interval(machine) + (click.isLeftClick() ? step : -step), 1, MAX_INTERVAL);
                data.set(INTERVAL, PersistentDataType.INTEGER, interval);
                data.set(REMAINING, PersistentDataType.INTEGER, interval);
            }
            case FUEL_SLOT -> {
                if (cursor.isEmpty() || cursor.hasItemMeta()) return;
                Long value = EMCEngine.get(cursor.getType());
                if (value == null || value <= 0) return;
                int amount = click.isRightClick() ? 1 : cursor.getAmount();
                long balance = fuel(machine);
                if (value > (Long.MAX_VALUE - balance) / amount) return;
                data.set(FUEL, PersistentDataType.LONG, balance + value * amount);
                newCursor = cursor.clone();
                newCursor.setAmount(cursor.getAmount() - amount);
            }
            case TOOL_SLOT -> {
                if (controls.kind != Kind.BREAKER) return;
                if (!cursor.isEmpty() && (!Tag.ITEMS_PICKAXES.isTagged(cursor.getType()) || cursor.getAmount() != 1)) return;
                newCursor = tool(machine);
                storeTool(machine, cursor.isEmpty() ? null : cursor);
            }
            default -> { return; }
        }
        if (machine.update(false, false)) {
            if (newCursor != cursor) event.getWhoClicked().setItemOnCursor(newCursor);
            controls.refresh(machine);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onControlDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Controls controls
                && event.getRawSlots().stream().anyMatch(slot -> slot < controls.inventory.getSize())) {
            event.setCancelled(true);
        }
    }

    private void refreshControls() {
        Iterator<Controls> iterator = openControls.iterator();
        while (iterator.hasNext()) {
            Controls controls = iterator.next();
            if (controls.inventory.getViewers().isEmpty()) {
                iterator.remove();
                continue;
            }
            Container machine = controls.machine();
            if (machine != null) controls.refresh(machine);
            else {
                for (var viewer : List.copyOf(controls.inventory.getViewers())) viewer.closeInventory();
                iterator.remove();
            }
        }
    }

    private void tick() {
        // Work from a snapshot since block changes can trigger other listeners.
        for (Location location : List.copyOf(machines)) {
            if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                machines.remove(location);
                continue;
            }
            Container machine = machine(location.getBlock());
            if (machine == null) {
                machines.remove(location);
                continue;
            }
            if (fuel(machine) > 0) operate(machine);
        }
    }

    private void operate(Container machine) {
        var data = machine.getPersistentDataContainer();
        long balance = fuel(machine) - 1;
        int remaining = data.getOrDefault(REMAINING, PersistentDataType.INTEGER, interval(machine)) - 1;
        if (remaining <= 0) {
            remaining = interval(machine);
            Block block = machine.getBlock();
            Block target = block.getRelative(((Directional) machine.getBlockData()).getFacing());
            if (target.getWorld().isChunkLoaded(target.getX() >> 4, target.getZ() >> 4)
                    && target.getY() >= target.getWorld().getMinHeight() && target.getY() < target.getWorld().getMaxHeight()
                    && target.getWorld().getWorldBorder().isInside(target.getLocation().add(0.5, 0.5, 0.5))) {
                if (machine.getType() == Material.DROPPER) place(machine, target);
                else balance -= breakBlock(machine, target, balance);
            }
        }
        data.set(FUEL, PersistentDataType.LONG, balance);
        data.set(REMAINING, PersistentDataType.INTEGER, remaining);
        machine.update(false, false);
    }

    private void place(Container machine, Block target) {
        if (!target.isEmpty()) return;
        Inventory inventory = machine.getSnapshotInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null || item.isEmpty() || !item.getType().isBlock() || item.hasItemMeta()) continue;
            var blockData = item.getType().createBlockData();
            if (isMultiBlock(blockData)
                    || !blockData.isSupported(target) || !target.canPlace(blockData)) continue;
            var attempt = new BlockDispenseEvent(machine.getBlock(), item.asOne(), new Vector(target.getX(), target.getY(), target.getZ()));
            if (!attempt.callEvent() || !target.isEmpty() || !attempt.getItem().isSimilar(item)) return;
            target.setBlockData(blockData);
            item.setAmount(item.getAmount() - 1);
            inventory.setItem(slot, item.isEmpty() ? null : item);
            return;
        }
    }

    private boolean isMultiBlock(BlockData data) {
        return data instanceof Bed || data instanceof Bisected && !(data instanceof Stairs || data instanceof TrapDoor);
    }

    private int breakBlock(Container machine, Block target, long balance) {
        if (target.isEmpty() || target.isLiquid() || target.getType().getHardness() < 0
                || target.getType() == Material.PISTON_HEAD || target.getType() == Material.MOVING_PISTON
                || target.getState() instanceof TileState || isMultiBlock(target.getBlockData())) return 0;
        ItemStack pickaxe = tool(machine);
        if (pickaxe == null || !target.isPreferredTool(pickaxe)) return 0;
        boolean mending = pickaxe.containsEnchantment(Enchantment.MENDING);
        if (mending && balance < MENDING_COST) return 0;

        Inventory inventory = machine.getSnapshotInventory();
        List<ItemStack> drops = new ArrayList<>(target.getDrops(pickaxe));
        Inventory staged = Bukkit.createInventory(null, inventory.getSize());
        ItemStack[] contents = inventory.getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            if (contents[slot] != null) contents[slot] = contents[slot].clone();
        }
        staged.setContents(contents);
        for (ItemStack drop : drops) {
            if (!staged.addItem(drop.clone()).isEmpty()) return 0;
        }
        target.setType(Material.AIR);
        inventory.setContents(staged.getContents());
        if (!mending) wearPickaxe(machine, pickaxe);
        return mending ? MENDING_COST : 0;
    }

    private void wearPickaxe(Container machine, ItemStack pickaxe) {
        if (!(pickaxe.getItemMeta() instanceof Damageable meta)) return;
        int max = meta.hasMaxDamage() ? meta.getMaxDamage() : pickaxe.getType().getMaxDurability();
        int damage = meta.getDamage() + 1;
        if (damage >= max) {
            storeTool(machine, null);
        } else {
            meta.setDamage(damage);
            pickaxe.setItemMeta(meta);
            storeTool(machine, pickaxe);
        }
    }

    private static int interval(Container machine) {
        return machine.getPersistentDataContainer().getOrDefault(INTERVAL, PersistentDataType.INTEGER, DEFAULT_INTERVAL);
    }

    private static long fuel(Container machine) {
        return machine.getPersistentDataContainer().getOrDefault(FUEL, PersistentDataType.LONG, 0L);
    }

    private static ItemStack tool(Container machine) {
        byte[] bytes = machine.getPersistentDataContainer().get(TOOL, PersistentDataType.BYTE_ARRAY);
        return bytes == null ? null : ItemStack.deserializeBytes(bytes);
    }

    private static void storeTool(Container machine, ItemStack item) {
        if (item == null) machine.getPersistentDataContainer().remove(TOOL);
        else machine.getPersistentDataContainer().set(TOOL, PersistentDataType.BYTE_ARRAY, item.serializeAsBytes());
    }

    private static ItemStack icon(Material material, String title, String... lore) {
        ItemStack item = new ItemStack(material);
        var meta = item.getItemMeta();
        meta.displayName(Component.text(title, NamedTextColor.GOLD));
        meta.lore(java.util.Arrays.stream(lore).map(line -> (Component) Component.text(line, NamedTextColor.GRAY)).toList());
        item.setItemMeta(meta);
        return item;
    }

    private static final class Controls implements InventoryHolder {
        private final Location location;
        private final Kind kind;
        private final String instance;
        private final Inventory inventory;

        Controls(Container machine) {
            location = machine.getLocation();
            kind = Kind.of(machine.getType());
            instance = machine.getPersistentDataContainer().getOrDefault(INSTANCE, PersistentDataType.STRING, "");
            inventory = Bukkit.createInventory(this, 9, Component.text(kind.title + " Controls"));
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        Container machine() {
            if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) return null;
            Container machine = BlockMachines.machine(location.getBlock());
            if (machine == null || machine.getType() != kind.material) return null;
            return instance.equals(machine.getPersistentDataContainer().getOrDefault(INSTANCE, PersistentDataType.STRING, ""))
                    ? machine : null;
        }

        void refresh(Container machine) {
            inventory.setItem(INTERVAL_SLOT, icon(Material.CLOCK, "Interval: " + interval(machine) + " ticks",
                    "Left +1 / Right -1", "Shift changes by 20 ticks"));
            inventory.setItem(FUEL_SLOT, icon(Material.SUNFLOWER, "Fuel: " + EMCEngine.formatEmc(fuel(machine)) + " EMC",
                    "Click with an EMC item to burn it", "Left: entire stack / Right: one item",
                    "1 EMC per loaded operating tick"));
            if (kind == Kind.BREAKER) {
                ItemStack pickaxe = tool(machine);
                inventory.setItem(TOOL_SLOT, pickaxe != null ? pickaxe : icon(Material.IRON_PICKAXE,
                        "Pickaxe slot", "Click with a pickaxe to insert it",
                        "Mending uses +1 EMC per block instead of durability"));
            }
        }
    }
}
