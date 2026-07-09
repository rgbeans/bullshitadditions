package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public final class MinecartListener implements Listener {

    static final double VANILLA_MAX_SPEED = 0.4;

    private double maxSpeed;

    MinecartListener(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    double getMaxSpeed() {
        return maxSpeed;
    }

    void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    int applyToLoaded(Server server) {
        int count = 0;
        for (World world : server.getWorlds()) {
            for (Minecart cart : world.getEntitiesByClass(Minecart.class)) {
                cart.setMaxSpeed(maxSpeed);
                count++;
            }
        }
        return count;
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (event.getVehicle() instanceof Minecart cart) {
            cart.setMaxSpeed(maxSpeed);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle() instanceof Minecart cart) {
            cart.setMaxSpeed(maxSpeed);
        }
    }
}
