# Fuller Flowers

Turn single flowers into full, lush clusters, right where they grow.

Fuller Flowers lets the small vanilla flowers grow into clusters: several flowers of the same kind in one block. Use bone meal on a flower, or place the same flower on it, and the block gains a flower, up to 4 by default. Flower pots fill up the same way. Breaking a cluster gives back every flower in it.

The mod adds blocks, but no items: every cluster is made of and drops the normal vanilla flowers. It is needed on both the server and the client.

Available for NeoForge, Forge and Fabric.

## Features

- **Flower clusters.** Up to 4 flowers of the same kind in one block by default, configurable up to 8, for all 14 small vanilla flowers.
- **Bone meal grows them.** Bone meal on a flower makes a cluster of 2 and adds one flower per use. A full cluster drops a flower instead, like the tall flowers do.
- **Place to add.** Placing the same flower on a flower or cluster adds it to the block, like pink petals. Sneak to place it next to the flower as usual.
- **Fuller flower pots.** A potted flower takes more of the same flower, up to 3 by default, and gives them back one by one.
- **Nothing gets lost.** Breaking a cluster drops all of its flowers, with any tool or by hand.
- **Vanilla look.** Clusters use the game's own flower textures, so they follow your resource pack. Each size has its own arrangement, turned a different way at every spot, and a replanted cluster looks the same as before.
- **Same flower, same behavior.** A cluster sits on the same soil, sounds the same, breaks instantly, burns and attracts bees like its flower. A wither rose cluster withers whatever walks into it.
- **Dispensers work.** Dispensers with bone meal grow flowers and clusters like a player does.
- **Server config.** Change the limit for all flowers or for single flowers, and the pot limit, in one small TOML file.

## How it works

| Action | Result |
| --- | --- |
| Bone meal on a supported flower | It becomes a cluster of 2. |
| Bone meal on a cluster that is not full | One flower more. |
| Bone meal on a full cluster | One flower drops; the cluster stays as it is. |
| Placing the same flower on a flower or a cluster that is not full | One flower more. Sneaking places the flower normally. |
| Breaking a cluster | All of its flowers drop. |
| Using the same flower on a potted flower or a pot that is not full | One flower more in the pot. |
| Using an empty hand on a pot with several flowers | One flower comes out. With 2 flowers, the normal potted flower is left. |
| Breaking a pot with several flowers | The flower pot and all of its flowers drop. |

- **Supported flowers.** Dandelion, poppy, blue orchid, allium, azure bluet, red tulip, orange tulip, white tulip, pink tulip, oxeye daisy, cornflower, lily of the valley, torchflower and wither rose. Tall flowers, pink petals and flowers from other mods are not changed.
- **One flower is the vanilla flower.** A block with one flower is the normal vanilla flower, and a pot with one flower is the normal vanilla potted flower. Clusters start at 2.
- **Limits.** Each flower has a limit, 4 by default and up to 8. A pot holds up to 3 flowers by default and never more than 4, and never more than the limit of its flower. A flower with a limit of 1 behaves exactly like vanilla: bone meal does nothing on it and placing the same flower does not stack.
- **Bone meal is only used when something happens.** Bone meal is used up, with the usual particles, only when it adds a flower or drops one.
- **Different flowers do not mix.** A poppy placed on a dandelion is placed next to it as usual.
- **Explosions.** Clusters lose flowers in explosions like other plants do. Pots follow the vanilla flower pot.
- **Pick block** on a cluster or a pot gives the flower.
- **Pots and other items.** On a pot with several flowers, another plant that fits in a flower pot does nothing, and any other item takes a flower out, like on a vanilla potted flower.
- **Bees and endermen.** Bees pollinate clusters like single flowers. Endermen do not pick clusters up.
- **Lower limits later.** When a limit is lowered, existing clusters and pots keep their flowers and still drop all of them, but they cannot grow further. Bone meal on a cluster at or above its limit drops a flower.

## Requirements

| | NeoForge | Forge | Fabric |
|---|---|---|---|
| Minecraft | 1.21.1 | 1.21.1 | 1.21.1 |
| Loader | NeoForge 21.1.1 or newer | Forge 52.0.0 or newer | Fabric Loader 0.15.11 or newer |
| Other | | | Fabric API 0.102.0 or newer |
| Jar | `fullerflowers-neoforge-1.21.1-<version>.jar` | `fullerflowers-forge-1.21.1-<version>.jar` | `fullerflowers-fabric-1.21.1-<version>.jar` |

