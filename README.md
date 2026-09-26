# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.7.10-9.1.x`) targets **Minecraft 1.7.10** and **Mekanism 9.1.x** (tested with 9.1.0.281 and 9.1.1.1031). Mekanism 7.x is not supported here; see the `1.7.10-7.1.2` branch for it.

Mekanism's upgrades are capped at 8 (Speed, Energy, Gas). This mod lifts that cap, lets machines run faster than one operation per tick, and makes the energy cost grow with the speed, so more upgrades really mean more performance *and* more consumption.

**No Mixin library is needed.** The mod is a plain core mod (ASM transformer); apart from Mekanism (and Mekanism's own dependencies) it requires nothing.

## Requirements

- Minecraft 1.7.10 with Forge 10.13.4.1614
- Mekanism 9.1.x for 1.7.10 (and its own dependencies, e.g. ForgeMultipart)

## Features

- **Configurable upgrade limits.** The maximum number of Speed, Energy, Gas and Muffling Upgrades a machine can hold is set in the config (64, 64, 64 and 4 by default). The upgrade items' stack size follows it (never above 64).
- **Bulk install.** The whole stack in the upgrade slot is installed at once, up to the limit, instead of one upgrade every 40 ticks.
- **Beyond one operation per tick.** When the required time drops below one tick, the excess progress turns into extra operations in the same tick (one extra operation per 20 excess progress). The progress bar shows full while a machine runs this fast.
- **Digital Miner.** Mekanism's Digital Miner mines one block per tick at the most, however many Speed Upgrades it has. When the time per block drops below one tick, it now mines several blocks in the same tick, up to `maxMinerOperations`. Every extra block costs the energy of one tick.
- **Electric Pump, Fluidic Plenisher and Formulaic Assemblicator.** Same for them: when the time per operation drops below one tick, they operate several times in the same tick, up to `maxPumpOperations`, `maxPlenisherOperations` and `maxAssemblicatorOperations`. The progress bar of the Assemblicator keeps working.
- **Digital Miner and Muffling.** The Digital Miner accepts Muffling Upgrades; fully muffled, its block break effects make no sound.
- **Energy costs scale with speed.** The energy per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but the effect of Energy Upgrades beyond the Speed Upgrades decays (see `freeEnergy` and `sustEnergy`). Gas Upgrades work the same way for the gas consumption (`freeGas`, `sustGas`).
- **Warning icon.** When a machine has more than 10 more Speed Upgrades than Energy Upgrades, a blinking yellow warning icon appears under the redstone control tab of its GUI. Hover it: *Insert Energy Upgrades!* Without enough Energy Upgrades the energy needed per tick can exceed what the machine can store, and it stops working.

Supported machines: electric machines (Enrichment Chamber, Crusher, ...), advanced electric machines (Purification Chamber, Chemical Injection Chamber, ...), chance machines, Pressurized Reaction Chamber, Metallurgic Infuser, Chemical Oxidizer, Chemical Dissolution Chamber, Chemical Crystallizer, Factories (all tiers), Factories (all tiers), Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator. Other machines get the upgrade limits and the effect scaling, but never take less than one tick per operation.

## Config

`config/mekanismtweaks.cfg` (restart the game after changing it, since it decides the stack size of the upgrade items):

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `maxGas` | 64 | Maximum Gas Upgrades installed. `2147483647` means unlimited. |
| `maxMuffling` | 4 | Maximum Muffling Upgrades installed. |
| `bulkInstall` | true | Install the whole stack in the upgrade slot at once. |
| `maxMinerOperations` | 64 | The most blocks a Digital Miner mines in one tick. `1` turns this off, as per vanilla Mekanism. |
| `maxPumpOperations` | 64 | The most times an Electric Pump pumps in one tick. `1` turns this off, as per vanilla Mekanism. |
| `maxPlenisherOperations` | 64 | The most times a Fluidic Plenisher plenishes in one tick. `1` turns this off, as per vanilla Mekanism. |
| `maxAssemblicatorOperations` | 64 | The most crafts a Formulaic Assemblicator does in one tick. `1` turns this off, as per vanilla Mekanism. |
| `energyBuffer` | true | Avoid an excessive energy buffer: the buffer increment of surplus Energy Upgrades decays like their saving effect. |
| `freeEnergy` / `freeGas` | 8 | Minimum number of Energy / Gas Upgrades whose effect never decays. |
| `sustEnergy` / `sustGas` | 0.5 | How much of the Energy / Gas Upgrades' effect is sustained against the Speed Upgrades. At 1 nothing decays (as in vanilla Mekanism); at 0 the effect decays fully. At 0.5, overcoming the Speed Upgrades needs upgrades of at least their square. |

The effect per 8 upgrades is Mekanism's own `UpgradeModifier` (`mekanism.cfg`).

## Building

Requires JDK 25 to run Gradle (the build compiles for Java 8).

1. Download `Mekanism-1.7.10-9.1.1.1031.jar` from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mekanism/files/2475797) and put it in `libs/`.
2. `./gradlew build` — the jar is written to `build/libs/`.
3. `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

`libs/compile-api-stubs.jar` only holds empty interfaces of the optional IC2, CoFH and ComputerCraft APIs that Mekanism's classes implement, so that the classes can be compiled against.

## How it works

`asm/MekanismTweaksTransformer` changes a few Mekanism classes while they are loaded: it replaces some method bodies or inserts calls to `Hooks` / `ClientHooks`, where the actual logic lives. Only such calls without any branch are inserted, so the existing stack map frames stay valid.

## Other versions

The `1.7.10-7.1.2` branch targets Mekanism 7.1.2 for 1.7.10 (with Mixin). The `1.12.2`, `1.16.5`, `1.18.2` and `master` branches target other Minecraft and Mekanism versions.

## License

See [LICENSE](LICENSE).
