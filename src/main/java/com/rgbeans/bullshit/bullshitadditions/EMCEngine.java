package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public final class EMCEngine {

    private static Map<Material, Long> emcValues = Collections.emptyMap();
    private static File dataFolder;

    private EMCEngine() {}

    public static Map<Material, Long> recalculate(Logger logger) {
        List<Recipe> allRecipes = new ArrayList<>();
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            allRecipes.add(it.next());
        }

        int shaped = 0, shapeless = 0, cooking = 0, stonecutting = 0, smithing = 0, generic = 0, other = 0;
        for (Recipe r : allRecipes) {
            if (r instanceof ShapedRecipe) shaped++;
            else if (r instanceof ShapelessRecipe) shapeless++;
            else if (r instanceof CookingRecipe<?>) cooking++;
            else if (r instanceof StonecuttingRecipe) stonecutting++;
            else if (r instanceof SmithingTransformRecipe) smithing++;
            else if (r instanceof FurnaceRecipe) cooking++;
            else if (r instanceof BlastingRecipe) cooking++;
            else if (r instanceof SmokingRecipe) cooking++;
            else if (r instanceof CampfireRecipe) cooking++;
            else if (hasIngredients(r)) generic++;
            else other++;
        }
        logger.info("Found recipes: " + shaped + " shaped, " + shapeless + " shapeless, "
                + cooking + " cooking, " + stonecutting + " stonecutting, "
                + smithing + " smithing" + (generic > 0 ? ", " + generic + " generic" : "")
                + (other > 0 ? ", " + other + " other" : ""));

        Map<Material, Long> values = new LinkedHashMap<>(EMCBaseValues.BASE_VALUES);
        int baseCount = values.size();

        boolean changed = true;
        int passes = 0;
        while (changed && passes < 50) {
            changed = false;
            passes++;
            for (Recipe recipe : allRecipes) {
                if (recipe instanceof ShapedRecipe r) {
                    changed |= tryAssignShaped(values, r);
                } else if (recipe instanceof ShapelessRecipe r) {
                    changed |= tryAssignShapeless(values, r);
                } else if (recipe instanceof CookingRecipe<?> r) {
                    changed |= tryAssignCooking(values, r);
                } else if (recipe instanceof StonecuttingRecipe r) {
                    changed |= tryAssignStonecutting(values, r);
                } else if (recipe instanceof SmithingTransformRecipe r) {
                    changed |= tryAssignSmithing(values, r);
                } else {
                    changed |= tryAssignGeneric(values, recipe);
                }
            }
        }

        emcValues = Collections.unmodifiableMap(values);
        int autoMapped = values.size() - baseCount;
        logger.info("EMC-V2: " + values.size() + " items mapped (" + baseCount + " base, "
                + autoMapped + " from recipes) in " + passes + " passes.");
        return emcValues;
    }

    static Map<Material, Long> recalculate(Logger logger, List<InternalRecipe> recipes) {
        logger.info("Calculating EMC values from " + recipes.size() + " recipes...");
        Map<Material, Long> values = new LinkedHashMap<>(EMCBaseValues.BASE_VALUES);

        boolean changed = true;
        int passes = 0;
        while (changed && passes < 50) {
            changed = false;
            passes++;
            for (InternalRecipe recipe : recipes) {
                changed |= tryAssignInternal(values, recipe);
            }
        }

        emcValues = Collections.unmodifiableMap(values);
        logger.info("EMC: " + emcValues.size() + " items mapped in " + passes + " passes.");
        return emcValues;
    }

    private static boolean tryAssignShaped(Map<Material, Long> values, ShapedRecipe recipe) {
        long sum = 0;
        String[] shape = recipe.getShape();
        Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
        Map<Character, RecipeChoice> choices = recipe.getChoiceMap();

        for (String row : shape) {
            for (int col = 0; col < row.length(); col++) {
                char key = row.charAt(col);
                if (key == ' ') continue;
                Long val = null;

                ItemStack ing = ingredients.get(key);
                if (ing != null && ing.getType() != Material.AIR) {
                    val = values.get(ing.getType());
                }
                if (val == null) {
                    RecipeChoice choice = choices.get(key);
                    if (choice != null) {
                        val = resolveChoice(values, choice);
                    }
                }
                if (val == null) {
                    // Empty slot: ingredient is null or AIR with no valid choice mapping
                    if (ing == null || ing.getType() == Material.AIR) continue;
                    return false;
                }
                sum += val;
            }
        }
        return assignIfCheaper(values, recipe.getResult(), sum);
    }

    private static boolean tryAssignShapeless(Map<Material, Long> values, ShapelessRecipe recipe) {
        long sum = 0;
        List<ItemStack> ingredients = recipe.getIngredientList();
        List<RecipeChoice> choices = recipe.getChoiceList();

        for (int i = 0; i < ingredients.size(); i++) {
            Long val = null;

            ItemStack ing = ingredients.get(i);
            if (ing != null && ing.getType() != Material.AIR) {
                val = values.get(ing.getType());
            }
            if (val == null) {
                RecipeChoice choice = i < choices.size() ? choices.get(i) : null;
                if (choice != null) {
                    val = resolveChoice(values, choice);
                }
            }
            if (val == null) {
                if (ing == null || ing.getType() == Material.AIR) continue;
                return false;
            }
            sum += val;
        }
        return assignIfCheaper(values, recipe.getResult(), sum);
    }

    private static boolean tryAssignCooking(Map<Material, Long> values, CookingRecipe<?> recipe) {
        Long inputVal = resolveChoice(values, recipe.getInputChoice());
        if (inputVal == null) return false;
        long fuelCost = values.getOrDefault(Material.COAL, 32L);
        return assignIfCheaper(values, recipe.getResult(), inputVal + fuelCost);
    }

    private static boolean tryAssignStonecutting(Map<Material, Long> values, StonecuttingRecipe recipe) {
        Long inputVal = resolveChoice(values, recipe.getInputChoice());
        if (inputVal == null) return false;
        return assignIfCheaper(values, recipe.getResult(), inputVal);
    }

    private static boolean tryAssignSmithing(Map<Material, Long> values, SmithingTransformRecipe recipe) {
        Long templateVal = resolveChoice(values, recipe.getTemplate());
        Long baseVal = resolveChoice(values, recipe.getBase());
        Long additionVal = resolveChoice(values, recipe.getAddition());
        if (templateVal == null || baseVal == null || additionVal == null) return false;
        return assignIfCheaper(values, recipe.getResult(), templateVal + baseVal + additionVal);
    }

    private static boolean hasIngredients(Recipe recipe) {
        try {
            var cls = recipe.getClass();
            cls.getMethod("getIngredientList");
            return true;
        } catch (NoSuchMethodException e1) {
            try {
                var cls = recipe.getClass();
                cls.getMethod("getChoiceList");
                return true;
            } catch (NoSuchMethodException e2) {
                return false;
            }
        }
    }

    private static boolean tryAssignGeneric(Map<Material, Long> values, Recipe recipe) {
        ItemStack result = recipe.getResult();
        if (result == null || hasCustomMeta(result)) return false;

        try {
            var cls = recipe.getClass();
            java.lang.reflect.Method choiceListMethod = cls.getMethod("getChoiceList");
            @SuppressWarnings("unchecked")
            List<RecipeChoice> choiceList = (List<RecipeChoice>) choiceListMethod.invoke(recipe);
            if (choiceList != null) {
                long sum = 0;
                for (RecipeChoice choice : choiceList) {
                    Long val = resolveChoice(values, choice);
                    if (val == null) return false;
                    sum += val;
                }
                return assignIfCheaper(values, result, sum);
            }
        } catch (Exception ignored) {}

        try {
            var cls = recipe.getClass();
            java.lang.reflect.Method ingListMethod = cls.getMethod("getIngredientList");
            @SuppressWarnings("unchecked")
            List<ItemStack> ingList = (List<ItemStack>) ingListMethod.invoke(recipe);
            if (ingList != null) {
                long sum = 0;
                List<RecipeChoice> choiceList = null;
                try {
                    var clMethod = cls.getMethod("getChoiceList");
                    choiceList = (List<RecipeChoice>) clMethod.invoke(recipe);
                } catch (Exception ignored2) {}
                for (int i = 0; i < ingList.size(); i++) {
                    Long val = null;
                    ItemStack ing = ingList.get(i);
                    if (ing != null && ing.getType() != Material.AIR) {
                        val = values.get(ing.getType());
                    }
                    if (val == null && choiceList != null && i < choiceList.size()) {
                        val = resolveChoice(values, choiceList.get(i));
                    }
                    if (val == null) {
                        if (ing == null || ing.getType() == Material.AIR) continue;
                        return false;
                    }
                    sum += val;
                }
                return assignIfCheaper(values, result, sum);
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static boolean tryAssignInternal(Map<Material, Long> values, InternalRecipe recipe) {
        long sum = 0;

        switch (recipe) {
            case InternalRecipe.Shaped r -> {
                for (String row : r.shape()) {
                    for (int col = 0; col < row.length(); col++) {
                        char key = row.charAt(col);
                        if (key == ' ') continue;
                        Ingredient ing = r.ingredients().get(key);
                        if (ing == null) return false;
                        Long val = ing.resolve(values);
                        if (val == null) return false;
                        sum += val;
                    }
                }
            }
            case InternalRecipe.Shapeless r -> {
                for (Ingredient ing : r.ingredients()) {
                    Long val = ing.resolve(values);
                    if (val == null) return false;
                    sum += val;
                }
            }
            case InternalRecipe.Cooking r -> {
                Long inputVal = r.input().resolve(values);
                if (inputVal == null) return false;
                sum += inputVal + values.getOrDefault(Material.COAL, 32L);
            }
            case InternalRecipe.Stonecutting r -> {
                Long inputVal = r.input().resolve(values);
                if (inputVal == null) return false;
                sum += inputVal;
            }
            case InternalRecipe.Smithing r -> {
                Long templateVal = r.template().resolve(values);
                Long baseVal = r.base().resolve(values);
                Long additionVal = r.addition().resolve(values);
                if (templateVal == null || baseVal == null || additionVal == null) return false;
                sum += templateVal + baseVal + additionVal;
            }
        }

        Material mat = recipe.resultType();
        if (EMCBaseValues.BASE_VALUES.containsKey(mat)) return false;
        int amount = recipe.resultAmount();
        long perItem = (sum + amount - 1) / amount;
        if (perItem <= 0) return false;
        Long existing = values.get(mat);
        if (existing == null || existing > perItem) {
            values.put(mat, perItem);
            return true;
        }
        return false;
    }

    private static boolean assignIfCheaper(Map<Material, Long> values, ItemStack result, long totalInput) {
        if (totalInput <= 0) return false;
        Material mat = result.getType();
        if (EMCBaseValues.BASE_VALUES.containsKey(mat)) return false;
        if (hasCustomMeta(result)) return false;
        long perItem = (totalInput + result.getAmount() - 1) / result.getAmount();
        Long existing = values.get(mat);
        if (existing == null || existing > perItem) {
            values.put(mat, perItem);
            return true;
        }
        return false;
    }

    private static Long resolveChoice(Map<Material, Long> values, RecipeChoice choice) {
        if (choice == null) return null;
        if (choice instanceof RecipeChoice.MaterialChoice mc) {
            Long best = null;
            for (Material mat : mc.getChoices()) {
                Long val = values.get(mat);
                if (val != null && (best == null || val < best)) best = val;
            }
            if (best != null) return best;
        }
        if (choice instanceof RecipeChoice.ExactChoice ec) {
            Long best = null;
            for (ItemStack item : ec.getChoices()) {
                Long val = values.get(item.getType());
                if (val != null && (best == null || val < best)) best = val;
            }
            if (best != null) return best;
        }
        ItemStack representative = choice.getItemStack();
        if (representative != null && !representative.getType().isEmpty()) {
            return values.get(representative.getType());
        }
        return null;
    }

    private static boolean hasCustomMeta(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() || meta.hasLore() || !meta.getPersistentDataContainer().isEmpty();
    }

    public static Long get(Material material) {
        return emcValues.get(material);
    }

    public static boolean has(Material material) {
        return emcValues.containsKey(material);
    }

    public static Map<Material, Long> getAll() {
        return emcValues;
    }

    public static int size() {
        return emcValues.size();
    }

    public static String formatEmc(double emc) {
        if (emc < 1_000_000_000L) {
            return String.format("%,.0f", emc);
        }
        int exponent = 0;
        double mantissa = emc;
        while (mantissa >= 10.0) {
            mantissa /= 10.0;
            exponent++;
        }
        return String.format("%.1fe%d", mantissa, exponent);
    }

    public static String formatEmc(long emc) {
        return formatEmc((double) emc);
    }

    public static void addBaseValue(Material material, long value) {
        EMCBaseValues.BASE_VALUES.put(material, value);
        logSetValue(material, value);
    }

    public static String traceRecipes(Material target) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Recipe trace for ").append(target.name()).append(" ===\n");
        sb.append("Has EMC: ").append(has(target) ? emcValues.get(target) : "NO").append("\n");
        sb.append("Is base value: ").append(EMCBaseValues.BASE_VALUES.containsKey(target) ? "YES (" + EMCBaseValues.BASE_VALUES.get(target) + ")" : "NO").append("\n\n");

        List<Recipe> matchingRecipes = new ArrayList<>();
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe r = it.next();
            if (r.getResult().getType() == target) {
                matchingRecipes.add(r);
            }
        }

        sb.append("Recipes producing this item: ").append(matchingRecipes.size()).append("\n\n");

        for (int i = 0; i < matchingRecipes.size(); i++) {
            Recipe r = matchingRecipes.get(i);
            sb.append("--- Recipe ").append(i + 1).append(" ---\n");
            sb.append("Type: ").append(r.getClass().getSimpleName()).append("\n");
            sb.append("Result amount: ").append(r.getResult().getAmount()).append("\n");

            if (r instanceof ShapedRecipe sr) {
                sb.append("Shape:\n");
                for (String row : sr.getShape()) sb.append("  ").append(row).append("\n");
                sb.append("Ingredients:\n");
                Map<Character, ItemStack> im = sr.getIngredientMap();
                Map<Character, RecipeChoice> cm = sr.getChoiceMap();
                for (char key : im.keySet()) {
                    ItemStack item = im.get(key);
                    RecipeChoice choice = cm.get(key);
                    String type = (item == null || item.getType() == Material.AIR) ? "AIR (null)" : item.getType().name();
                    boolean ingHasEmc = item != null && item.getType() != Material.AIR && has(item.getType());
                    String choiceInfo = "";
                    if (choice != null && choice instanceof RecipeChoice.MaterialChoice mc) {
                        choiceInfo = " tag=" + mc.getChoices().stream().map(Enum::name).reduce((a, b) -> a + "," + b).orElse("?");
                    }
                    sb.append("  '").append(key).append("' = ").append(type)
                            .append(ingHasEmc ? " EMC=" + get(item.getType()) : " (no EMC)")
                            .append(choiceInfo).append("\n");
                }
            } else if (r instanceof ShapelessRecipe sr) {
                List<ItemStack> il = sr.getIngredientList();
                List<RecipeChoice> cl = sr.getChoiceList();
                sb.append("Ingredients (").append(il.size()).append("):\n");
                for (int j = 0; j < il.size(); j++) {
                    ItemStack item = il.get(j);
                    String type = item != null ? item.getType().name() : "?";
                    String hasEmc = (item != null && has(item.getType())) ? " EMC=" + get(item.getType()) : " (no EMC)";
                    sb.append("  ").append(type).append(hasEmc).append("\n");
                }
            } else if (r instanceof CookingRecipe<?> cr) {
                RecipeChoice input = cr.getInputChoice();
                sb.append("Input: ");
                sb.append(input != null ? input.toString() : "null").append("\n");
            }
        }
        return sb.toString();
    }

    public static String traceMissingRoots() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Missing Items — Recipe Root Analysis ===\n\n");

        Set<Material> missing = new LinkedHashSet<>();
        for (Material mat : Material.values()) {
            if (!mat.isItem()) continue;
            String name = mat.name();
            if (name.contains("SPAWN_EGG") || name.contains("POTTERY_SHERD") || name.contains("BANNER_PATTERN")) continue;
            if (!has(mat) && !EMCBaseValues.BASE_VALUES.containsKey(mat)) {
                missing.add(mat);
            }
        }

        // Build recipe index: result material → list of recipes that produce it
        Map<Material, List<Recipe>> recipeIndex = new LinkedHashMap<>();
        for (Iterator<Recipe> it = Bukkit.recipeIterator(); it.hasNext(); ) {
            Recipe r = it.next();
            recipeIndex.computeIfAbsent(r.getResult().getType(), k -> new ArrayList<>()).add(r);
        }

        Set<Material> itemsWithRecipe = new LinkedHashSet<>();
        Set<Material> itemsWithoutRecipe = new LinkedHashSet<>();

        for (Material mat : missing) {
            if (recipeIndex.containsKey(mat)) {
                itemsWithRecipe.add(mat);
            } else {
                itemsWithoutRecipe.add(mat);
            }
        }

        sb.append("Total missing: ").append(missing.size()).append("\n");
        sb.append("Missing items WITH crafting recipes: ").append(itemsWithRecipe.size()).append("\n");
        sb.append("Missing items WITHOUT any recipe (prime roots): ").append(itemsWithoutRecipe.size()).append("\n\n");

        if (!itemsWithoutRecipe.isEmpty()) {
            sb.append("=== PRIME ROOTS (no recipe, need base EMC) ===\n");
            for (Material mat : itemsWithoutRecipe) {
                sb.append(mat.name()).append("\n");
            }
            sb.append("\n");
        }

        if (!itemsWithRecipe.isEmpty()) {
            sb.append("=== MISSING BUT HAVE RECIPES (blocked by missing ingredients) ===\n");
            for (Material mat : itemsWithRecipe) {
                List<Recipe> recipes = recipeIndex.get(mat);
                Recipe first = recipes.get(0);
                sb.append(mat.name()).append(" (").append(recipes.size()).append(" recipe(s), type=")
                        .append(first.getClass().getSimpleName()).append(")\n");
            }
        }

        return sb.toString();
    }

    public static void init(File pluginDataFolder) {
        dataFolder = pluginDataFolder;
    }

    private static void logSetValue(Material material, long value) {
        if (dataFolder == null) return;
        String line = "    BASE_VALUES.put(Material." + material.name() + ", " + value + "L);\n";
        File file = new File(dataFolder, "emc_set_log.txt");
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file, true)) {
            fw.write(line);
        } catch (IOException ignored) {}
    }
}