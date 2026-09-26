# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.12.2`) targets **Minecraft 1.12.2** with **Forge** and **Mekanism 9.8.x** (developed and tested with 9.8.3.390).

Mekanism's Speed, Energy and Gas Upgrades are capped at 8 each. This mod lifts that cap, keeps the effect of every 8 upgrades as it is, lets machines do several operations in one tick when they are fast enough, and lets the effect of surplus Energy Upgrades decay against the Speed Upgrades, so more upgrades really mean more performance *and* more consumption.

**No extra library is needed** — Mixin is bundled in the mod. Apart from Mekanism the mod requires nothing.

## Requirements

- Minecraft 1.12.2 with Forge 14.23.5.2847 (or a later build for the same Minecraft version)
- Mekanism 9.8.x for Minecraft 1.12.2

## Features

- **Configurable upgrade limits.** The maximum number of Speed, Energy, Gas and Muffling Upgrades a machine can hold is set in the config (64, 64, 64 and 4 by default). The stack size of the upgrade items follows it, but never exceeds 64.
- **Same effect per 8 upgrades.** Mekanism divides the number of upgrades by the maximum to get the effect, so raising the maximum alone would weaken every upgrade. This mod keeps 8 upgrades as one unit of effect (the base is Mekanism's own `UpgradeModifier`).
- **Several operations per tick.** When the time per operation drops below one tick, the excess progress turns into extra operations in the same tick (one extra operation per 20 excess progress). The progress bar shows full while a machine runs this fast. Every extra operation costs the energy of one tick.
- **Factories.** Every tier of Factory does the same: a Factory that is faster than one operation per tick per process performs the extra operations of each process in the same tick, as long as it has the energy (and the gas) for them.
- **Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator.** Same for them: several blocks, pump operations, plenish operations or crafts per tick.
- **Energy Upgrades and the energy cost.** The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but only up to the Speed Upgrades (at least `freeEnergy`): the effect of Energy Upgrades beyond that decays (see `sustEnergy`). Gas Upgrades work the same way for the gas consumption (`freeGas`, `sustGas`).
- **Effect display.** The upgrade screen shows the effect that is really applied, in exponential notation.

Supported machines: electric machines (Enrichment Chamber, Crusher, ...), advanced electric machines (Purification Chamber, Chemical Injection Chamber, ...), chance machines, Pressurized Reaction Chamber, Metallurgic Infuser, Chemical Oxidizer, Chemical Dissolution Chamber, Chemical Crystallizer, Factories, Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator. Other machines get the upgrade limits and the effect scaling, but never take less than one tick per operation.

## Config

`config/mekanismtweaks.cfg` (restart the game after changing it, since it decides the stack size of the upgrade items):

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `maxGas` | 64 | Maximum Gas Upgrades installed. `2147483647` means unlimited. |
| `maxMuffling` | 4 | Maximum Muffling Upgrades installed. |
| `energyBuffer` | true | Avoid an excessive energy buffer: the buffer increment of surplus Energy Upgrades decays like their saving effect. |
| `freeEnergy` / `freeGas` | 8 | Minimum number of Energy / Gas Upgrades whose effect never decays. |
| `sustEnergy` / `sustGas` | 0.5 | How much of the Energy / Gas Upgrades' effect is sustained against the Speed Upgrades. At 1 nothing decays (as in vanilla Mekanism); at 0 the effect decays fully. At 0.5, overcoming the Speed Upgrades needs upgrades of at least their square. |

The effect per 8 upgrades is Mekanism's own `UpgradeModifier` (`mekanism.cfg`).

## Building

Requires JDK 8 (ForgeGradle 2.3 with Gradle 4.9).

- `./gradlew build` — the jar is written to `build/libs/`. Mekanism is downloaded from [CurseMaven](https://www.cursemaven.com/) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

`libs/compile-api-stubs.jar` only holds empty interfaces of the optional IC2, CoFH and ComputerCraft APIs that Mekanism's classes implement, so that the classes can be compiled against.

## Other versions

Every Minecraft version has its own branch, named after the Minecraft version: `1.21.1`, `1.20.4`, `1.20.1`, `1.19.2`, `1.19.1`, `1.16.4`, `1.16.3`, `1.16.1`, `1.15.2`, `1.12.2`, `1.12.1`, `1.12` (Mekanism 9.4 - 9.8), `1.11.2` and `1.10.2` (Mekanism 9.x). The `1.7.10-7.1.2` and `1.7.10-9.1.x` branches target Mekanism 7.1.2 and 9.1.x for Minecraft 1.7.10. The `1.16.5`, `1.18.2` and `master` branches keep the older implementations.

## Credits

Based on the original MekanismTweaks for 1.12.2 by nin8995.

## License

See [LICENSE](LICENSE).
