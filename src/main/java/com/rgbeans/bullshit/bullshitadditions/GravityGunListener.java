package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.Sound;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static com.rgbeans.bullshit.bullshitadditions.GameplaySettings.Setting.GRAB_PLAYERS;

public final class GravityGunListener implements Listener {

    private static final NamespacedKey CARRIED_BLOCK = new NamespacedKey("bullshitadditions", "carried_block");

    private final Plugin plugin;
    private final GameplaySettings settings;
    private final Map<UUID, UUID> held = new HashMap<>();
    private final Map<UUID, Long> lastAction = new HashMap<>();

    GravityGunListener(Plugin plugin, GameplaySettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        for (var world : plugin.getServer().getWorlds()) {
            for (BlockDisplay display : world.getEntitiesByClass(BlockDisplay.class)) {
                restoreCarriedBlock(display);
            }
        }
        plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
    }

    private boolean onCooldown(Player player) {
        long now = System.currentTimeMillis();
        Long last = lastAction.get(player.getUniqueId());
        if (last != null && now - last < 200) {
            return true;
        }
        lastAction.put(player.getUniqueId(), now);
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!GravityGun.isGravityGun(event.getItem())) return;

        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) return;

        if (action == Action.RIGHT_CLICK_BLOCK && event.useInteractedBlock() == Event.Result.DENY) return;
        event.setCancelled(true);

        boolean holding = held.containsKey(player.getUniqueId());
        if (left) {
            if (holding) release(player);
            return;
        }

        if (onCooldown(player)) return;
        if (holding) {
            shoot(player);
        } else if (player.isSneaking() && event.getClickedBlock() != null) {
            grabBlock(player, event.getClickedBlock());
        } else {
            grabByRay(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) return;

        event.setCancelled(true);

        if (onCooldown(player)) return;
        if (held.containsKey(player.getUniqueId())) {
            shoot(player);
        } else {
            grab(player, event.getRightClicked());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) return;

        UUID targetId = held.get(player.getUniqueId());
        if (targetId != null && targetId.equals(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            release(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Entity target = takeHeld(event.getPlayer());
        if (target instanceof BlockDisplay display) releaseBlock(display, new Vector());
        lastAction.remove(event.getPlayer().getUniqueId());
    }

    private void grabByRay(Player player) {
        Location eye = player.getEyeLocation();
        RayTraceResult result = player.getWorld().rayTrace(
                eye, eye.getDirection(), GravityGun.GRAB_RANGE, FluidCollisionMode.NEVER, true, 0.5,
                entity -> canGrab(player, entity));

        if (result == null || result.getHitEntity() == null) {
            player.sendActionBar(ChatColor.RED + "No entity within reach!");
            return;
        }
        grab(player, result.getHitEntity());
    }

    private void grab(Player player, Entity target) {
        if (!canGrab(player, target)) return;
        hold(player, target);
    }

    private boolean canGrab(Player player, Entity target) {
        return !target.equals(player) && !(target instanceof Projectile || target instanceof FallingBlock || target instanceof Display
                || target instanceof EnderDragon || target instanceof EnderDragonPart || target instanceof Mannequin)
                && (!(target instanceof Player) || settings.enabled(GRAB_PLAYERS))
                && !held.containsValue(target.getUniqueId());
    }

    private void grabBlock(Player player, Block block) {
        if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SPECTATOR) return;
        double reach = GravityGun.GRAB_RANGE;
        if (player.getEyeLocation().distanceSquared(block.getLocation().add(0.5, 0.5, 0.5)) > reach * reach) return;
        if (!player.isOp() && block.getWorld().equals(plugin.getServer().getWorlds().getFirst())) {
            Location spawn = block.getWorld().getSpawnLocation();
            int radius = plugin.getServer().getSpawnRadius();
            if (radius > 0 && Math.abs(block.getX() - spawn.getBlockX()) <= radius
                    && Math.abs(block.getZ() - spawn.getBlockZ()) <= radius) return;
        }

        BlockData data = block.getBlockData();
        if (!block.getType().isOccluding() || !block.getType().isItem()
                || block.getType().getHardness() < 0 || block.getState() instanceof TileState
                || data instanceof Waterlogged water && water.isWaterlogged()) {
            player.sendActionBar(ChatColor.RED + "This block cannot be picked up.");
            return;
        }

        BlockBreakEvent protectionCheck = new BlockBreakEvent(block, player);
        protectionCheck.setDropItems(false);
        protectionCheck.setExpToDrop(0);
        if (!protectionCheck.callEvent() || !block.getBlockData().equals(data)) return;

        BlockDisplay carried = block.getWorld().spawn(block.getLocation(), BlockDisplay.class, entity -> {
            entity.setBlock(data);
            entity.setTeleportDuration(1);
            entity.getPersistentDataContainer().set(CARRIED_BLOCK, PersistentDataType.BOOLEAN, true);
        });
        if (!carried.isValid()) return;
        if (!block.getBlockData().equals(data)) {
            carried.remove();
            return;
        }
        block.setType(Material.AIR);
        hold(player, carried);
    }

    private void hold(Player player, Entity target) {
        held.put(player.getUniqueId(), target.getUniqueId());
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.6f, 1.8f);
        player.sendActionBar(ChatColor.AQUA + "Grabbed " + targetName(target));
    }

    private void shoot(Player player) {
        Entity target = takeHeld(player);
        if (target == null) return;
        Vector velocity = player.getEyeLocation().getDirection().multiply(GravityGun.SHOOT_SPEED);
        if (target instanceof BlockDisplay display) releaseBlock(display, velocity);
        else target.setVelocity(velocity);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.8f, 1.5f);
    }

    private void release(Player player) {
        Entity target = takeHeld(player);
        if (target == null) return;
        if (target instanceof BlockDisplay display) releaseBlock(display, new Vector());
        else {
            target.setVelocity(new Vector());
            target.setFallDistance(0f);
        }
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 0.6f, 1.8f);
        player.sendActionBar(ChatColor.GRAY + "Released " + targetName(target));
    }

    private Entity takeHeld(Player player) {
        UUID targetId = held.remove(player.getUniqueId());
        if (targetId == null) return null;
        Entity target = plugin.getServer().getEntity(targetId);
        if (target == null || target.isDead()) return null;
        return target;
    }

    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof BlockDisplay display) restoreCarriedBlock(display);
            // Recover falling blocks left behind by v1.4.
            if (entity instanceof FallingBlock block
                    && block.getPersistentDataContainer().has(CARRIED_BLOCK, PersistentDataType.BOOLEAN)) {
                resumeFalling(block);
            }
        }
    }

    @EventHandler
    public void onEntitiesUnload(EntitiesUnloadEvent event) {
        for (Entity entity : event.getEntities()) {
            held.values().remove(entity.getUniqueId());
        }
    }

    void releaseAll() {
        for (UUID targetId : held.values()) {
            if (plugin.getServer().getEntity(targetId) instanceof BlockDisplay display) {
                releaseBlock(display, new Vector());
            }
        }
        held.clear();
    }

    private String targetName(Entity entity) {
        if (entity instanceof BlockDisplay display) return display.getBlock().getMaterial().name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
        return entity.getName();
    }

    private void restoreCarriedBlock(BlockDisplay display) {
        if (!held.containsValue(display.getUniqueId())
                && display.getPersistentDataContainer().has(CARRIED_BLOCK, PersistentDataType.BOOLEAN)) {
            releaseBlock(display, new Vector());
        }
    }

    private void releaseBlock(BlockDisplay display, Vector velocity) {
        BlockData data = display.getBlock();
        Location position = display.getLocation().add(0.5, 0, 0.5);
        if (!position.getBlock().isEmpty()) {
            position.getWorld().dropItemNaturally(position, new org.bukkit.inventory.ItemStack(data.getMaterial()));
        } else {
            FallingBlock falling = position.getWorld().spawn(position, FallingBlock.class, entity -> {
                entity.setBlockData(data);
                entity.setHurtEntities(false);
                entity.setVelocity(velocity);
            });
            if (!falling.isValid()) {
                position.getWorld().dropItemNaturally(position, new org.bukkit.inventory.ItemStack(data.getMaterial()));
            }
        }
        display.remove();
    }

    private void resumeFalling(Entity entity) {
        if (entity instanceof FallingBlock block) {
            block.setGravity(true);
            block.shouldAutoExpire(true);
            block.setTicksLived(1);
            block.getPersistentDataContainer().remove(CARRIED_BLOCK);
        }
    }

    private void tick() {
        Iterator<Map.Entry<UUID, UUID>> it = held.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, UUID> entry = it.next();
            Player player = plugin.getServer().getPlayer(entry.getKey());
            Entity target = plugin.getServer().getEntity(entry.getValue());
            if (player == null || !player.isOnline() || target == null || target.isDead()
                    || !target.getWorld().equals(player.getWorld())
                    || target instanceof Player && !settings.enabled(GRAB_PLAYERS)
                    || !GravityGun.isGravityGun(player.getInventory().getItemInMainHand())) {
                if (target instanceof BlockDisplay display) releaseBlock(display, new Vector());
                it.remove();
                continue;
            }

            Location hold = player.getEyeLocation().add(
                    player.getEyeLocation().getDirection().multiply(GravityGun.HOLD_DISTANCE));
            if (target instanceof BlockDisplay display) {
                hold.subtract(0.5, 0.5, 0.5);
                if (hold.getWorld().isChunkLoaded(hold.getBlockX() >> 4, hold.getBlockZ() >> 4)) {
                    display.teleport(hold);
                }
            } else {
                Vector pull = hold.toVector().subtract(target.getLocation().toVector()).multiply(0.5);
                target.setVelocity(pull);
                target.setFallDistance(0f);
            }
        }
    }
}
