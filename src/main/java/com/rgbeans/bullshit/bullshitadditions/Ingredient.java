package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public record Ingredient(List<Material> materials) {

    public static Ingredient of(Material mat) {
        return new Ingredient(List.of(mat));
    }

    public static Ingredient of(Material... mats) {
        return new Ingredient(List.of(mats));
    }

    Long resolve(Map<Material, Long> values) {
        Long best = null;
        for (Material mat : materials) {
            Long val = values.get(mat);
            if (val != null && (best == null || val < best)) best = val;
        }
        return best;
    }
}
