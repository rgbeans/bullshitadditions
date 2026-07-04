package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.inventory.ItemStack;

public record RecipeInfo(String displayName, ItemStack result, ItemStack[] ingredients) {}
