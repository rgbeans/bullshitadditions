package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class EMCEngineTest {

    private static Map<Material, Long> emcValues;

    static {
        List<InternalRecipe> recipes = new ArrayList<>();

        // === SHAPED RECIPES ===
        recipes.add(shaped(Material.OAK_PLANKS, 4, new String[]{"L"},
                Map.of('L', Ingredient.of(Material.OAK_LOG))));
        recipes.add(shaped(Material.SPRUCE_PLANKS, 4, new String[]{"L"},
                Map.of('L', Ingredient.of(Material.SPRUCE_LOG))));
        recipes.add(shaped(Material.BIRCH_PLANKS, 4, new String[]{"L"},
                Map.of('L', Ingredient.of(Material.BIRCH_LOG))));

        recipes.add(shaped(Material.STICK, 4, new String[]{"P", "P"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.CRAFTING_TABLE, 1, new String[]{"PP", "PP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.CHEST, 1, new String[]{"PPP", "P P", "PPP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.OAK_BOAT, 1, new String[]{"P P", "PPP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.BOWL, 1, new String[]{"P P", " P "},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.BUCKET, 1, new String[]{"I I", " I "},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));
        recipes.add(shaped(Material.FURNACE, 1, new String[]{"CCC", "C C", "CCC"},
                Map.of('C', Ingredient.of(Material.COBBLESTONE))));
        recipes.add(shaped(Material.IRON_CHESTPLATE, 1, new String[]{"I I", "III", "III"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));
        recipes.add(shaped(Material.IRON_PICKAXE, 1, new String[]{"III", " S ", " S "},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.IRON_SWORD, 1, new String[]{"I", "I", "S"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.IRON_AXE, 1, new String[]{"II", "IS", " S"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.IRON_SHOVEL, 1, new String[]{"I", "S", "S"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.IRON_HOE, 1, new String[]{"II", " S", " S"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.IRON_HELMET, 1, new String[]{"III", "I I"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));
        recipes.add(shaped(Material.IRON_LEGGINGS, 1, new String[]{"III", "I I", "I I"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));
        recipes.add(shaped(Material.IRON_BOOTS, 1, new String[]{"I I", "I I"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));

        recipes.add(shaped(Material.DIAMOND_CHESTPLATE, 1, new String[]{"D D", "DDD", "DDD"},
                Map.of('D', Ingredient.of(Material.DIAMOND))));
        recipes.add(shaped(Material.DIAMOND_PICKAXE, 1, new String[]{"DDD", " S ", " S "},
                Map.of('D', Ingredient.of(Material.DIAMOND), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.DIAMOND_SWORD, 1, new String[]{"D", "D", "S"},
                Map.of('D', Ingredient.of(Material.DIAMOND), 'S', Ingredient.of(Material.STICK))));

        recipes.add(shaped(Material.GOLDEN_CHESTPLATE, 1, new String[]{"G G", "GGG", "GGG"},
                Map.of('G', Ingredient.of(Material.GOLD_INGOT))));
        recipes.add(shaped(Material.GOLDEN_PICKAXE, 1, new String[]{"GGG", " S ", " S "},
                Map.of('G', Ingredient.of(Material.GOLD_INGOT), 'S', Ingredient.of(Material.STICK))));

        recipes.add(shaped(Material.CLOCK, 1, new String[]{" G ", "GRG", " G "},
                Map.of('G', Ingredient.of(Material.GOLD_INGOT), 'R', Ingredient.of(Material.REDSTONE))));
        recipes.add(shaped(Material.COMPASS, 1, new String[]{" I ", "IRI", " I "},
                Map.of('I', Ingredient.of(Material.IRON_INGOT), 'R', Ingredient.of(Material.REDSTONE))));
        recipes.add(shaped(Material.LADDER, 3, new String[]{"S S", "SSS", "S S"},
                Map.of('S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.OAK_SIGN, 3, new String[]{"PPP", "PPP", " S "},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.OAK_DOOR, 3, new String[]{"PP", "PP", "PP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.OAK_TRAPDOOR, 2, new String[]{"PPP", "PPP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS))));
        recipes.add(shaped(Material.OAK_FENCE, 3, new String[]{"PSP", "PSP"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.OAK_FENCE_GATE, 1, new String[]{"SPS", "SPS"},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.STONE_PICKAXE, 1, new String[]{"CCC", " S ", " S "},
                Map.of('C', Ingredient.of(Material.COBBLESTONE), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.STONE_SWORD, 1, new String[]{"C", "C", "S"},
                Map.of('C', Ingredient.of(Material.COBBLESTONE), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.WOODEN_PICKAXE, 1, new String[]{"PPP", " S ", " S "},
                Map.of('P', Ingredient.of(Material.OAK_PLANKS), 'S', Ingredient.of(Material.STICK))));
        recipes.add(shaped(Material.CLAY, 1, new String[]{"CC", "CC"},
                Map.of('C', Ingredient.of(Material.CLAY_BALL))));
        recipes.add(shaped(Material.SNOW_BLOCK, 1, new String[]{"SS", "SS"},
                Map.of('S', Ingredient.of(Material.SNOWBALL))));
        recipes.add(shaped(Material.GLOWSTONE, 1, new String[]{"GG", "GG"},
                Map.of('G', Ingredient.of(Material.GLOWSTONE_DUST))));
        recipes.add(shaped(Material.QUARTZ_BLOCK, 1, new String[]{"QQ", "QQ"},
                Map.of('Q', Ingredient.of(Material.QUARTZ))));
        recipes.add(shaped(Material.PRISMARINE, 1, new String[]{"PP", "PP"},
                Map.of('P', Ingredient.of(Material.PRISMARINE_SHARD))));
        recipes.add(shaped(Material.IRON_BARS, 16, new String[]{"III", "III"},
                Map.of('I', Ingredient.of(Material.IRON_INGOT))));

        // === SHAPELESS RECIPES ===
        recipes.add(shapeless(Material.IRON_BLOCK, 1, Ingredient.of(Material.IRON_INGOT), 9));
        recipes.add(shapeless(Material.GOLD_BLOCK, 1, Ingredient.of(Material.GOLD_INGOT), 9));
        recipes.add(shapeless(Material.DIAMOND_BLOCK, 1, Ingredient.of(Material.DIAMOND), 9));
        recipes.add(shapeless(Material.EMERALD_BLOCK, 1, Ingredient.of(Material.EMERALD), 9));
        recipes.add(shapeless(Material.COAL_BLOCK, 1, Ingredient.of(Material.COAL), 9));
        recipes.add(shapeless(Material.COPPER_BLOCK, 1, Ingredient.of(Material.COPPER_INGOT), 9));
        recipes.add(shapeless(Material.REDSTONE_BLOCK, 1, Ingredient.of(Material.REDSTONE), 9));
        recipes.add(shapeless(Material.LAPIS_BLOCK, 1, Ingredient.of(Material.LAPIS_LAZULI), 9));
        recipes.add(shapeless(Material.IRON_NUGGET, 9, Ingredient.of(Material.IRON_INGOT), 1));
        recipes.add(shapeless(Material.GOLD_NUGGET, 9, Ingredient.of(Material.GOLD_INGOT), 1));
        recipes.add(shapeless(Material.PRISMARINE_BRICKS, 1, Ingredient.of(Material.PRISMARINE_SHARD), 9));

        // Netherite ingot
        recipes.add(new InternalRecipe.Shapeless(Material.NETHERITE_INGOT, 1,
                concat(Ingredient.of(Material.NETHERITE_SCRAP), 4, Ingredient.of(Material.GOLD_INGOT), 4)));
        // Netherite block
        recipes.add(shapeless(Material.NETHERITE_BLOCK, 1, Ingredient.of(Material.NETHERITE_INGOT), 9));
        // Sea lantern
        recipes.add(new InternalRecipe.Shapeless(Material.SEA_LANTERN, 1,
                concat(Ingredient.of(Material.PRISMARINE_SHARD), 4, Ingredient.of(Material.PRISMARINE_CRYSTALS), 5)));

        // === COOKING RECIPES ===
        recipes.add(new InternalRecipe.Cooking(Material.GLASS, 1, Ingredient.of(Material.SAND)));
        recipes.add(new InternalRecipe.Cooking(Material.BRICK, 1, Ingredient.of(Material.CLAY_BALL)));
        recipes.add(new InternalRecipe.Cooking(Material.NETHER_BRICK, 1, Ingredient.of(Material.NETHERRACK)));
        recipes.add(new InternalRecipe.Cooking(Material.TERRACOTTA, 1, Ingredient.of(Material.CLAY)));
        recipes.add(new InternalRecipe.Cooking(Material.GREEN_DYE, 1, Ingredient.of(Material.CACTUS)));

        // === STONECUTTING RECIPES ===
        recipes.add(new InternalRecipe.Stonecutting(Material.STONE_STAIRS, 1, Ingredient.of(Material.STONE)));
        recipes.add(new InternalRecipe.Stonecutting(Material.STONE_BRICKS, 1, Ingredient.of(Material.STONE)));

        // === SMITHING RECIPES ===
        recipes.add(new InternalRecipe.Smithing(Material.NETHERITE_CHESTPLATE, 1,
                Ingredient.of(Material.COBBLESTONE), Ingredient.of(Material.DIAMOND_CHESTPLATE),
                Ingredient.of(Material.NETHERITE_INGOT)));
        recipes.add(new InternalRecipe.Smithing(Material.NETHERITE_PICKAXE, 1,
                Ingredient.of(Material.COBBLESTONE), Ingredient.of(Material.DIAMOND_PICKAXE),
                Ingredient.of(Material.NETHERITE_INGOT)));
        recipes.add(new InternalRecipe.Smithing(Material.NETHERITE_SWORD, 1,
                Ingredient.of(Material.COBBLESTONE), Ingredient.of(Material.DIAMOND_SWORD),
                Ingredient.of(Material.NETHERITE_INGOT)));

        // Run
        Logger logger = Logger.getLogger(EMCEngineTest.class.getName());
        emcValues = EMCEngine.recalculate(logger, recipes);
    }

    // === ASSERTIONS ===

    @Test void testWoodChain() {
        assertEquals(8L, emcValues.get(Material.OAK_PLANKS));
        assertEquals(4L, emcValues.get(Material.STICK));
    }
    @Test void testWoodenItems() {
        assertEquals(32L, emcValues.get(Material.CRAFTING_TABLE));
        assertEquals(64L, emcValues.get(Material.CHEST));
        assertEquals(40L, emcValues.get(Material.OAK_BOAT));
        assertEquals(24L, emcValues.get(Material.BOWL));
        assertEquals(32L, emcValues.get(Material.WOODEN_PICKAXE));
    }
    @Test void testStoneTools() {
        assertEquals(11L, emcValues.get(Material.STONE_PICKAXE));
        assertEquals(6L, emcValues.get(Material.STONE_SWORD));
    }
    @Test void testIronTools() {
        assertEquals(3080L, emcValues.get(Material.IRON_PICKAXE));
        assertEquals(2052L, emcValues.get(Material.IRON_SWORD));
        assertEquals(3080L, emcValues.get(Material.IRON_AXE));
        assertEquals(1032L, emcValues.get(Material.IRON_SHOVEL));
        assertEquals(2056L, emcValues.get(Material.IRON_HOE));
    }
    @Test void testIronArmor() {
        assertEquals(8192L, emcValues.get(Material.IRON_CHESTPLATE));
        assertEquals(5120L, emcValues.get(Material.IRON_HELMET));
        assertEquals(7168L, emcValues.get(Material.IRON_LEGGINGS));
        assertEquals(4096L, emcValues.get(Material.IRON_BOOTS));
    }
    @Test void testDiamondTools() {
        assertEquals(24584L, emcValues.get(Material.DIAMOND_PICKAXE));
        assertEquals(16388L, emcValues.get(Material.DIAMOND_SWORD));
    }
    @Test void testDiamondArmor() {
        assertEquals(65536L, emcValues.get(Material.DIAMOND_CHESTPLATE));
    }
    @Test void testGoldTools() {
        assertEquals(16384L, emcValues.get(Material.GOLDEN_CHESTPLATE));
        assertEquals(6152L, emcValues.get(Material.GOLDEN_PICKAXE));
    }
    @Test void testMiscCrafted() {
        assertEquals(3072L, emcValues.get(Material.BUCKET));
        assertEquals(8L, emcValues.get(Material.FURNACE));
        assertEquals(8224L, emcValues.get(Material.CLOCK));
        assertEquals(4128L, emcValues.get(Material.COMPASS));
        assertEquals(9L, emcValues.get(Material.LADDER));
        assertEquals(17L, emcValues.get(Material.OAK_SIGN));
        assertEquals(16L, emcValues.get(Material.OAK_DOOR));
        assertEquals(24L, emcValues.get(Material.OAK_TRAPDOOR));
        assertEquals(13L, emcValues.get(Material.OAK_FENCE));
        assertEquals(32L, emcValues.get(Material.OAK_FENCE_GATE));
        assertEquals(384L, emcValues.get(Material.IRON_BARS));
    }
    @Test void testBlocks() {
        assertEquals(9216L, emcValues.get(Material.IRON_BLOCK));
        assertEquals(18432L, emcValues.get(Material.GOLD_BLOCK));
        assertEquals(73728L, emcValues.get(Material.DIAMOND_BLOCK));
        assertEquals(147456L, emcValues.get(Material.EMERALD_BLOCK));
        assertEquals(288L, emcValues.get(Material.COAL_BLOCK));
        assertEquals(4608L, emcValues.get(Material.COPPER_BLOCK));
        assertEquals(288L, emcValues.get(Material.REDSTONE_BLOCK));
        assertEquals(7776L, emcValues.get(Material.LAPIS_BLOCK));
        assertEquals(256L, emcValues.get(Material.QUARTZ_BLOCK));
        assertEquals(64L, emcValues.get(Material.CLAY));
        assertEquals(4L, emcValues.get(Material.SNOW_BLOCK));
        assertEquals(1536L, emcValues.get(Material.GLOWSTONE));
        assertEquals(64L, emcValues.get(Material.PRISMARINE));
        assertEquals(144L, emcValues.get(Material.PRISMARINE_BRICKS));
        assertEquals(384L, emcValues.get(Material.SEA_LANTERN));
    }
    @Test void testNuggets() {
        assertEquals(113L, emcValues.get(Material.IRON_NUGGET));
        assertEquals(227L, emcValues.get(Material.GOLD_NUGGET));
    }
    @Test void testFurnaceResults() {
        assertEquals(33L, emcValues.get(Material.GLASS));
        assertEquals(48L, emcValues.get(Material.BRICK));
        assertEquals(33L, emcValues.get(Material.NETHER_BRICK));
        assertEquals(96L, emcValues.get(Material.TERRACOTTA));
        assertEquals(33L, emcValues.get(Material.GREEN_DYE));
    }
    @Test void testStonecutting() {
        assertEquals(1L, emcValues.get(Material.STONE_STAIRS));
        assertEquals(1L, emcValues.get(Material.STONE_BRICKS));
    }
    @Test void testNetherite() {
        assertEquals(40960L, emcValues.get(Material.NETHERITE_INGOT));
        assertEquals(106497L, emcValues.get(Material.NETHERITE_CHESTPLATE));
        assertEquals(65545L, emcValues.get(Material.NETHERITE_PICKAXE));
        assertEquals(57349L, emcValues.get(Material.NETHERITE_SWORD));
        assertEquals(368640L, emcValues.get(Material.NETHERITE_BLOCK));
    }

    @Test void testNoMissingCoreItems() {
        List<Material> mustHave = List.of(
                Material.OAK_PLANKS, Material.STICK, Material.CRAFTING_TABLE,
                Material.CHEST, Material.OAK_BOAT, Material.BOWL,
                Material.BUCKET, Material.FURNACE,
                Material.IRON_CHESTPLATE, Material.IRON_HELMET,
                Material.IRON_LEGGINGS, Material.IRON_BOOTS,
                Material.IRON_PICKAXE, Material.IRON_SWORD, Material.IRON_AXE,
                Material.IRON_SHOVEL, Material.IRON_HOE,
                Material.DIAMOND_PICKAXE, Material.DIAMOND_SWORD,
                Material.DIAMOND_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.GOLDEN_PICKAXE,
                Material.CLOCK, Material.COMPASS,
                Material.GLASS, Material.BRICK, Material.NETHER_BRICK,
                Material.TERRACOTTA, Material.GREEN_DYE,
                Material.IRON_BLOCK, Material.GOLD_BLOCK,
                Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK,
                Material.NETHERITE_INGOT, Material.NETHERITE_CHESTPLATE,
                Material.NETHERITE_PICKAXE, Material.NETHERITE_SWORD,
                Material.NETHERITE_BLOCK
        );
        for (Material mat : mustHave) {
            assertNotNull(emcValues.get(mat), "Missing EMC for " + mat.name());
        }
    }

    @Test void printReport() {
        System.out.println("\n==========================================");
        System.out.println("=== EMC VALUES (" + emcValues.size() + " mapped) ===");
        System.out.println("==========================================");
        emcValues.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.printf("%-45s = %d%n", e.getKey().name(), e.getValue()));

        System.out.println("\n==========================================");
        System.out.println("=== BASE VALUES (" + EMCBaseValues.BASE_VALUES.size() + " total) ===");
        System.out.println("==========================================");
        EMCBaseValues.BASE_VALUES.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> System.out.printf("%-45s = %d%n", e.getKey().name(), e.getValue()));

        // Note: can't iterate Material.values() without a server
        System.out.println("\n==========================================");
        System.out.println("(Cannot enumerate ALL missing items — needs a running server)");
        System.out.println("Use /emc listempty in-game for the full missing-items list.");
        System.out.println("==========================================\n");
    }

    // === HELPERS ===

    private static InternalRecipe.Shaped shaped(Material result, int amount, String[] shape,
                                                  Map<Character, Ingredient> ingredients) {
        return new InternalRecipe.Shaped(result, amount, shape, ingredients);
    }

    private static InternalRecipe.Shapeless shapeless(Material result, int amount,
                                                       Ingredient ingredient, int count) {
        List<Ingredient> list = new ArrayList<>();
        for (int i = 0; i < count; i++) list.add(ingredient);
        return new InternalRecipe.Shapeless(result, amount, list);
    }

    private static List<Ingredient> concat(Ingredient a, int countA, Ingredient b, int countB) {
        List<Ingredient> list = new ArrayList<>();
        for (int i = 0; i < countA; i++) list.add(a);
        for (int i = 0; i < countB; i++) list.add(b);
        return list;
    }
}
