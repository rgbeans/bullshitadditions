package com.rgbeans.bullshit.bullshitadditions;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.rgbeans.bullshit.bullshitadditions.GameplaySettings.Setting.*;

final class HerobrineSightings {
    private static final String SKIN = "https://textures.minecraft.net/texture/98b7ca3c7d314a61abed8fc18d797fc30b6efc8445425c4e250997e52e6cb";

    private final JavaPlugin plugin;
    private final GameplaySettings settings;
    private final ResolvableProfile profile;
    private final Set<Mannequin> visible = new HashSet<>();
    private int secondsSinceCheck;

    HerobrineSightings(JavaPlugin plugin, GameplaySettings settings) {
        this.plugin = plugin;
        this.settings = settings;
        var skinProfile = Bukkit.createProfileExact(UUID.randomUUID(), "Herobrine");
        String texture = "{\"textures\":{\"SKIN\":{\"url\":\"" + SKIN + "\"}}}";
        skinProfile.setProperty(new ProfileProperty("textures",
                Base64.getEncoder().encodeToString(texture.getBytes(StandardCharsets.UTF_8))));
        profile = ResolvableProfile.resolvableProfile(skinProfile);
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, 20L);
    }

    private void tick() {
        if (!settings.enabled(HEROBRINE)) {
            secondsSinceCheck = 0;
            return;
        }
        if (++secondsSinceCheck < settings.get(HEROBRINE_INTERVAL)) return;
        secondsSinceCheck = 0;
        double chance = settings.get(HEROBRINE_CHANCE);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isDead() || player.getGameMode() == GameMode.SPECTATOR
                    || ThreadLocalRandom.current().nextDouble(100) >= chance) continue;
            Location location = findVisibleSpot(player);
            if (location != null) show(player, location);
        }
    }

    private Location findVisibleSpot(Player player) {
        Location eye = player.getEyeLocation();
        World world = player.getWorld();
        Vector look = eye.getDirection();
        Vector horizontal = look.clone().setY(0);
        if (horizontal.lengthSquared() < 0.01) return null;
        horizontal.normalize();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < 6; attempt++) {
            Vector offset = horizontal.clone().rotateAroundY(Math.toRadians(random.nextDouble(-24, 24)));
            Location candidate = eye.clone().add(offset.multiply(random.nextDouble(9, 17)));
            if (!world.isChunkLoaded(candidate.getBlockX() >> 4, candidate.getBlockZ() >> 4)) continue;

            for (int height = 3; height >= -5; height--) {
                Location feet = candidate.getBlock().getLocation().add(0.5, height, 0.5);
                if (feet.getBlockY() <= world.getMinHeight() || feet.getBlockY() + 1 >= world.getMaxHeight()) continue;
                if (!feet.getBlock().isEmpty() || !feet.clone().add(0, 1, 0).getBlock().isEmpty()
                        || !feet.clone().subtract(0, 1, 0).getBlock().getType().isOccluding()) continue;
                Vector toFace = feet.toVector().add(new Vector(0, 1.6, 0)).subtract(eye.toVector());
                double distance = toFace.length();
                Vector direction = toFace.normalize();
                if (look.dot(direction) < 0.90
                        || world.rayTraceBlocks(eye, direction, distance, FluidCollisionMode.NEVER, true) != null) continue;
                feet.setDirection(direction.multiply(-1).setY(0));
                return feet;
            }
        }
        return null;
    }

    private void show(Player viewer, Location location) {
        Mannequin apparition = viewer.getWorld().spawn(location, Mannequin.class, entity -> {
            entity.setProfile(profile);
            entity.setDescription(null);
            entity.setVisibleByDefault(false);
            entity.setCollidable(false);
            entity.setInvulnerable(true);
            entity.setImmovable(true);
            entity.setGravity(false);
            entity.setPersistent(false);
        });
        if (!apparition.isValid()) return;
        visible.add(apparition);
        viewer.showEntity(plugin, apparition);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            apparition.remove();
            visible.remove(apparition);
        }, (long) Math.ceil(settings.get(HEROBRINE_DURATION)));
    }

    void clear() {
        visible.forEach(Mannequin::remove);
        visible.clear();
    }
}
