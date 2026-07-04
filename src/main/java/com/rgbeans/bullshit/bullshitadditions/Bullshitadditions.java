package com.rgbeans.bullshit.bullshitadditions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.FileWriter;
import java.io.IOException;

public final class Bullshitadditions extends JavaPlugin {

    private RecipeGui recipeGui;
    private PlayerDataManager playerDataManager;
    private TransmutationTable transmutationTable;
    private final Map<UUID, int[]> lastAmmoCounts = new HashMap<>();

    @Override
    public void onEnable() {
        registerRecipes();
        EMCEngine.init(getDataFolder());
        EMCEngine.recalculate(getLogger());

        playerDataManager = new PlayerDataManager(this);
        transmutationTable = new TransmutationTable(this, playerDataManager);
        recipeGui = new RecipeGui(this);

        getServer().getPluginManager().registerEvents(new GunListener(), this);
        getServer().getPluginManager().registerEvents(new RifleListener(), this);
        getServer().getPluginManager().registerEvents(new HeavyStickListener(), this);
        getServer().getPluginManager().registerEvents(new TargetDummyListener(), this);
        getServer().getPluginManager().registerEvents(new AmmoBoxListener(), this);
        getServer().getPluginManager().registerEvents(recipeGui, this);
        getServer().getPluginManager().registerEvents(transmutationTable, this);
        getServer().getPluginManager().registerEvents(new TransmutationBlock(transmutationTable), this);

        getServer().getScheduler().runTaskTimer(this, this::updateAmmoScoreboards, 0L, 20L);
        getServer().getScheduler().runTaskTimer(this, this::updateEmcTabList, 0L, 100L);

        getLogger().info("Bullshit Additions enabled!");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) playerDataManager.saveAll();
        getLogger().info("Bullshit Additions disabled!");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String name = command.getName().toLowerCase();

        if (name.equals("ironbargun")) {
            return giveItem(sender, Pistol.create(), "Pistol");
        }

        if (name.equals("pistol")) {
            return giveItem(sender, Pistol.create(), "Pistol");
        }

        if (name.equals("targetdummy")) {
            return giveItem(sender, TargetDummyItem.create(), "Target Dummy");
        }

        if (name.equals("heavystick")) {
            return giveItem(sender, HeavyStick.create(), "Heavy Stick");
        }

        if (name.equals("ammobox")) {
            return giveItem(sender, AmmoBox.create(), "Ammo Box");
        }

        if (name.equals("rifle")) {
            return giveItem(sender, Rifle.create(), "Rifle");
        }

        if (name.equals("recipes")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            recipeGui.open(player);
            return true;
        }

        if (name.equals("transmute")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            transmutationTable.open(player);
            return true;
        }

        if (name.equals("transmutationtable")) {
            return giveItem(sender, TransmutationBlock.createItem(), "Transmutation Table");
        }

        if (name.equals("emc")) {
            return handleEmc(sender, args);
        }

