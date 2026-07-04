# Bullshit Additions

A PaperMC plugin adding custom weapons, items, and utilities.

## Items

### Pistol
Stone hoe model. Right-click to fire iron nuggets. Consumes iron nuggets from inventory or ammo box.
- **Damage**: 5.0
- **Recipe**: Iron nuggets in cross around iron hoe
- **Command**: `/pistol` (permission: `bullshitadditions.pistol`, default: op)

### Rifle
Iron hoe model. Right-click to fire gold nuggets. Low cooldown for rapid fire.
- **Damage**: 6.0
- **Recipe**: Iron blocks on edges, iron ingots in corners, iron hoe in center
- **Command**: `/rifle` (permission: `bullshitadditions.rifle`, default: op)

### Heavy Stick
Stick model. Hits teleport the target 2 blocks downward (into the ground for suffocation) with no knockback. Has 16 durability tracked in lore.
- **Recipe**: 2 iron ingots stacked vertically (stick recipe shape)
- **Command**: `/heavystick` (permission: `bullshitadditions.heavystick`, default: op)

### Ammo Box
Ender chest model. Cannot be placed. Right-click opens a 3x3 inventory that only accepts iron and gold nuggets. Guns automatically pull ammo from ammo boxes in your inventory.
- **Recipe**: Chest (center), Bundle (top), 7 Iron Blocks everywhere else
- **Command**: `/ammobox` (permission: `bullshitadditions.ammobox`, default: op)

### Target Dummy
Placable item that spawns an invincible armor stand with a target block head and leather armor. Hit it to see your damage dealt in the action bar. Remove it by hitting with a dirt block.
- **Recipe**: Hay bales in cross around target block
- **Command**: `/targetdummy` (permission: `bullshitadditions.targetdummy`, default: op)

### Recipes
#### Custom
- **Sticky Piston (Honey Bottle)**: 1 piston + 1 honey bottle (shapeless)
- **Sticky Piston (Honey Block)**: 1 honey block + 4 pistons (shapeless)

## Commands
| Command | Permission | Default |
|---|---|---|
| `/recipes` | `bullshitadditions.recipes` | Everyone |
| `/pistol`, `/ironbargun` | `bullshitadditions.pistol` | OP |
| `/rifle` | `bullshitadditions.rifle` | OP |
| `/heavystick` | `bullshitadditions.heavystick` | OP |
| `/ammobox` | `bullshitadditions.ammobox` | OP |
| `/targetdummy` | `bullshitadditions.targetdummy` | OP |

## Ammo Scoreboard
While holding a Pistol or Rifle, a sidebar displays live counts of iron and gold nuggets across your inventory and ammo boxes.

## Recipe GUI
`/recipes` opens a double-chest overview of all custom items. Click any item to see its recipe in a scrollable detail view. A crafting table icon returns to the overview.

## Permissions
All permissions work with LuckPerms. Grant all with `bullshitadditions.*`.

## Building
```bash
./gradlew build
```
The plugin JAR will be in `build/libs/`.

## Requirements
- PaperMC 1.21.4+
- Java 26
