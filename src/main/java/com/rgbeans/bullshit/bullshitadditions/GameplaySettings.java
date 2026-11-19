package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class GameplaySettings {
    enum Setting {
        DRAGON_DAMAGE("guns.dragon-damage", "Guns damage the Ender Dragon", Material.DRAGON_HEAD, true),
        PISTOL_DAMAGE("guns.pistol.damage", "Pistol damage", Material.IRON_NUGGET, Pistol.DAMAGE, 0, 100, 1),
        RIFLE_DAMAGE("guns.rifle.damage", "Rifle damage", Material.GOLD_NUGGET, Rifle.DAMAGE, 0, 100, 1),
        BLICKY_DAMAGE("guns.blicky.damage", "My Blicky damage", Material.GOLD_INGOT, MyBlicky.DAMAGE, 0, 100, 1),
        PISTOL_COOLDOWN("guns.pistol.cooldown-ms", "Pistol cooldown (ms)", Material.STONE_HOE, Pistol.COOLDOWN_MILLIS, 0, 5000, 50),
        RIFLE_COOLDOWN("guns.rifle.cooldown-ms", "Rifle cooldown (ms)", Material.IRON_HOE, Rifle.COOLDOWN_MILLIS, 0, 5000, 50),
        BLICKY_COOLDOWN("guns.blicky.cooldown-ms", "My Blicky cooldown (ms)", Material.DIAMOND_HOE, MyBlicky.COOLDOWN_MILLIS, 0, 5000, 50),
        PISTOL_SPEED("guns.pistol.projectile-speed", "Pistol projectile speed", Material.ARROW, Pistol.PROJECTILE_SPEED, 1, 10, 0.5),
        RIFLE_SPEED("guns.rifle.projectile-speed", "Rifle projectile speed", Material.ARROW, Rifle.PROJECTILE_SPEED, 1, 10, 0.5),
        BLICKY_SPEED("guns.blicky.projectile-speed", "My Blicky projectile speed", Material.SPECTRAL_ARROW, MyBlicky.PROJECTILE_SPEED, 1, 10, 0.5),
        AMMO_BOX("guns.use-ammo-boxes", "Guns draw ammo from boxes", Material.ENDER_CHEST, true),
        AMMO_DISPLAY("display.ammo-scoreboard", "Ammo scoreboard", Material.PAPER, true),
        GRAB_BLOCKS("gravity-gun.grab-blocks", "Gravity gun picks up blocks", Material.STONE, true),
        GRAB_ENTITIES("gravity-gun.grab-entities", "Gravity gun picks up entities", Material.LEAD, true),
        GRAB_PLAYERS("gravity-gun.grab-players", "Gravity gun picks up players", Material.TOTEM_OF_UNDYING, true),
        GRAB_RANGE("gravity-gun.grab-range", "Gravity gun reach (blocks)", Material.SPYGLASS, GravityGun.GRAB_RANGE, 2, 20, 1),
        HOLD_DISTANCE("gravity-gun.hold-distance", "Gravity gun hold distance", Material.IRON_BARS, GravityGun.HOLD_DISTANCE, 2, 8, 0.5),
        LAUNCH_SPEED("gravity-gun.launch-speed", "Gravity gun launch speed", Material.WIND_CHARGE, GravityGun.SHOOT_SPEED, 0.5, 8, 0.5),
        GRAB_COOLDOWN("gravity-gun.cooldown-ms", "Gravity gun cooldown (ms)", Material.GLOWSTONE_DUST, 200, 0, 5000, 50),
        HEROBRINE("herobrine.enabled", "Herobrine sightings", Material.PLAYER_HEAD, true),
        HEROBRINE_CHANCE("herobrine.chance-percent", "Herobrine chance per check (%)", Material.ENDER_EYE, 1, 0, 100, 0.5),
        HEROBRINE_INTERVAL("herobrine.interval-seconds", "Herobrine check interval (seconds)", Material.CLOCK, 60, 5, 600, 5),
        HEROBRINE_DURATION("herobrine.duration-ticks", "Herobrine visible time (ticks)", Material.REPEATER, 2, 1, 40, 1),
        MINECART_SPEED("minecart-speed-multiplier", "Minecart speed multiplier", Material.MINECART, 4, 1, 100, 0.5),
        STICK_DEPTH("heavy-stick.depth", "Heavy Stick depth (blocks)", Material.STICK, 2, 0, 8, 1),
        DUMMY_DAMAGE("display.target-dummy-damage", "Target dummy damage readout", Material.TARGET, true),
        CONDENSER("energy-condenser.processing", "Energy Condenser processing", Material.BARREL, true),
        EMC_DISPLAY("display.emc-tab-list", "EMC tab-list display", Material.NETHER_STAR, true),
        TRANSMUTATION("transmutation.table-access", "Transmutation Table access", Material.POLISHED_BLACKSTONE_SLAB, true),
        RECIPE_GUI("display.recipe-browser", "Recipe browser", Material.KNOWLEDGE_BOOK, true);

        final String path;
        final String label;
        final Material icon;
        final double defaultValue;
        final double min;
        final double max;
        final double step;

        Setting(String path, String label, Material icon, boolean defaultValue) {
            this(path, label, icon, defaultValue ? 1 : 0, 0, 1, 0);
        }

        Setting(String path, String label, Material icon, double defaultValue, double min, double max, double step) {
            this.path = path;
            this.label = label;
            this.icon = icon;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        boolean toggle() {
            return step == 0;
        }
    }

    private final JavaPlugin plugin;

    GameplaySettings(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    double get(Setting setting) {
        if (setting.toggle()) {
            return plugin.getConfig().getBoolean(setting.path, setting.defaultValue != 0) ? 1 : 0;
        }
        double value = plugin.getConfig().getDouble(setting.path, setting.defaultValue);
        return Double.isFinite(value) && value >= setting.min && value <= setting.max ? value : setting.defaultValue;
    }

    boolean enabled(Setting setting) {
        return get(setting) != 0;
    }

    void set(Setting setting, double value) throws IOException {
        Object previous = plugin.getConfig().get(setting.path);
        if (setting.toggle()) {
            plugin.getConfig().set(setting.path, value != 0);
        } else {
            plugin.getConfig().set(setting.path, value);
        }
        try {
            Path directory = Files.createDirectories(plugin.getDataFolder().toPath());
            Path temporary = Files.createTempFile(directory, "config-", ".tmp");
            try {
                plugin.getConfig().save(temporary.toFile());
                Files.move(temporary, directory.resolve("config.yml"),
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException e) {
            plugin.getConfig().set(setting.path, previous);
            throw e;
        }
    }
}
