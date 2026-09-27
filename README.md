# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.16.5`) targets **Minecraft 1.16.5** with **Forge** and **Mekanism 10.1.x** (developed and tested with 10.1.2.457).

Mekanism's Speed and Energy Upgrades are capped at 8 each. This mod lifts that cap to 64, lets machines do several operations in one tick when they are fast enough, and makes the energy cost grow with the speed, so more upgrades really mean more performance *and* more consumption.

**No extra library is needed** — Forge already ships Mixin. Apart from Mekanism the mod requires nothing.

## Requirements

- Minecraft 1.16.5 with Forge 36 or later
- Mekanism 10.1.x for Minecraft 1.16.5

## Features

- **Up to 64 Speed and Energy Upgrades.** The upgrade items stack to 64 and a machine can hold 64 of each.
- **Same effect per 8 upgrades.** Mekanism divides the number of upgrades by the maximum to get the effect, so raising the maximum alone would weaken every upgrade. This mod keeps 8 upgrades as one unit of effect (the base is `maxUpgradeMultiplier` in Mekanism's general config). The time per operation shrinks exponentially with the Speed Upgrades.
- **Several operations per tick.** When the time per operation drops below one tick, machines do the rest of the operations in the same tick: every recipe machine, all tiers of Factories, the Electric Pump, the Fluidic Plenisher, the Formulaic Assemblicator and the Digital Miner (several blocks per tick).
- **Factories.** A Factory that is faster than one operation per tick shows a full progress bar instead of a meaningless (negative) ratio.
- **Energy Upgrades and the energy cost.** The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy, but only up to the number of Speed Upgrades (at least 8); they also enlarge the energy buffer.
- **Effect display.** The upgrade screen shows the effect that is really applied, in exponential notation.

Known issue: with more than 56 Speed Upgrades, some machines can make the server TPS drop. Test it on the machines you use.

## Config

This version has no config file of its own. The effect per 8 upgrades follows `maxUpgradeMultiplier` in Mekanism's general config.

## Building

Requires JDK 8 (ForgeGradle 5).

- `./gradlew jar` — the jar is written to `build/libs/`. Mekanism is downloaded from [ModMaven](https://modmaven.dev/) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

## Other versions

Every Minecraft version has its own branch, named after the Minecraft version: `1.21.1`, `1.20.4`, `1.20.1`, `1.19.2`, `1.19.1`, `1.18.2`, `1.16.5`, `1.16.4`, `1.16.3`, `1.16.1`, `1.15.2`, `1.12.2`, `1.12.1`, `1.12`, `1.11.2` and `1.10.2`. The `1.7.10-7.1.2` and `1.7.10-9.1.x` branches target Mekanism 7.1.2 and 9.1.x for Minecraft 1.7.10. `master` (Minecraft 1.19.2) keeps an older implementation. The newer branches (1.15.2 - 1.21.1 and the 1.7.10 ones) have configurable upgrade limits and more options.

## Credits

Original implementation by nin8995.

## License

See [LICENSE](LICENSE).
