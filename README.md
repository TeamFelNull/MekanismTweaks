# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.21.1`) targets **Minecraft 1.21.1** with **NeoForge** and **Mekanism 10.7.x** (developed and tested with 10.7.19).

Mekanism's Speed, Energy and Chemical Upgrades are capped at 8 each. This mod lifts that cap, keeps the effect of every 8 upgrades as it is, and lets the effect of surplus Energy Upgrades decay against the Speed Upgrades, so more upgrades really mean more performance *and* more consumption.

**No extra library is needed** — NeoForge already ships Mixin. Apart from Mekanism the mod requires nothing.

## Requirements

- Minecraft 1.21.1 with NeoForge 21.1
- Mekanism 10.7.x for 1.21.1

## Features

- **Configurable upgrade limits.** The maximum number of Speed, Energy and Chemical Upgrades a machine can hold is set in the config (64 each by default).
- **Same effect per 8 upgrades.** Mekanism divides the number of upgrades by the maximum to get the effect, so raising the maximum alone would weaken every upgrade. This mod keeps 8 upgrades as one unit of effect (the base is Mekanism's own `UpgradeModifier`). Mekanism itself then takes care of the rest: installing the whole stack in the upgrade slot at once, and performing several operations in one tick when a machine is faster than one tick.
- **Energy Upgrades and the energy cost.** The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but only up to the Speed Upgrades (at least `freeEnergy`): the effect of Energy Upgrades beyond that decays (see `sustEnergy`). Chemical Upgrades work the same way for the chemical consumption.
- **Digital Miner.** Mekanism's Digital Miner mines one block per tick at the most, however many Speed Upgrades it has. When the time per block drops below one tick, it now mines several blocks in the same tick, up to `maxMinerOperations`. Every extra block costs the energy of one tick.
- **Effect display.** The upgrade screen shows the effect that is really applied, in exponential notation.
- **Warning.** When a machine has more than 10 more Speed Upgrades than Energy Upgrades, the yellow warning tab of its GUI shows *Insert Energy Upgrades!* Without enough Energy Upgrades the energy needed per tick can exceed what the machine can store, and it stops working.

## Config

`config/mekanismtweaks-common.toml`:

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `maxChemical` | 64 | Maximum Chemical Upgrades installed. `2147483647` means unlimited. |
| `freeEnergy` | 8 | Minimum number of Energy Upgrades whose effect never decays. |
| `freeChemical` | 8 | Minimum number of Chemical Upgrades whose effect never decays. |
| `sustEnergy` | 0.5 | How much of the effect of the Energy Upgrades beyond the Speed Upgrades is sustained. At 1 nothing decays (as in vanilla Mekanism); at 0 the surplus has no effect. At 0.5, overcoming the Speed Upgrades needs Energy Upgrades of at least their square. |
| `sustChemical` | 0.5 | Same as `sustEnergy`, for the Chemical Upgrades. |
| `maxMinerOperations` | 64 | The most blocks a Digital Miner mines in one tick. `1` turns this off, as per vanilla Mekanism. |

## Building

Requires JDK 21.

- `./gradlew build` — the jar is written to `build/libs/`. Mekanism is downloaded from [ModMaven](https://modmaven.dev/) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

## Other versions

The `1.7.10-7.1.2` and `1.7.10-9.1.x` branches target Mekanism for Minecraft 1.7.10. The `1.12.2`, `1.16.5`, `1.18.2` and `master` (1.19.2) branches target other Minecraft and Mekanism versions.

## License

See [LICENSE](LICENSE).
