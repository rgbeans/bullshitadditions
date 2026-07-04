package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class TargetDummy {

    public static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "target_dummy_entity");

    private TargetDummy() {}

    public static ArmorStand spawn(Location location) {
        World world = location.getWorld();
        ArmorStand stand = world.spawn(location, ArmorStand.class);

        stand.setArms(true);
        stand.setBasePlate(false);
        stand.setGravity(false);
        stand.setInvulnerable(false);

        stand.getEquipment().setHelmet(new ItemStack(Material.TARGET), true);
        stand.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE), true);
        stand.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS), true);
        stand.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS), true);
        stand.setCanMove(false);

        stand.getPersistentDataContainer().set(KEY, PersistentDataType.BOOLEAN, true);

        return stand;
    }

    public static boolean isDummy(ArmorStand stand) {
        return stand.getPersistentDataContainer().has(KEY, PersistentDataType.BOOLEAN);
    }
}
