package com.rgbeans.bullshit.bullshitadditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecipeRegistry {

    private static final List<RecipeInfo> RECIPES = new ArrayList<>();

    private RecipeRegistry() {}

    public static void register(RecipeInfo recipe) {
        RECIPES.add(recipe);
    }

    public static List<RecipeInfo> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static int size() {
        return RECIPES.size();
    }
}
