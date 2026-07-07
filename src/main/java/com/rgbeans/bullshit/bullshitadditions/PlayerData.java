package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class PlayerData {

    private double emc;
    private final Set<Material> learned = new HashSet<>();

    public PlayerData(double emc) {
        this.emc = emc;
    }

    public double emc() {
        return emc;
    }

    public void addEmc(double amount) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) return;
        this.emc += amount;
        if (Double.isNaN(this.emc) || Double.isInfinite(this.emc)) this.emc = 0;
    }

    public boolean spendEmc(double amount) {
        if (emc < amount) return false;
        emc -= amount;
        return true;
    }

    public Set<Material> learned() {
        return learned;
    }

    public void learn(Material mat) {
        learned.add(mat);
    }

    public boolean knows(Material mat) {
        return learned.contains(mat);
    }
}
