package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public sealed interface InternalRecipe {

    Material resultType();
    int resultAmount();

    record Shaped(Material resultType, int resultAmount, String[] shape,
                  Map<Character, Ingredient> ingredients) implements InternalRecipe {}

    record Shapeless(Material resultType, int resultAmount,
                     List<Ingredient> ingredients) implements InternalRecipe {}

    record Cooking(Material resultType, int resultAmount, Ingredient input) implements InternalRecipe {}

    record Stonecutting(Material resultType, int resultAmount, Ingredient input) implements InternalRecipe {}

    record Smithing(Material resultType, int resultAmount,
                    Ingredient template, Ingredient base, Ingredient addition) implements InternalRecipe {}
}
