# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.15.2`) targets **Minecraft 1.15.2** with **Forge** and **Mekanism 9.10.x** (developed and tested with 9.10.9).

Mekanism's Speed, Energy and Gas Upgrades are capped at 8 each. This mod lifts that cap, keeps the effect of every 8 upgrades as it is, lets machines do several operations in one tick when they are fast enough, and lets the effect of surplus Energy Upgrades decay against the Speed Upgrades, so more upgrades really mean more performance *and* more consumption.

**No extra library is needed** — Forge already ships Mixin. Apart from Mekanism the mod requires nothing.

## Requirements

- Minecraft 1.15.2 with Forge 31
- Mekanism 9.10.x for 1.15.2

## Features

- **Configurable upgrade limits.** The maximum number of Speed, Energy and Gas Upgrades a machine can hold is set in the config (64 each by default). Mekanism itself installs the whole stack in the upgrade slot at once.
- **Same effect per 8 upgrades.** Mekanism divides the number of upgrades by the maximum to get the effect, so raising the maximum alone would weaken every upgrade. This mod keeps 8 upgrades as one unit of effect (the base is Mekanism's own `UpgradeModifier`).
- **Several operations per tick.** When the time per operation drops below one tick, ordinary recipe machines and Factories (Enrichment Chamber, Crusher, Metallurgic Infuser, all tiers of Factories, ...) do the rest of the operations in the same tick, up to `maxMachineOperations`. The progress bar shows full while a machine runs this fast. Every extra operation costs the energy of one tick.
- **Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator.** Same for them, up to `maxMinerOperations`, `maxPumpOperations`, `maxPlenisherOperations` and `maxAssemblicatorOperations`.
- **Digital Miner and Muffling.** The Digital Miner accepts Muffling Upgrades; fully muffled, its block break effect makes no sound.
- **Energy Upgrades and the energy cost.** The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but only up to the Speed Upgrades (at least `freeEnergy`): the effect of Energy Upgrades beyond that decays (see `sustEnergy`). Gas Upgrades work the same way for the gas consumption.
- **Effect display.** The upgrade screen shows the effect that is really applied, in exponential notation.

**Note:** Mekanism 10.0.x has no warning tab yet (it was added in 10.1.0), so this version does not show the "Insert Energy Upgrades!" warning. Without enough Energy Upgrades the energy needed per tick can exceed what a machine can store, and it stops working: install Energy Upgrades along with the Speed Upgrades.

## Config

`config/mekanismtweaks-common.toml`:

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `maxGas` | 64 | Maximum Gas Upgrades installed. `2147483647` means unlimited. |
| `freeEnergy` | 8 | Minimum number of Energy Upgrades whose effect never decays. |
| `freeGas` | 8 | Minimum number of Gas Upgrades whose effect never decays. |
| `sustEnergy` | 0.5 | How much of the effect of the Energy Upgrades beyond the Speed Upgrades is sustained. At 1 nothing decays (as in vanilla Mekanism); at 0 the surplus has no effect. At 0.5, overcoming the Speed Upgrades needs Energy Upgrades of at least their square. |
| `sustGas` | 0.5 | Same as `sustEnergy`, for the Gas Upgrades. |
| `maxMachineOperations` | 64 | The most operations an ordinary recipe machine does in one tick. `1` turns this off, as per vanilla Mekanism. |
| `maxMinerOperations` | 64 | The most blocks a Digital Miner mines in one tick. `1` turns this off. |
| `maxPumpOperations` | 64 | The most times an Electric Pump pumps in one tick. `1` turns this off. |
| `maxPlenisherOperations` | 64 | The most times a Fluidic Plenisher plenishes in one tick. `1` turns this off. |
| `maxAssemblicatorOperations` | 64 | The most crafts a Formulaic Assemblicator does in one tick. `1` turns this off. |

## Building

Requires JDK 8 (the game and the compiler) and JDK 17 to run Gradle (ForgeGradle 5.1 with Gradle 7.6).

- `./gradlew build` — the jar is written to `build/libs/`. Mekanism 9.10.9 is downloaded from CurseMaven (CurseForge file 2996531) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

## Other versions

Every Minecraft version has its own branch, named after the Minecraft version: `1.21.1`, `1.20.4`, `1.20.1`, `1.19.2`, `1.19.1`, `1.16.4`, `1.16.3`, `1.16.1`, `1.15.2`, `1.12.2`, `1.12.1`, `1.12` (Mekanism 9.4 - 9.8), `1.11.2` and `1.10.2` (Mekanism 9.x). The `1.7.10-7.1.2` and `1.7.10-9.1.x` branches target Mekanism 7.1.2 and 9.1.x for Minecraft 1.7.10. The `1.16.5`, `1.18.2` and `master` branches keep the older implementations.

## License

See [LICENSE](LICENSE).
