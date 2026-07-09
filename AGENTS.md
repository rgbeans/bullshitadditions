# Bullshit Additions — PaperMC Plugin

PaperMC 1.21.4 plugin adding EMC transmutation, guns, custom items, and energy automation.

## Commands

```bash
.\gradlew build            # Compile + test, JAR at build\libs\bullshitadditions-1.0-SNAPSHOT.jar
.\gradlew test             # Run all tests
.\gradlew test --tests "com.rgbeans.bullshit.bullshitadditions.EMCEngineTest.testWoodChain"  # Single test
.\gradlew runServer        # PaperMC 26.1.2 with 2GB RAM, plugin auto-deployed
```

## Build gotchas

- **Java toolchain is 25** (`build.gradle.kts:27`). The README says Java 26 — the toolchain wins. Use JDK 25.
- **Configuration cache is ON** (`gradle.properties`). After changing `build.gradle.kts`, run `.\gradlew clean build --no-configuration-cache` if the cache goes stale.
- **`plugin.yml` version** is template-expanded from `gradle.properties` via `processResources`. Don't remove `${version}`.
- **Paper API uses wildcard** `26.1.2.build.+`. The `runServer` task pins `26.1.2` exactly.

## Git & Release workflow

No CI — everything is manual.

```bash
# 1. Feature branch
git checkout -b feature/<description>

# 2. Commit
git add -A
git commit -m "<title>" -m "<bullet 1>" -m "<bullet 2>"

# 3. Push + PR
git push -u origin feature/<description>
gh pr create --base main --head feature/<description> --title "..." --body "..."

# 4. Merge
gh pr merge <number> --merge --delete-branch

# 5. Create GitHub Release with JAR
git checkout main; git pull
gh release create vX.Y --title "vX.Y - <summary>" --notes "<changelog>" build\libs\bullshitadditions-1.0-SNAPSHOT.jar
```

- **Version tags**: semantic `vX.Y` (currently v1.2).
- **Branch naming**: `feature/<description>` for features.
- **Commit style**: single-line title, multi-line body with `-` bullets.

## Tests

- Single test file: `src/test/java/.../EMCEngineTest.java`, JUnit 5, 16 tests.
- Tests do NOT use a Bukkit server. They call `EMCEngine.recalculate(Logger, List<InternalRecipe>)` with synthetic recipes.
- **Every assertion is an exact `long` EMC value.** Changing any base value in `EMCBaseValues.java` or algorithm in `EMCEngine.java` WILL break tests. Run `.\gradlew test` after any EMC change and update expected values as needed.
- The `printReport()` test dumps all computed EMC values to stdout — useful when debugging.

## Architecture

Package: `com.rgbeans.bullshit.bullshitadditions`

### Core systems

| Class | Role |
|---|---|
| `Bullshitadditions.java` | Plugin entrypoint: registers recipes, commands, listeners, starts scheduler tasks |
| `EMCEngine.java` | EMC calculation (iterative propagation over all Bukkit recipes, up to 50 passes) |
| `EMCBaseValues.java` | 482 hardcoded base EMC values in a `LinkedHashMap` |
| `TransmutationTable.java` | Burn/buy GUI with paging. Also holds the `TransmutableItem` registry for plugin custom items |
| `PlayerDataManager.java` | Per-player YAML persistence (`plugins/bullshitadditions/playerdata/<uuid>.yml`) |
| `PlayerData.java` | EMC balance + `learned` materials + `learnedCustom` string keys |
| `EnergyCondenser.java` | Placed barrel: ticks every 6 ticks, consumes items for EMC, produces target item |
| `InternalRecipe.java` | Sealed interface (Shaped/Shapeless/Cooking/Stonecutting/Smithing) — **test-only** |

### Custom item pattern

Every custom item follows this pattern:
```java
public final class Foo {
    static final NamespacedKey KEY = new NamespacedKey("bullshitadditions", "foo");

    public static ItemStack create() { /* set PDC, display name, lore, etc */ }
    public static boolean isItem(ItemStack item) { /* check PDC */ }
}
```
- Items use vanilla materials (stone hoe, stick, clay ball, etc.) distinguished by `PersistentDataContainer` with `PersistentDataType.BOOLEAN`.
- Guns (Pistol, Rifle) are **deliberately excluded** from transmutation.
- To make a new item transmutable: create the item class, add a recipe, then call `TransmutationTable.registerTransmutable(...)` in `Bullshitadditions.registerTransmutableItems()` after EMC recalc.

### Key conventions

- **Listener registration**: in `Bullshitadditions.onEnable()` via `getServer().getPluginManager().registerEvents()`
- **Recipe registration**: in `registerRecipes()` using `NamespacedKey(this, "name")`
- **Command permissions**: declared in `plugin.yml`; `/emc add/remove` additionally checks `sender.isOp()`
- **Scheduler tasks**: ammo scoreboard @20t, EMC tab list @100t, condenser processor @6t
- **Custom meta blocking**: `hasCustomMeta()` rejects items with display name, lore, or non-empty PDC from vanilla EMC systems — transmutable custom items are checked against the registry BEFORE this gate

## Submodule

`paper-docs/` is a git submodule (`https://github.com/PaperMC/docs.git`). Don't modify it. Run `git submodule update --init` if missing.
