package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public final class PlayerDataManager {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerData> cache = new HashMap<>();

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerData get(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::load);
    }

    public void save(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;

        File file = new File(plugin.getDataFolder(), "playerdata" + File.separator + uuid + ".yml");
        file.getParentFile().mkdirs();
        FileConfiguration config = new YamlConfiguration();
        config.set("emc", data.emc());

        List<String> learned = new ArrayList<>();
        for (Material mat : data.learned()) {
            learned.add(mat.name());
        }
        config.set("learned", learned);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save data for " + uuid, e);
        }
    }

    public void saveAll() {
        for (UUID uuid : cache.keySet()) {
            save(uuid);
        }
    }

    public void unload(UUID uuid) {
        save(uuid);
        cache.remove(uuid);
    }

    private PlayerData load(UUID uuid) {
        File file = new File(plugin.getDataFolder(), "playerdata" + File.separator + uuid + ".yml");
        if (!file.exists()) return new PlayerData(0);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        double emc = config.getDouble("emc", 0);
        if (Double.isNaN(emc) || Double.isInfinite(emc)) {
            plugin.getLogger().warning("Corrupted EMC (" + emc + ") for " + uuid + " — resetting to 0.");
            emc = 0;
        }
        PlayerData data = new PlayerData(emc);

        for (String name : config.getStringList("learned")) {
            Material mat = Material.getMaterial(name);
            if (mat != null) data.learn(mat);
        }

        return data;
    }
}
