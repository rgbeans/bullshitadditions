package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class GameplaySettings {
    enum Setting {
        AMMO_DISPLAY("display.ammo-scoreboard", "Ammo scoreboard", Material.PAPER, true),
        GRAB_PLAYERS("gravity-gun.grab-players", "Gravity gun picks up players", Material.TOTEM_OF_UNDYING, true),
        HEROBRINE("herobrine.enabled", "Herobrine sightings", Material.PLAYER_HEAD, true),
        HEROBRINE_CHANCE("herobrine.chance-percent", "Herobrine chance per check (%)", Material.ENDER_EYE, 100, 0, 100, 0.5),
        HEROBRINE_INTERVAL("herobrine.interval-seconds", "Herobrine check interval (seconds)", Material.CLOCK, 300, 5, 600, 5),
        HEROBRINE_DURATION("herobrine.duration-ticks", "Herobrine visible time (ticks)", Material.REPEATER, 10, 1, 40, 1),
        MINECART_SPEED("minecart-speed-multiplier", "Minecart speed multiplier", Material.MINECART, 4, 1, 100, 0.5),
        EMC_DISPLAY("display.emc-tab-list", "EMC tab-list display", Material.NETHER_STAR, true);

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

    void migrate() {
        var config = plugin.getConfig();
        if (config.getInt("settings-version") >= 2) return;
        for (String path : java.util.List.of(
                "guns.dragon-damage",
                "guns.pistol.damage",
                "guns.rifle.damage",
                "guns.blicky.damage",
                "guns.pistol.cooldown-ms",
                "guns.rifle.cooldown-ms",
                "guns.blicky.cooldown-ms",
                "guns.pistol.projectile-speed",
                "guns.rifle.projectile-speed",
                "guns.blicky.projectile-speed",
                "guns.use-ammo-boxes",
                "gravity-gun.grab-blocks",
                "gravity-gun.grab-entities",
                "gravity-gun.grab-range",
                "gravity-gun.hold-distance",
                "gravity-gun.launch-speed",
                "gravity-gun.cooldown-ms",
                "heavy-stick.depth",
                "display.target-dummy-damage",
                "energy-condenser.processing",
                "transmutation.table-access",
                "display.recipe-browser")) {
            config.set(path, null);
        }
        for (String path : java.util.List.of("guns", "heavy-stick", "energy-condenser", "transmutation")) {
            var section = config.getConfigurationSection(path);
            if (section != null && section.getKeys(false).isEmpty()) config.set(path, null);
        }
        migrateDefault(Setting.HEROBRINE_CHANCE, 1);
        migrateDefault(Setting.HEROBRINE_INTERVAL, 60);
        migrateDefault(Setting.HEROBRINE_DURATION, 2);
        config.set("settings-version", 2);
        try {
            save();
        } catch (IOException e) {
            plugin.getLogger().log(java.util.logging.Level.WARNING, "Could not migrate config.yml", e);
        }
    }

    private void migrateDefault(Setting setting, double oldValue) {
        if (plugin.getConfig().getDouble(setting.path, -1) == oldValue) {
            plugin.getConfig().set(setting.path, null);
        }
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
            save();
        } catch (IOException e) {
            plugin.getConfig().set(setting.path, previous);
            throw e;
        }
    }

    private void save() throws IOException {
        Path directory = Files.createDirectories(plugin.getDataFolder().toPath());
        Path temporary = Files.createTempFile(directory, "config-", ".tmp");
        try {
            plugin.getConfig().save(temporary.toFile());
            Files.move(temporary, directory.resolve("config.yml"),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