        return false;
    }

    @Override
    public java.util.List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("emc")) return super.onTabComplete(sender, command, alias, args);

        if (args.length == 1) {
            return java.util.List.of("get", "add", "remove", "reload", "list", "listempty", "set", "trace", "missingroots").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return java.util.List.of();
    }

    private boolean handleEmc(CommandSender sender, String[] args) {
        String sub = args.length > 0 ? args[0].toLowerCase() : "";

        switch (sub) {
            case "get" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }
                double emc = playerDataManager.get(player.getUniqueId()).emc();
                sender.sendMessage("§6Your EMC: §f" + EMCEngine.formatEmc(emc));
                return true;
            }
            case "add" -> {
                if (!sender.isOp()) {
                    sender.sendMessage("§cYou must be an operator to use this command.");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /emc add <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    playerDataManager.get(player.getUniqueId()).addEmc(amount);
                    sender.sendMessage("§aAdded " + EMCEngine.formatEmc(amount) + " EMC. Balance: " + EMCEngine.formatEmc(playerDataManager.get(player.getUniqueId()).emc()));
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + args[1]);
                }
                return true;
            }
            case "remove" -> {
                if (!sender.isOp()) {
                    sender.sendMessage("§cYou must be an operator to use this command.");
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /emc remove <amount>");
                    return true;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    PlayerData data = playerDataManager.get(player.getUniqueId());
                    if (!data.spendEmc(amount)) {
                        sender.sendMessage("§cNot enough EMC! You have " + EMCEngine.formatEmc(data.emc()));
                        return true;
                    }
                    sender.sendMessage("§aRemoved " + EMCEngine.formatEmc(amount) + " EMC. Balance: " + EMCEngine.formatEmc(data.emc()));
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + args[1]);
                }
                return true;
            }
            case "reload" -> {
                EMCEngine.recalculate(getLogger());
                sender.sendMessage("§aEMC reloaded. " + EMCEngine.size() + " items mapped.");
                return true;
            }
            case "list" -> {
                java.util.Map<Material, Long> vals = EMCEngine.getAll();
                StringBuilder sb = new StringBuilder("=== EMC Values (").append(vals.size()).append(" items) ===\n");
                vals.entrySet().stream().sorted(Map.Entry.comparingByValue())
                        .forEach(e -> sb.append(e.getKey().name()).append(" = ").append(e.getValue()).append("\n"));
                writeToFile("emc_list.txt", sb.toString());
                sender.sendMessage("§aWrote " + vals.size() + " values to emc_list.txt");
                return true;
            }
            case "trace" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /emc trace <material>");
                    return true;
                }
                Material mat = Material.getMaterial(args[1].toUpperCase());
                if (mat == null) {
                    sender.sendMessage("§cUnknown material: " + args[1]);
                    return true;
                }
                String result = EMCEngine.traceRecipes(mat);
                sender.sendMessage(result);
                return true;
            }
            case "missingroots" -> {
                String result = EMCEngine.traceMissingRoots();
                writeToFile("emc_missingroots.txt", result);
                sender.sendMessage("§aWrote missing-item root analysis to emc_missingroots.txt");
                return true;
            }
            case "listempty" -> {
                java.util.List<Material> empty = new java.util.ArrayList<>();
                for (Material mat : Material.values()) {
                    if (!mat.isItem() || EMCEngine.has(mat)) continue;
                    String name = mat.name();
                    if (name.contains("SPAWN_EGG") || name.contains("POTTERY_SHERD") || name.contains("BANNER_PATTERN")) continue;
                    empty.add(mat);
                }
                StringBuilder sb = new StringBuilder("=== Missing EMC (").append(empty.size()).append(" items) ===\n");
                empty.forEach(m -> sb.append(m.name()).append("\n"));
                writeToFile("emc_listempty.txt", sb.toString());
                sender.sendMessage("§aWrote " + empty.size() + " items to emc_listempty.txt");
                return true;
            }
            case "set" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can use this command.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /emc set <value>");
                    return true;
                }
                ItemStack held = player.getInventory().getItemInMainHand();
                if (held.getType().isAir()) {
                    sender.sendMessage("§cYou must hold an item in your hand.");
                    return true;
                }
                try {
                    long value = Long.parseLong(args[1]);
                    EMCEngine.addBaseValue(held.getType(), value);
                    sender.sendMessage("§aSet " + held.getType().name() + " base EMC = " + value + ". Run /emc reload.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid number: " + args[1]);
                }
                return true;
            }
            default -> {
                sender.sendMessage("§cUse /emc get | add <n> | remove <n> | reload | list | listempty | set <value> | trace <mat> | missingroots");
                return true;
            }
        }
    }

    private boolean giveItem(CommandSender sender, ItemStack item, String displayName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        player.getInventory().addItem(item);
        player.sendMessage("§aHere's your " + displayName + "!");
        return true;
    }

    private void registerRecipes() {
        ItemStack[] pistolIngredients = new ItemStack[9];
        pistolIngredients[1] = new ItemStack(Material.IRON_NUGGET);
        pistolIngredients[3] = new ItemStack(Material.IRON_NUGGET);
        pistolIngredients[4] = new ItemStack(Material.IRON_HOE);
        pistolIngredients[5] = new ItemStack(Material.IRON_NUGGET);
        pistolIngredients[7] = new ItemStack(Material.IRON_NUGGET);

        ShapedRecipe pistolRecipe = new ShapedRecipe(
                new NamespacedKey(this, "pistol"),
                Pistol.create()
        );
        pistolRecipe.shape(" I ", "IHI", " I ");
        pistolRecipe.setIngredient('I', Material.IRON_NUGGET);
        pistolRecipe.setIngredient('H', Material.IRON_HOE);
        getServer().addRecipe(pistolRecipe);
        RecipeRegistry.register(new RecipeInfo("Pistol", Pistol.create(), pistolIngredients));

        ItemStack[] dummyIngredients = new ItemStack[9];
        dummyIngredients[1] = new ItemStack(Material.HAY_BLOCK);
        dummyIngredients[3] = new ItemStack(Material.HAY_BLOCK);
        dummyIngredients[4] = new ItemStack(Material.TARGET);
        dummyIngredients[5] = new ItemStack(Material.HAY_BLOCK);
        dummyIngredients[7] = new ItemStack(Material.HAY_BLOCK);

        ShapedRecipe dummyRecipe = new ShapedRecipe(
                new NamespacedKey(this, "target_dummy"),
                TargetDummyItem.create()
        );
        dummyRecipe.shape(" H ", "HTH", " H ");
        dummyRecipe.setIngredient('H', Material.HAY_BLOCK);
        dummyRecipe.setIngredient('T', Material.TARGET);
        getServer().addRecipe(dummyRecipe);
        RecipeRegistry.register(new RecipeInfo("Target Dummy", TargetDummyItem.create(), dummyIngredients));

        ItemStack[] heavyStickIngredients = new ItemStack[9];
        heavyStickIngredients[1] = new ItemStack(Material.IRON_INGOT);
        heavyStickIngredients[4] = new ItemStack(Material.IRON_INGOT);

        ShapedRecipe heavyStickRecipe = new ShapedRecipe(
                new NamespacedKey(this, "heavy_stick"),
                HeavyStick.create()
        );
        heavyStickRecipe.shape("I", "I");
        heavyStickRecipe.setIngredient('I', Material.IRON_INGOT);
        getServer().addRecipe(heavyStickRecipe);
        RecipeRegistry.register(new RecipeInfo("Heavy Stick", HeavyStick.create(), heavyStickIngredients));

        ItemStack stickyPiston1 = new ItemStack(Material.STICKY_PISTON);
        ShapelessRecipe honeyBottleRecipe = new ShapelessRecipe(
                new NamespacedKey(this, "sticky_piston_honey_bottle"),
                stickyPiston1
        );
        honeyBottleRecipe.addIngredient(Material.PISTON);
        honeyBottleRecipe.addIngredient(Material.HONEY_BOTTLE);
        getServer().addRecipe(honeyBottleRecipe);

        ItemStack[] honeyBottleDisplay = new ItemStack[9];
        honeyBottleDisplay[0] = new ItemStack(Material.PISTON);
        honeyBottleDisplay[1] = new ItemStack(Material.HONEY_BOTTLE);
        RecipeRegistry.register(new RecipeInfo("Sticky Piston - Honey Bottle", stickyPiston1.clone(), honeyBottleDisplay));

        ItemStack stickyPiston4 = new ItemStack(Material.STICKY_PISTON, 4);
        ShapelessRecipe honeyBlockRecipe = new ShapelessRecipe(
                new NamespacedKey(this, "sticky_piston_honey_block"),
                stickyPiston4
        );
        honeyBlockRecipe.addIngredient(Material.HONEY_BLOCK);
        honeyBlockRecipe.addIngredient(4, Material.PISTON);
        getServer().addRecipe(honeyBlockRecipe);

        ItemStack[] honeyBlockDisplay = new ItemStack[9];
        honeyBlockDisplay[0] = new ItemStack(Material.HONEY_BLOCK);
        honeyBlockDisplay[1] = new ItemStack(Material.PISTON);
        honeyBlockDisplay[2] = new ItemStack(Material.PISTON);
        honeyBlockDisplay[3] = new ItemStack(Material.PISTON);
        honeyBlockDisplay[4] = new ItemStack(Material.PISTON);
        RecipeRegistry.register(new RecipeInfo("Sticky Piston - Honey Block", stickyPiston4.clone(), honeyBlockDisplay));

        ItemStack[] ammoBoxIngredients = new ItemStack[9];
        ammoBoxIngredients[0] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[1] = new ItemStack(Material.BUNDLE);
        ammoBoxIngredients[2] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[3] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[4] = new ItemStack(Material.CHEST);
        ammoBoxIngredients[5] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[6] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[7] = new ItemStack(Material.IRON_BLOCK);
        ammoBoxIngredients[8] = new ItemStack(Material.IRON_BLOCK);

        ShapedRecipe ammoBoxRecipe = new ShapedRecipe(
                new NamespacedKey(this, "ammo_box"),
                AmmoBox.create()
        );
        ammoBoxRecipe.shape("IBI", "ICI", "III");
        ammoBoxRecipe.setIngredient('I', Material.IRON_BLOCK);
        ammoBoxRecipe.setIngredient('B', Material.BUNDLE);
        ammoBoxRecipe.setIngredient('C', Material.CHEST);
        getServer().addRecipe(ammoBoxRecipe);
        RecipeRegistry.register(new RecipeInfo("Ammo Box", AmmoBox.create(), ammoBoxIngredients));

        ItemStack[] rifleIngredients = new ItemStack[9];
        rifleIngredients[0] = new ItemStack(Material.IRON_INGOT);
        rifleIngredients[1] = new ItemStack(Material.IRON_BLOCK);
        rifleIngredients[2] = new ItemStack(Material.IRON_INGOT);
        rifleIngredients[3] = new ItemStack(Material.IRON_BLOCK);
        rifleIngredients[4] = new ItemStack(Material.IRON_HOE);
        rifleIngredients[5] = new ItemStack(Material.IRON_BLOCK);
        rifleIngredients[6] = new ItemStack(Material.IRON_INGOT);
        rifleIngredients[7] = new ItemStack(Material.IRON_BLOCK);
        rifleIngredients[8] = new ItemStack(Material.IRON_INGOT);

        ShapedRecipe rifleRecipe = new ShapedRecipe(
                new NamespacedKey(this, "rifle"),
                Rifle.create()
        );
        rifleRecipe.shape("IBI", "BHB", "IBI");
        rifleRecipe.setIngredient('I', Material.IRON_INGOT);
        rifleRecipe.setIngredient('B', Material.IRON_BLOCK);
        rifleRecipe.setIngredient('H', Material.IRON_HOE);
        getServer().addRecipe(rifleRecipe);
        RecipeRegistry.register(new RecipeInfo("Rifle", Rifle.create(), rifleIngredients));

        ShapedRecipe transmutationRecipe = new ShapedRecipe(
                new NamespacedKey(this, "transmutation_table"),
                TransmutationBlock.createItem()
        );
        transmutationRecipe.shape("NRN", "RSR", "NRN");
        transmutationRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        transmutationRecipe.setIngredient('R', Material.REDSTONE);
        transmutationRecipe.setIngredient('S', Material.POLISHED_BLACKSTONE_SLAB);
        getServer().addRecipe(transmutationRecipe);

        ItemStack[] transIngredients = new ItemStack[9];
        transIngredients[0] = new ItemStack(Material.NETHERITE_INGOT);
        transIngredients[2] = new ItemStack(Material.NETHERITE_INGOT);
        transIngredients[4] = new ItemStack(Material.POLISHED_BLACKSTONE_SLAB);
        transIngredients[6] = new ItemStack(Material.NETHERITE_INGOT);
        transIngredients[8] = new ItemStack(Material.NETHERITE_INGOT);
        transIngredients[1] = new ItemStack(Material.REDSTONE);
        transIngredients[3] = new ItemStack(Material.REDSTONE);
        transIngredients[5] = new ItemStack(Material.REDSTONE);
        transIngredients[7] = new ItemStack(Material.REDSTONE);
        RecipeRegistry.register(new RecipeInfo("Transmutation Table", TransmutationBlock.createItem(), transIngredients));
    }

    private void updateAmmoScoreboards() {
        for (Player player : getServer().getOnlinePlayers()) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            boolean holdingGun = Pistol.isGun(mainHand) || Pistol.isGun(offHand)
                    || Rifle.isRifle(mainHand) || Rifle.isRifle(offHand);

            if (holdingGun) {
                int iron = countTotalAmmo(player, Material.IRON_NUGGET);
                int gold = countTotalAmmo(player, Material.GOLD_NUGGET);
                UUID uuid = player.getUniqueId();
                int[] last = lastAmmoCounts.get(uuid);

                if (last != null && last[0] == iron && last[1] == gold) continue;
                lastAmmoCounts.put(uuid, new int[]{iron, gold});

                Scoreboard board = player.getScoreboard();
                Objective obj = board.getObjective("ammo");
                if (obj == null) {
                    board = Bukkit.getScoreboardManager().getNewScoreboard();
                    obj = board.registerNewObjective("ammo", Criteria.DUMMY, ChatColor.GOLD + "  Ammo  ");
                    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                    player.setScoreboard(board);
                    last = null;
                }

                if (last != null) {
                    board.resetScores(ChatColor.GRAY + "Iron: " + last[0]);
                    board.resetScores(ChatColor.YELLOW + "Gold: " + last[1]);
                }

                obj.getScore(ChatColor.GRAY + "Iron: " + iron).setScore(1);
                obj.getScore(ChatColor.YELLOW + "Gold: " + gold).setScore(0);
            } else {
                Scoreboard board = player.getScoreboard();
                if (board.getObjective("ammo") != null) {
                    lastAmmoCounts.remove(player.getUniqueId());
                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                }
            }
        }
    }

    private int countTotalAmmo(Player player, Material type) {
        int count = 0;
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < 36; i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == type) {
                count += item.getAmount();
            }
            if (AmmoBox.isAmmoBox(item)) {
                count += AmmoBox.countAmmo(item, type);
            }
        }
        return count;
    }

    private void updateEmcTabList() {
        for (Player player : getServer().getOnlinePlayers()) {
            double emc = playerDataManager.get(player.getUniqueId()).emc();
            player.setPlayerListHeader(ChatColor.GOLD + "EMC: " + ChatColor.WHITE + EMCEngine.formatEmc(emc) + "\n");
        }
    }

    private void writeToFile(String filename, String content) {
        java.io.File file = new java.io.File(getDataFolder(), filename);
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        } catch (IOException e) {
            getLogger().warning("Failed to write " + filename + ": " + e.getMessage());
        }
    }
}
