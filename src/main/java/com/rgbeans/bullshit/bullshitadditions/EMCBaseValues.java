package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EMCBaseValues {

    static final Map<Material, Long> BASE_VALUES = new LinkedHashMap<>();

    static {
        BASE_VALUES.put(Material.AIR, 0L);
        BASE_VALUES.put(Material.COBBLESTONE, 1L);
        BASE_VALUES.put(Material.STONE, 1L);
        BASE_VALUES.put(Material.DIRT, 1L);
        BASE_VALUES.put(Material.GRASS_BLOCK, 1L);
        BASE_VALUES.put(Material.SAND, 1L);
        BASE_VALUES.put(Material.GRAVEL, 4L);
        BASE_VALUES.put(Material.NETHERRACK, 1L);
        BASE_VALUES.put(Material.END_STONE, 4L);
        BASE_VALUES.put(Material.OBSIDIAN, 64L);
        BASE_VALUES.put(Material.CLAY_BALL, 16L);
        BASE_VALUES.put(Material.FLINT, 4L);
        BASE_VALUES.put(Material.ICE, 1L);
        BASE_VALUES.put(Material.SNOWBALL, 1L);
        BASE_VALUES.put(Material.VINE, 8L);
        BASE_VALUES.put(Material.LILY_PAD, 16L);
        BASE_VALUES.put(Material.CACTUS, 1L);
        BASE_VALUES.put(Material.SUGAR_CANE, 32L);

        BASE_VALUES.put(Material.OAK_LOG, 32L);
        BASE_VALUES.put(Material.SPRUCE_LOG, 32L);
        BASE_VALUES.put(Material.BIRCH_LOG, 32L);
        BASE_VALUES.put(Material.JUNGLE_LOG, 32L);
        BASE_VALUES.put(Material.ACACIA_LOG, 32L);
        BASE_VALUES.put(Material.DARK_OAK_LOG, 32L);
        BASE_VALUES.put(Material.MANGROVE_LOG, 32L);
        BASE_VALUES.put(Material.CHERRY_LOG, 32L);
        BASE_VALUES.put(Material.CRIMSON_STEM, 32L);
        BASE_VALUES.put(Material.WARPED_STEM, 32L);
        BASE_VALUES.put(Material.BAMBOO_BLOCK, 32L);

        BASE_VALUES.put(Material.OAK_SAPLING, 32L);
        BASE_VALUES.put(Material.SPRUCE_SAPLING, 32L);
        BASE_VALUES.put(Material.BIRCH_SAPLING, 32L);
        BASE_VALUES.put(Material.JUNGLE_SAPLING, 32L);
        BASE_VALUES.put(Material.ACACIA_SAPLING, 32L);
        BASE_VALUES.put(Material.DARK_OAK_SAPLING, 32L);
        BASE_VALUES.put(Material.MANGROVE_PROPAGULE, 32L);
        BASE_VALUES.put(Material.CHERRY_SAPLING, 32L);

        BASE_VALUES.put(Material.WHEAT_SEEDS, 1L);
        BASE_VALUES.put(Material.BEETROOT_SEEDS, 1L);
        BASE_VALUES.put(Material.PUMPKIN_SEEDS, 1L);
        BASE_VALUES.put(Material.MELON_SEEDS, 1L);

        BASE_VALUES.put(Material.BROWN_MUSHROOM, 32L);
        BASE_VALUES.put(Material.RED_MUSHROOM, 32L);
        BASE_VALUES.put(Material.NETHER_WART, 24L);
        BASE_VALUES.put(Material.CRIMSON_FUNGUS, 32L);
        BASE_VALUES.put(Material.WARPED_FUNGUS, 32L);

        BASE_VALUES.put(Material.EGG, 32L);
        BASE_VALUES.put(Material.COCOA_BEANS, 32L);

        BASE_VALUES.put(Material.ROTTEN_FLESH, 1L);
        BASE_VALUES.put(Material.BONE, 4L);
        BASE_VALUES.put(Material.STRING, 12L);
        BASE_VALUES.put(Material.SPIDER_EYE, 128L);
        BASE_VALUES.put(Material.GUNPOWDER, 192L);
        BASE_VALUES.put(Material.ENDER_PEARL, 1024L);
        BASE_VALUES.put(Material.BLAZE_ROD, 1536L);
        BASE_VALUES.put(Material.GHAST_TEAR, 4096L);
        BASE_VALUES.put(Material.SLIME_BALL, 256L);
        BASE_VALUES.put(Material.FEATHER, 48L);
        BASE_VALUES.put(Material.LEATHER, 48L);
        BASE_VALUES.put(Material.INK_SAC, 16L);
        BASE_VALUES.put(Material.SHULKER_SHELL, 2048L);
        BASE_VALUES.put(Material.GLOW_INK_SAC, 16L);
        BASE_VALUES.put(Material.BREEZE_ROD, 2304L);

        BASE_VALUES.put(Material.COAL, 32L);
        BASE_VALUES.put(Material.CHARCOAL, 32L);

        BASE_VALUES.put(Material.RAW_IRON, 1024L);
        BASE_VALUES.put(Material.RAW_GOLD, 2048L);
        BASE_VALUES.put(Material.RAW_COPPER, 512L);
        BASE_VALUES.put(Material.IRON_INGOT, 1024L);
        BASE_VALUES.put(Material.GOLD_INGOT, 2048L);
        BASE_VALUES.put(Material.COPPER_INGOT, 512L);

        BASE_VALUES.put(Material.DIAMOND, 8192L);
        BASE_VALUES.put(Material.EMERALD, 16384L);
        BASE_VALUES.put(Material.REDSTONE, 32L);
        BASE_VALUES.put(Material.LAPIS_LAZULI, 864L);
        BASE_VALUES.put(Material.QUARTZ, 64L);
        BASE_VALUES.put(Material.NETHERITE_SCRAP, 8192L);

        BASE_VALUES.put(Material.STICK, 4L);
        BASE_VALUES.put(Material.PRISMARINE_SHARD, 16L);
        BASE_VALUES.put(Material.PRISMARINE_CRYSTALS, 64L);
        BASE_VALUES.put(Material.GLOWSTONE_DUST, 384L);
        BASE_VALUES.put(Material.HEAVY_CORE, 32768L);
        BASE_VALUES.put(Material.OMINOUS_TRIAL_KEY, 2048L);

        BASE_VALUES.put(Material.DEEPSLATE, 1L);
        BASE_VALUES.put(Material.GRANITE, 1L);
        BASE_VALUES.put(Material.DIORITE, 1L);
        BASE_VALUES.put(Material.ANDESITE, 1L);
        BASE_VALUES.put(Material.TUFF, 1L);
        BASE_VALUES.put(Material.BLACKSTONE, 1L);
        BASE_VALUES.put(Material.BASALT, 1L);
        BASE_VALUES.put(Material.CALCITE, 1L);
        BASE_VALUES.put(Material.DRIPSTONE_BLOCK, 4L);
        BASE_VALUES.put(Material.POINTED_DRIPSTONE, 1L);

        BASE_VALUES.put(Material.SOUL_SAND, 8L);
        BASE_VALUES.put(Material.SOUL_SOIL, 8L);

        BASE_VALUES.put(Material.KELP, 1L);
        BASE_VALUES.put(Material.BAMBOO, 1L);
        BASE_VALUES.put(Material.SWEET_BERRIES, 16L);
        BASE_VALUES.put(Material.GLOW_BERRIES, 16L);
        BASE_VALUES.put(Material.MOSS_BLOCK, 8L);
        BASE_VALUES.put(Material.GLOW_LICHEN, 1L);
        BASE_VALUES.put(Material.HANGING_ROOTS, 4L);
        BASE_VALUES.put(Material.TWISTING_VINES, 1L);
        BASE_VALUES.put(Material.WEEPING_VINES, 1L);

        BASE_VALUES.put(Material.AMETHYST_SHARD, 64L);
        BASE_VALUES.put(Material.SEA_PICKLE, 64L);
        BASE_VALUES.put(Material.HONEYCOMB, 64L);

        BASE_VALUES.put(Material.PHANTOM_MEMBRANE, 256L);
        BASE_VALUES.put(Material.RABBIT_HIDE, 32L);
        BASE_VALUES.put(Material.RABBIT_FOOT, 512L);
        BASE_VALUES.put(Material.NAUTILUS_SHELL, 1024L);
        BASE_VALUES.put(Material.TURTLE_SCUTE, 256L);
        BASE_VALUES.put(Material.ARMADILLO_SCUTE, 128L);
        BASE_VALUES.put(Material.ECHO_SHARD, 2048L);
        BASE_VALUES.put(Material.HEART_OF_THE_SEA, 4096L);
        BASE_VALUES.put(Material.GOAT_HORN, 512L);
        BASE_VALUES.put(Material.TRIDENT, 8192L);
        BASE_VALUES.put(Material.TOTEM_OF_UNDYING, 65536L);
        BASE_VALUES.put(Material.NETHER_STAR, 131072L);

        BASE_VALUES.put(Material.WHEAT, 24L);
        BASE_VALUES.put(Material.POTATO, 16L);
        BASE_VALUES.put(Material.CARROT, 16L);
        BASE_VALUES.put(Material.BEETROOT, 16L);
        BASE_VALUES.put(Material.MELON_SLICE, 8L);
        BASE_VALUES.put(Material.APPLE, 32L);
        BASE_VALUES.put(Material.CHORUS_FRUIT, 64L);
        BASE_VALUES.put(Material.PITCHER_POD, 32L);
        BASE_VALUES.put(Material.TORCHFLOWER_SEEDS, 32L);
        BASE_VALUES.put(Material.BLUE_EGG, 32L);
        BASE_VALUES.put(Material.BROWN_EGG, 32L);
        BASE_VALUES.put(Material.TURTLE_EGG, 256L);
        BASE_VALUES.put(Material.SNIFFER_EGG, 4096L);

        BASE_VALUES.put(Material.BEEF, 64L);
        BASE_VALUES.put(Material.CHICKEN, 64L);
        BASE_VALUES.put(Material.COD, 64L);
        BASE_VALUES.put(Material.MUTTON, 64L);
        BASE_VALUES.put(Material.PORKCHOP, 64L);
        BASE_VALUES.put(Material.RABBIT, 64L);
        BASE_VALUES.put(Material.SALMON, 64L);
        BASE_VALUES.put(Material.TROPICAL_FISH, 64L);
        BASE_VALUES.put(Material.PUFFERFISH, 128L);

        BASE_VALUES.put(Material.COAL_ORE, 32L);
        BASE_VALUES.put(Material.COPPER_ORE, 512L);
        BASE_VALUES.put(Material.DIAMOND_ORE, 8192L);
        BASE_VALUES.put(Material.EMERALD_ORE, 16384L);
        BASE_VALUES.put(Material.GOLD_ORE, 2048L);
        BASE_VALUES.put(Material.IRON_ORE, 1024L);
        BASE_VALUES.put(Material.LAPIS_ORE, 864L);
        BASE_VALUES.put(Material.REDSTONE_ORE, 32L);
        BASE_VALUES.put(Material.NETHER_GOLD_ORE, 2048L);
        BASE_VALUES.put(Material.NETHER_QUARTZ_ORE, 64L);
        BASE_VALUES.put(Material.ANCIENT_DEBRIS, 8192L);
        BASE_VALUES.put(Material.GILDED_BLACKSTONE, 256L);
        BASE_VALUES.put(Material.DEEPSLATE_COAL_ORE, 32L);
        BASE_VALUES.put(Material.DEEPSLATE_COPPER_ORE, 512L);
        BASE_VALUES.put(Material.DEEPSLATE_DIAMOND_ORE, 8192L);
        BASE_VALUES.put(Material.DEEPSLATE_EMERALD_ORE, 16384L);
        BASE_VALUES.put(Material.DEEPSLATE_GOLD_ORE, 2048L);
        BASE_VALUES.put(Material.DEEPSLATE_IRON_ORE, 1024L);
        BASE_VALUES.put(Material.DEEPSLATE_LAPIS_ORE, 864L);
        BASE_VALUES.put(Material.DEEPSLATE_REDSTONE_ORE, 32L);

        BASE_VALUES.put(Material.OAK_LEAVES, 1L);
        BASE_VALUES.put(Material.SPRUCE_LEAVES, 1L);
        BASE_VALUES.put(Material.BIRCH_LEAVES, 1L);
        BASE_VALUES.put(Material.JUNGLE_LEAVES, 1L);
        BASE_VALUES.put(Material.ACACIA_LEAVES, 1L);
        BASE_VALUES.put(Material.DARK_OAK_LEAVES, 1L);
        BASE_VALUES.put(Material.MANGROVE_LEAVES, 1L);
        BASE_VALUES.put(Material.CHERRY_LEAVES, 1L);
        BASE_VALUES.put(Material.PALE_OAK_LEAVES, 1L);
        BASE_VALUES.put(Material.AZALEA_LEAVES, 1L);
        BASE_VALUES.put(Material.FLOWERING_AZALEA_LEAVES, 1L);

        BASE_VALUES.put(Material.STRIPPED_OAK_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_SPRUCE_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_BIRCH_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_JUNGLE_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_ACACIA_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_DARK_OAK_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_MANGROVE_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_CHERRY_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_PALE_OAK_LOG, 32L);
        BASE_VALUES.put(Material.STRIPPED_CRIMSON_STEM, 32L);
        BASE_VALUES.put(Material.STRIPPED_WARPED_STEM, 32L);
        BASE_VALUES.put(Material.STRIPPED_BAMBOO_BLOCK, 32L);

        BASE_VALUES.put(Material.PALE_OAK_LOG, 32L);
        BASE_VALUES.put(Material.PALE_OAK_SAPLING, 32L);

        BASE_VALUES.put(Material.PUMPKIN, 16L);
        BASE_VALUES.put(Material.RED_SAND, 1L);
        BASE_VALUES.put(Material.MUD, 1L);
        BASE_VALUES.put(Material.PODZOL, 4L);
        BASE_VALUES.put(Material.MYCELIUM, 4L);
        BASE_VALUES.put(Material.ROOTED_DIRT, 2L);
        BASE_VALUES.put(Material.MUDDY_MANGROVE_ROOTS, 8L);
        BASE_VALUES.put(Material.MANGROVE_ROOTS, 4L);
        BASE_VALUES.put(Material.MUSHROOM_STEM, 32L);
        BASE_VALUES.put(Material.BROWN_MUSHROOM_BLOCK, 32L);
        BASE_VALUES.put(Material.RED_MUSHROOM_BLOCK, 32L);
        BASE_VALUES.put(Material.CRIMSON_NYLIUM, 4L);
        BASE_VALUES.put(Material.WARPED_NYLIUM, 4L);
        BASE_VALUES.put(Material.WARPED_WART_BLOCK, 64L);
        BASE_VALUES.put(Material.NETHER_SPROUTS, 8L);
        BASE_VALUES.put(Material.CRIMSON_ROOTS, 8L);
        BASE_VALUES.put(Material.WARPED_ROOTS, 8L);
        BASE_VALUES.put(Material.SHROOMLIGHT, 256L);
        BASE_VALUES.put(Material.SPORE_BLOSSOM, 32L);
        BASE_VALUES.put(Material.SEAGRASS, 1L);
        BASE_VALUES.put(Material.SHORT_GRASS, 1L);
        BASE_VALUES.put(Material.TALL_GRASS, 1L);
        BASE_VALUES.put(Material.FERN, 1L);
        BASE_VALUES.put(Material.LARGE_FERN, 1L);
        BASE_VALUES.put(Material.DEAD_BUSH, 1L);

        BASE_VALUES.put(Material.POPPY, 16L);
        BASE_VALUES.put(Material.DANDELION, 16L);
        BASE_VALUES.put(Material.BLUE_ORCHID, 16L);
        BASE_VALUES.put(Material.ALLIUM, 16L);
        BASE_VALUES.put(Material.AZURE_BLUET, 16L);
        BASE_VALUES.put(Material.RED_TULIP, 16L);
        BASE_VALUES.put(Material.ORANGE_TULIP, 16L);
        BASE_VALUES.put(Material.WHITE_TULIP, 16L);
        BASE_VALUES.put(Material.PINK_TULIP, 16L);
        BASE_VALUES.put(Material.OXEYE_DAISY, 16L);
        BASE_VALUES.put(Material.CORNFLOWER, 16L);
        BASE_VALUES.put(Material.LILY_OF_THE_VALLEY, 16L);
        BASE_VALUES.put(Material.SUNFLOWER, 16L);
        BASE_VALUES.put(Material.LILAC, 16L);
        BASE_VALUES.put(Material.ROSE_BUSH, 16L);
        BASE_VALUES.put(Material.PEONY, 16L);
        BASE_VALUES.put(Material.TORCHFLOWER, 16L);
        BASE_VALUES.put(Material.PITCHER_PLANT, 32L);
        BASE_VALUES.put(Material.WITHER_ROSE, 64L);

        BASE_VALUES.put(Material.WATER_BUCKET, 3072L);
        BASE_VALUES.put(Material.LAVA_BUCKET, 3072L);
        BASE_VALUES.put(Material.MILK_BUCKET, 3072L);
        BASE_VALUES.put(Material.POWDER_SNOW_BUCKET, 3072L);
        BASE_VALUES.put(Material.COD_BUCKET, 3136L);
        BASE_VALUES.put(Material.SALMON_BUCKET, 3136L);
        BASE_VALUES.put(Material.TROPICAL_FISH_BUCKET, 3136L);
        BASE_VALUES.put(Material.PUFFERFISH_BUCKET, 3200L);
        BASE_VALUES.put(Material.AXOLOTL_BUCKET, 3136L);
        BASE_VALUES.put(Material.TADPOLE_BUCKET, 3136L);

        BASE_VALUES.put(Material.CHAINMAIL_HELMET, 256L);
        BASE_VALUES.put(Material.CHAINMAIL_CHESTPLATE, 512L);
        BASE_VALUES.put(Material.CHAINMAIL_LEGGINGS, 384L);
        BASE_VALUES.put(Material.CHAINMAIL_BOOTS, 256L);

        BASE_VALUES.put(Material.IRON_HORSE_ARMOR, 4096L);
        BASE_VALUES.put(Material.GOLDEN_HORSE_ARMOR, 8192L);
        BASE_VALUES.put(Material.DIAMOND_HORSE_ARMOR, 32768L);
        BASE_VALUES.put(Material.COPPER_HORSE_ARMOR, 2048L);
        BASE_VALUES.put(Material.IRON_NAUTILUS_ARMOR, 4096L);
        BASE_VALUES.put(Material.GOLDEN_NAUTILUS_ARMOR, 8192L);
        BASE_VALUES.put(Material.DIAMOND_NAUTILUS_ARMOR, 32768L);
        BASE_VALUES.put(Material.COPPER_NAUTILUS_ARMOR, 2048L);

        BASE_VALUES.put(Material.POISONOUS_POTATO, 1L);
        BASE_VALUES.put(Material.DRAGON_BREATH, 64L);
        BASE_VALUES.put(Material.ELYTRA, 65536L);
        BASE_VALUES.put(Material.ENCHANTED_GOLDEN_APPLE, 131072L);
        BASE_VALUES.put(Material.EXPERIENCE_BOTTLE, 256L);
        BASE_VALUES.put(Material.OMINOUS_BOTTLE, 2048L);
        BASE_VALUES.put(Material.TRIAL_KEY, 1024L);
        BASE_VALUES.put(Material.DISC_FRAGMENT_5, 512L);
        BASE_VALUES.put(Material.COMMAND_BLOCK_MINECART, 36864L);

        BASE_VALUES.put(Material.MUSIC_DISC_11, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_13, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_BLOCKS, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_CAT, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_CHIRP, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_CREATOR, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_CREATOR_MUSIC_BOX, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_FAR, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_LAVA_CHICKEN, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_MALL, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_MELLOHI, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_OTHERSIDE, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_PIGSTEP, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_PRECIPICE, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_RELIC, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_STAL, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_STRAD, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_TEARS, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_WAIT, 2048L);
        BASE_VALUES.put(Material.MUSIC_DISC_WARD, 2048L);

        BASE_VALUES.put(Material.AMETHYST_CLUSTER, 256L);
        BASE_VALUES.put(Material.LARGE_AMETHYST_BUD, 128L);
        BASE_VALUES.put(Material.MEDIUM_AMETHYST_BUD, 64L);
        BASE_VALUES.put(Material.SMALL_AMETHYST_BUD, 32L);
        BASE_VALUES.put(Material.BUDDING_AMETHYST, 512L);

        BASE_VALUES.put(Material.AZALEA, 1L);
        BASE_VALUES.put(Material.FLOWERING_AZALEA, 1L);
        BASE_VALUES.put(Material.BIG_DRIPLEAF, 1L);
        BASE_VALUES.put(Material.SMALL_DRIPLEAF, 1L);
        BASE_VALUES.put(Material.BUSH, 4L);
        BASE_VALUES.put(Material.FIREFLY_BUSH, 4L);
        BASE_VALUES.put(Material.CACTUS_FLOWER, 16L);
        BASE_VALUES.put(Material.CLOSED_EYEBLOSSOM, 16L);
        BASE_VALUES.put(Material.OPEN_EYEBLOSSOM, 16L);
        BASE_VALUES.put(Material.PINK_PETALS, 8L);
        BASE_VALUES.put(Material.WILDFLOWERS, 8L);
        BASE_VALUES.put(Material.SHORT_DRY_GRASS, 1L);
        BASE_VALUES.put(Material.TALL_DRY_GRASS, 1L);

        BASE_VALUES.put(Material.BEE_NEST, 128L);
        BASE_VALUES.put(Material.BELL, 4096L);
        BASE_VALUES.put(Material.COBWEB, 16L);
        BASE_VALUES.put(Material.CHORUS_FLOWER, 64L);
        BASE_VALUES.put(Material.CHORUS_PLANT, 32L);
        BASE_VALUES.put(Material.CRYING_OBSIDIAN, 128L);
        BASE_VALUES.put(Material.DRAGON_EGG, 65536L);
        BASE_VALUES.put(Material.REINFORCED_DEEPSLATE, 64L);

        BASE_VALUES.put(Material.CARVED_PUMPKIN, 16L);
        BASE_VALUES.put(Material.DIRT_PATH, 1L);
        BASE_VALUES.put(Material.FARMLAND, 1L);
        BASE_VALUES.put(Material.PALE_MOSS_BLOCK, 8L);
        BASE_VALUES.put(Material.PALE_HANGING_MOSS, 4L);

        BASE_VALUES.put(Material.FROGSPAWN, 128L);
        BASE_VALUES.put(Material.OCHRE_FROGLIGHT, 256L);
        BASE_VALUES.put(Material.PEARLESCENT_FROGLIGHT, 256L);
        BASE_VALUES.put(Material.VERDANT_FROGLIGHT, 256L);

        BASE_VALUES.put(Material.SCULK, 64L);
        BASE_VALUES.put(Material.SCULK_SENSOR, 512L);
        BASE_VALUES.put(Material.SCULK_CATALYST, 1024L);
        BASE_VALUES.put(Material.SCULK_SHRIEKER, 2048L);
        BASE_VALUES.put(Material.SCULK_VEIN, 16L);

        BASE_VALUES.put(Material.SUSPICIOUS_SAND, 128L);
        BASE_VALUES.put(Material.SUSPICIOUS_GRAVEL, 128L);
        BASE_VALUES.put(Material.WET_SPONGE, 512L);
        BASE_VALUES.put(Material.SPAWNER, 8192L);
        BASE_VALUES.put(Material.TRIAL_SPAWNER, 8192L);
        BASE_VALUES.put(Material.VAULT, 16384L);

        BASE_VALUES.put(Material.CREEPER_HEAD, 2048L);
        BASE_VALUES.put(Material.SKELETON_SKULL, 2048L);
        BASE_VALUES.put(Material.WITHER_SKELETON_SKULL, 8192L);
        BASE_VALUES.put(Material.ZOMBIE_HEAD, 2048L);
        BASE_VALUES.put(Material.PIGLIN_HEAD, 2048L);
        BASE_VALUES.put(Material.DRAGON_HEAD, 32768L);

        BASE_VALUES.put(Material.TUBE_CORAL, 32L);
        BASE_VALUES.put(Material.BRAIN_CORAL, 32L);
        BASE_VALUES.put(Material.BUBBLE_CORAL, 32L);
        BASE_VALUES.put(Material.FIRE_CORAL, 32L);
        BASE_VALUES.put(Material.HORN_CORAL, 32L);
        BASE_VALUES.put(Material.TUBE_CORAL_BLOCK, 128L);
        BASE_VALUES.put(Material.BRAIN_CORAL_BLOCK, 128L);
        BASE_VALUES.put(Material.BUBBLE_CORAL_BLOCK, 128L);
        BASE_VALUES.put(Material.FIRE_CORAL_BLOCK, 128L);
        BASE_VALUES.put(Material.HORN_CORAL_BLOCK, 128L);
        BASE_VALUES.put(Material.TUBE_CORAL_FAN, 32L);
        BASE_VALUES.put(Material.BRAIN_CORAL_FAN, 32L);
        BASE_VALUES.put(Material.BUBBLE_CORAL_FAN, 32L);
        BASE_VALUES.put(Material.FIRE_CORAL_FAN, 32L);
        BASE_VALUES.put(Material.HORN_CORAL_FAN, 32L);
        BASE_VALUES.put(Material.DEAD_TUBE_CORAL, 16L);
        BASE_VALUES.put(Material.DEAD_BRAIN_CORAL, 16L);
        BASE_VALUES.put(Material.DEAD_BUBBLE_CORAL, 16L);
        BASE_VALUES.put(Material.DEAD_FIRE_CORAL, 16L);
        BASE_VALUES.put(Material.DEAD_HORN_CORAL, 16L);
        BASE_VALUES.put(Material.DEAD_TUBE_CORAL_BLOCK, 64L);
        BASE_VALUES.put(Material.DEAD_BRAIN_CORAL_BLOCK, 64L);
        BASE_VALUES.put(Material.DEAD_BUBBLE_CORAL_BLOCK, 64L);
        BASE_VALUES.put(Material.DEAD_FIRE_CORAL_BLOCK, 64L);
        BASE_VALUES.put(Material.DEAD_HORN_CORAL_BLOCK, 64L);
        BASE_VALUES.put(Material.DEAD_TUBE_CORAL_FAN, 16L);
        BASE_VALUES.put(Material.DEAD_BRAIN_CORAL_FAN, 16L);
        BASE_VALUES.put(Material.DEAD_BUBBLE_CORAL_FAN, 16L);
        BASE_VALUES.put(Material.DEAD_FIRE_CORAL_FAN, 16L);
        BASE_VALUES.put(Material.DEAD_HORN_CORAL_FAN, 16L);

        BASE_VALUES.put(Material.COPPER_GOLEM_STATUE, 16384L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_GOLEM_STATUE, 16384L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_GOLEM_STATUE, 16384L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_GOLEM_STATUE, 16384L);

        BASE_VALUES.put(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 57344L);

        BASE_VALUES.put(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);
        BASE_VALUES.put(Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, 57344L);

        BASE_VALUES.put(Material.HONEY_BLOCK, 384L);

        BASE_VALUES.put(Material.RESIN_CLUMP, 64L);
        BASE_VALUES.put(Material.CREAKING_HEART, 4096L);

        BASE_VALUES.put(Material.BUNDLE, 64L);
        BASE_VALUES.put(Material.WHITE_BUNDLE, 72L);
        BASE_VALUES.put(Material.LIGHT_GRAY_BUNDLE, 72L);
        BASE_VALUES.put(Material.GRAY_BUNDLE, 72L);
        BASE_VALUES.put(Material.BLACK_BUNDLE, 72L);
        BASE_VALUES.put(Material.BROWN_BUNDLE, 72L);
        BASE_VALUES.put(Material.RED_BUNDLE, 72L);
        BASE_VALUES.put(Material.ORANGE_BUNDLE, 72L);
        BASE_VALUES.put(Material.YELLOW_BUNDLE, 72L);
        BASE_VALUES.put(Material.LIME_BUNDLE, 72L);
        BASE_VALUES.put(Material.GREEN_BUNDLE, 72L);
        BASE_VALUES.put(Material.CYAN_BUNDLE, 72L);
        BASE_VALUES.put(Material.LIGHT_BLUE_BUNDLE, 72L);
        BASE_VALUES.put(Material.BLUE_BUNDLE, 72L);
        BASE_VALUES.put(Material.PURPLE_BUNDLE, 72L);
        BASE_VALUES.put(Material.MAGENTA_BUNDLE, 72L);
        BASE_VALUES.put(Material.PINK_BUNDLE, 72L);

        BASE_VALUES.put(Material.SHULKER_BOX, 4160L);
        BASE_VALUES.put(Material.WHITE_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.LIGHT_GRAY_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.GRAY_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.BLACK_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.BROWN_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.RED_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.ORANGE_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.YELLOW_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.LIME_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.GREEN_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.CYAN_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.LIGHT_BLUE_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.BLUE_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.PURPLE_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.MAGENTA_SHULKER_BOX, 4168L);
        BASE_VALUES.put(Material.PINK_SHULKER_BOX, 4168L);

        BASE_VALUES.put(Material.FILLED_MAP, 8512L);
        BASE_VALUES.put(Material.HONEY_BOTTLE, 195L);
        BASE_VALUES.put(Material.TIPPED_ARROW, 704L);
        BASE_VALUES.put(Material.CHISELED_RESIN_BRICKS, 256L);
        BASE_VALUES.put(Material.RESIN_BRICK, 96L);
        BASE_VALUES.put(Material.RESIN_BLOCK, 576L);
        BASE_VALUES.put(Material.RESIN_BRICKS, 384L);
        BASE_VALUES.put(Material.RESIN_BRICK_SLAB, 64L);
        BASE_VALUES.put(Material.RESIN_BRICK_STAIRS, 96L);
        BASE_VALUES.put(Material.RESIN_BRICK_WALL, 96L);

        BASE_VALUES.put(Material.PLAYER_HEAD, 2048L);

        BASE_VALUES.put(Material.ENCHANTED_BOOK, 4096L);
        BASE_VALUES.put(Material.FIREWORK_STAR, 256L);
        BASE_VALUES.put(Material.POTION, 256L);
        BASE_VALUES.put(Material.SPLASH_POTION, 448L);
        BASE_VALUES.put(Material.LINGERING_POTION, 704L);
        BASE_VALUES.put(Material.WRITTEN_BOOK, 192L);

        BASE_VALUES.put(Material.CHIPPED_ANVIL, 31744L);
        BASE_VALUES.put(Material.DAMAGED_ANVIL, 31744L);

        BASE_VALUES.put(Material.BLACK_CONCRETE, 8L);
        BASE_VALUES.put(Material.BLUE_CONCRETE, 8L);
        BASE_VALUES.put(Material.BROWN_CONCRETE, 8L);
        BASE_VALUES.put(Material.CYAN_CONCRETE, 8L);
        BASE_VALUES.put(Material.GRAY_CONCRETE, 8L);
        BASE_VALUES.put(Material.GREEN_CONCRETE, 8L);
        BASE_VALUES.put(Material.LIGHT_BLUE_CONCRETE, 8L);
        BASE_VALUES.put(Material.LIGHT_GRAY_CONCRETE, 8L);
        BASE_VALUES.put(Material.LIME_CONCRETE, 8L);
        BASE_VALUES.put(Material.MAGENTA_CONCRETE, 8L);
        BASE_VALUES.put(Material.ORANGE_CONCRETE, 8L);
        BASE_VALUES.put(Material.PINK_CONCRETE, 8L);
        BASE_VALUES.put(Material.PURPLE_CONCRETE, 8L);
        BASE_VALUES.put(Material.RED_CONCRETE, 8L);
        BASE_VALUES.put(Material.WHITE_CONCRETE, 8L);
        BASE_VALUES.put(Material.YELLOW_CONCRETE, 8L);

        BASE_VALUES.put(Material.EXPOSED_COPPER, 4608L);
        BASE_VALUES.put(Material.WEATHERED_COPPER, 4608L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER, 4608L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_BARS, 3072L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_BARS, 3072L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_BARS, 3072L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_CHAIN, 1024L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_CHAIN, 1024L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_CHAIN, 1024L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_CHEST, 4608L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_CHEST, 4608L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_CHEST, 4608L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_DOOR, 1536L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_DOOR, 1536L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_DOOR, 1536L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_LANTERN, 528L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_LANTERN, 528L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_LANTERN, 528L);
        BASE_VALUES.put(Material.EXPOSED_COPPER_TRAPDOOR, 1536L);
        BASE_VALUES.put(Material.WEATHERED_COPPER_TRAPDOOR, 1536L);
        BASE_VALUES.put(Material.OXIDIZED_COPPER_TRAPDOOR, 1536L);
        BASE_VALUES.put(Material.EXPOSED_LIGHTNING_ROD, 1536L);
        BASE_VALUES.put(Material.WEATHERED_LIGHTNING_ROD, 1536L);
        BASE_VALUES.put(Material.OXIDIZED_LIGHTNING_ROD, 1536L);
    }

    private EMCBaseValues() {}
}