On Minecraft 1.21.1, NeoForge and Forge are separate loaders that do not run each other's mods, so each has its own jar. Pick the jar that matches your loader.

The Fabric jar bundles [NightConfig](https://github.com/TheElectronWill/night-config) (core and toml, LGPL-3.0) to read the TOML config. NeoForge and Forge already ship it.

### Client and server

Install the mod on the server and on every client. The clusters are new blocks, so a client needs the mod to know them:

- **NeoForge, Forge and Fabric:** a client with the loader but without the mod cannot join a server that has it; the loader refuses it and lists what is missing.
- **Vanilla clients** can connect, but are disconnected with a network error as soon as a cluster comes into view.
- **Forge:** the server list shows the server as incompatible for a client without the mod.

The config lives on the server only; clients do not need it.

In singleplayer, install it in your own game.

## Configuration

### Server: `config/fullerflowers-server.toml`

The file is created with comments on the first server start, or in singleplayer the first time a world is opened. It is read every time a server or world starts; restart the server or reopen the world after changing it. The file is never sent to clients. The options and their defaults:

```toml
# Most flowers one block can hold, a whole number from 1 to 8. Applies to every supported flower.
# 1 means flowers never grow into clusters.
maxFlowers = 4

# Per-flower values that replace maxFlowers for the flowers listed, for example
# ["minecraft:poppy=5", "minecraft:wither_rose=1"]. Each value is a whole number from 1 to 8.
# Supported flowers: dandelion, poppy, blue_orchid, allium, azure_bluet, red_tulip, orange_tulip,
# white_tulip, pink_tulip, oxeye_daisy, cornflower, lily_of_the_valley, torchflower, wither_rose.
flowerOverrides = []

# Most flowers one flower pot can hold, a whole number from 1 to 4.
# A pot never holds more than the block limit of that flower.
maxPotFlowers = 3
```

- `maxFlowers`: the limit for every supported flower, from 1 to 8. `1` turns clusters off: flowers behave like vanilla.
- `flowerOverrides`: a list of `"flower=limit"` entries that replace `maxFlowers` for the flowers named. `["minecraft:poppy=8", "minecraft:wither_rose=1"]` lets poppies grow to 8 and keeps wither roses single, while every other flower uses `maxFlowers`.
- `maxPotFlowers`: the limit for flower pots, from 1 to 4. A pot holds at most the smaller of `maxPotFlowers` and the limit of its flower; `1` keeps pots vanilla.

Your own values and comments are never rewritten. An invalid `maxFlowers` or `maxPotFlowers` falls back to its default with a warning in the log. An entry of `flowerOverrides` that is not written as `"flower=limit"`, names a flower that is not supported, or has a limit outside 1 to 8 is skipped with a warning; the other entries still apply. A file that cannot be read as TOML is left untouched; the defaults are used and the log says what is wrong.

## Removing the mod

Clusters and pots with several flowers are blocks of this mod. Without the mod, the game removes them: the flowers in them are lost and the spots become empty. Break the clusters and take the extra flowers out of the pots first if you want to keep them. Single flowers and vanilla potted flowers are not affected.

## Compatibility

- **Placing Dispensers:** a dispenser placing a flower in front of a cluster of the same flower that is not full adds the flower to it, like a player does. In front of a single flower of the same kind, Placing Dispensers keeps the flower in the dispenser, as it does for every block that is already there.
- **Ranching Dispensers:** works as is. Dispensing bone meal is vanilla behavior, which Fuller Flowers handles itself.
- **Bone meal events:** on NeoForge and Forge, bone meal used on flowers and clusters still goes through the loader's bone meal event, so mods that block or change bone meal keep working.
- **Resource packs:** clusters and pots use the vanilla flower and flower pot textures and models, so packs that change them change the clusters too.

## Building from source

```
./gradlew build
```

The jars are written to `neoforge/build/libs/`, `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT. See [LICENSE](LICENSE). The Fabric jar includes NightConfig, which is licensed under the LGPL-3.0.
