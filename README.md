# Bullshit Additions

A PaperMC plugin adding an EMC (Energy-Matter Currency) system, a transmutation table, custom weapons, items, and utilities.

## Equivalent Exchange (EMC)

Every item has an EMC value representing its material worth. Over 300 items have hand-tuned base values, and thousands more are auto-calculated from crafting/furnace/smithing/stonecutting recipes via dependency propagation.

### Transmutation Table
A GUI for converting items to/from EMC. Right-click the placed table or use `/transmute`.
- **Left-click**: Buy 1 of the selected item with EMC
- **Right-click**: Buy 64
- **Shift-right-click**: Burn 64 of held item for EMC
- **Recipe**: 4 netherite ingots (corners), 4 redstone (edges), polished blackstone slab (center)
- **Commands**: `/transmute` (permission: `bullshitadditions.transmute`, default: op), `/transmutationtable` (gives item, OP only)

### Tab List
Your EMC is displayed at the top of the tab list. Values above 1 billion are abbreviated (e.g. `1.2e9`).

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

## Server settings

Run `/bsconfig` in game as an operator. Click an option to toggle it. For numbers, left-click increases, right-click decreases, Shift changes by five steps, and Q resets the default. Changes take effect immediately and save to `config.yml`.

The menu controls the ammo scoreboard, Gravity Gun pickup of players, Herobrine sightings, minecart speed, and the EMC tab-list display.

Herobrine checks for a visible spot in front of each eligible player once every five minutes by default. If there is a spot, the sighting lasts 10 ticks (0.5 seconds at 20 TPS) and is visible only to that player. The chance, interval, and duration are adjustable.

The Gravity Gun picks up eligible blocks with sneak + right-click. Held blocks can pass through terrain without placing themselves. Right-click again to launch or left-click to release. Released blocks fall and settle normally, or drop as an item when released inside another block.

## Block Placer and Block Breaker

The Block Placer looks like an enchanted dropper. Its storage takes blocks by hand or hopper. It places one block in the empty space it faces. The Block Breaker looks like an enchanted dispenser. It breaks a block instantly with its installed pickaxe and stores the drops in its hopper-accessible output. It waits if the loot won't fit. Both machines skip multi-block structures, and the breaker skips containers and other blocks with stored data.

Right-click for storage or sneak + right-click for controls:

- **Clock** adjusts the interval from 1 to 1,200 ticks. Left-click adds one, right-click subtracts one, and Shift changes the interval by 20 ticks. The default is 20 ticks.
- **Sunflower** burns the EMC-valued item on your cursor. Left-click burns the stack and right-click burns one. Each EMC powers one loaded tick, including time spent waiting for the next interval or an available target. Unloaded machines pause.
- **Pickaxe** on the breaker installs or swaps a pickaxe. It uses vanilla tool drops, including Fortune and Silk Touch. Mending prevents wear and costs one extra EMC per successful break.

Craft either machine with a dropper (placer) or dispenser (breaker) in the center, four iron ingots, three redstone, and a piston (placer) or diamond pickaxe (breaker) at the bottom center. Both recipes appear in `/recipes`.

## Commands
| Command | Permission | Default |
|---|---|---|
| `/blockplacer` | `bullshitadditions.blockplacer` | OP |
| `/blockbreaker` | `bullshitadditions.blockbreaker` | OP |
| `/bsconfig` | `bullshitadditions.config` | OP |
| `/emc get` | `bullshitadditions.emc` | Everyone |
| `/emc reload` | `bullshitadditions.emc` | OP |
| `/emc list` | `bullshitadditions.emc` | OP |
| `/emc listempty` | `bullshitadditions.emc` | OP |
| `/emc set <value>` | `bullshitadditions.emc` | OP |
| `/emc trace <material>` | `bullshitadditions.emc` | OP |
| `/emc missingroots` | `bullshitadditions.emc` | OP |
| `/emc add <amount>` | OP | OP |
| `/emc remove <amount>` | OP | OP |
| `/transmute` | `bullshitadditions.transmute` | OP |
| `/transmutationtable` | `bullshitadditions.transmutationtable` | OP |
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

## Requirements
- PaperMC 1.21.4+
- Java 26
