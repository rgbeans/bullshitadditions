package com.rgbeans.bullshit.bullshitadditions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rgbeans.bullshit.bullshitadditions.GameplaySettings.Setting;

final class ConfigMenu implements Listener {
    static final String PERMISSION = "bullshitadditions.config";
    private static final Setting[] SETTINGS = Setting.values();
    private static final DecimalFormat NUMBER = new DecimalFormat("0.##");

    private final GameplaySettings settings;
    private final Consumer<Setting> changed;
    private final Logger logger;

    ConfigMenu(GameplaySettings settings, Consumer<Setting> changed, Logger logger) {
        this.settings = settings;
        this.changed = changed;
        this.logger = logger;
    }

    void open(Player player) {
        Inventory inventory = new MenuInventory().getInventory();
        refresh(inventory);
        player.openInventory(inventory);
    }

    private void refresh(Inventory inventory) {
        for (int slot = 0; slot < SETTINGS.length; slot++) {
            Setting setting = SETTINGS[slot];
            ItemStack icon = new ItemStack(setting.icon);
            var meta = icon.getItemMeta();
            meta.displayName(Component.text(setting.label, NamedTextColor.GOLD));
            double value = settings.get(setting);
            String text = setting.toggle() ? (value == 0 ? "Off" : "On") : NUMBER.format(value);
            meta.lore(List.of(
                    Component.text("Current: " + text, NamedTextColor.WHITE),
                    Component.text(setting.toggle() ? "Click to toggle" : "Left + / Right − / Shift ×5", NamedTextColor.GRAY),
                    Component.text("Press Q to reset the default", NamedTextColor.GRAY)));
            icon.setItemMeta(meta);
            inventory.setItem(slot, icon);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Inventory inventory = event.getView().getTopInventory();
        if (!(inventory.getHolder() instanceof MenuInventory)) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player) || !player.hasPermission(PERMISSION)) return;
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= SETTINGS.length) return;
        Setting setting = SETTINGS[slot];
        double value = settings.get(setting);
        ClickType click = event.getClick();
        switch (click) {
            case DROP, MIDDLE -> value = setting.defaultValue;
            case LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT -> {
                if (setting.toggle()) {
                    value = value == 0 ? 1 : 0;
                } else {
                    double delta = setting.step * (click.isShiftClick() ? 5 : 1);
                    value += click.isLeftClick() ? delta : -delta;
                    value = Math.clamp(value, setting.min, setting.max);
                }
            }
            default -> { return; }
        }

        try {
            settings.set(setting, value);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save " + setting.path, e);
            player.sendMessage(Component.text("Could not save the setting. Check the server log.", NamedTextColor.RED));
            refresh(inventory);
            return;
        }
        changed.accept(setting);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            Inventory open = viewer.getOpenInventory().getTopInventory();
            if (open.getHolder() instanceof MenuInventory) refresh(open);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof MenuInventory) event.setCancelled(true);
    }

    private static final class MenuInventory implements InventoryHolder {
        private final Inventory inventory = Bukkit.createInventory(this, 36, Component.text("Bullshit Additions Config"));

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}
