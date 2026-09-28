# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.12.2`) targets **Minecraft 1.12.2** with **Forge** and **Mekanism 9.8.x** (developed and tested with 9.8.3.390).

Mekanism's Speed, Energy and Gas Upgrades are capped at 8 each. This mod lifts that cap, keeps the effect of every 8 upgrades as it is, lets machines do several operations in one tick when they are fast enough, and lets the effect of surplus Energy Upgrades decay against the Speed Upgrades, so more upgrades really mean more performance *and* more consumption.

Install [MixinBooter 11.17](https://github.com/CleanroomMC/MixinBooter) alongside the mod. Mixin and MixinExtras are provided by MixinBooter rather than bundled in this jar.

## Requirements

- Minecraft 1.12.2 with Forge 14.23.5.2847 (or a later build for the same Minecraft version)
- Mekanism 9.8.x for Minecraft 1.12.2
- MixinBooter 11.17

## Features

- **Configurable upgrade limits.** The maximum number of Speed, Energy, Gas and Muffling Upgrades a machine can hold is set in the config (64, 64, 64 and 4 by default). Every upgrade item, including Muffling, Anchor and Filter, stacks to 64 regardless of its installation limit.
- **Same effect per 8 upgrades.** Mekanism divides the number of upgrades by the maximum to get the effect, so raising the maximum alone would weaken every upgrade. This mod keeps 8 upgrades as one unit of effect (the base is Mekanism's own `UpgradeModifier`).
- **Several operations per tick.** When the time per operation drops below one tick, excess progress turns into extra operations in that tick (one extra operation per 20 excess progress). The progress bar shows full while a machine runs this fast. Energy is charged once per tick, not once per extra operation; the per-tick cost already scales with the Speed Upgrades. The repeat loop stops when no further operation succeeds, rather than running through an enormous theoretical count after inputs or output space run out. Very large batches that do succeed can still lengthen a server tick.
- **Factories.** Each processing lane keeps its own progress and performs its extra operations independently. An idle lane does not consume another lane's progress, and every Factory tier benefits from the increased speed.
- **Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator.** Same for them: several blocks, pump operations, plenish operations or crafts per tick.
- **Digital Miner Muffling.** Muffling Upgrades progressively reduce block-break sound and particles. At the configured maximum, the miner sends no block-break effect event at all.
- **Bulk upgrade handling.** Upgrades placed in a machine's upgrade slot are installed together up to its limit. Sneak-right-clicking with a stack installs as many as fit; Shift-clicking the removal button takes out all upgrades of the selected type.
- **Energy Upgrades and the energy cost.** The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but only up to the Speed Upgrades (at least `freeEnergy`): the effect of Energy Upgrades beyond that decays (see `sustEnergy`). Gas Upgrades work the same way for the gas consumption (`freeGas`, `sustGas`).
- **Insufficient energy buffer warning.** The energy info tab warns when a processing machine's maximum energy storage cannot cover even one tick of its energy cost, including a Pressurized Reaction Chamber recipe's extra cost. Digital Miner keeps its built-in warning.
- **Effect display.** The upgrade screen shows the effect that is really applied, in exponential notation.

Supported machines: electric machines (Enrichment Chamber, Crusher, ...), advanced electric machines (Purification Chamber, Chemical Injection Chamber, ...), chance machines, Pressurized Reaction Chamber, Metallurgic Infuser, Chemical Oxidizer, Chemical Dissolution Chamber, Chemical Crystallizer, Factories, Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator. Other machines get the upgrade limits and the effect scaling, but never take less than one tick per operation.

## Config

`config/mekanismtweaks.cfg` (restart the game after changing upgrade limits or effects):

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `maxGas` | 64 | Maximum Gas Upgrades installed. `2147483647` means unlimited. |
| `maxMuffling` | 4 | Maximum Muffling Upgrades installed (1–64). Higher values provide finer steps of sound and particle reduction; the limit is 64 because the upgrade count is encoded in the block-break event data. |
| `avoidExcessiveEnergyBuffer` | true | Avoid an excessive energy buffer: the buffer increment of surplus Energy Upgrades decays like their saving effect. |
| `freeEnergy` / `freeGas` | 8 | Minimum number of Energy / Gas Upgrades whose effect never decays. |
| `sustEnergy` / `sustGas` | 0.5 | How much of the Energy / Gas Upgrades' effect is sustained against the Speed Upgrades. At 1 nothing decays (as in vanilla Mekanism); at 0 the effect decays fully. At 0.5, overcoming the Speed Upgrades needs upgrades of at least their square. |

The effect per 8 upgrades is Mekanism's own `UpgradeModifier` (`mekanism.cfg`).

### Speed formula

Let `x` be the number of installed Speed Upgrades, `d` the machine's base ticks per operation, and `M` Mekanism's `maxUpgradeMultiplier`. With `T = d / M^(x/8)`, the required ticks are:

```text
T > 2:   floor(T)
T <= 2:  -ceil(20 * (2^max(log_M(d / 2) + 1 - x/8, 0) / T - 1))
```

A negative result represents excess progress, with 20 progress per additional operation. [Explore the speed curve in Desmos](https://www.desmos.com/calculator/fk7mk2t9hq?lang=ja).

### Energy Upgrade effect

Let `m` be the installed Speed Upgrade count, `x` the installed count of the upgrade whose effect is being calculated (`ENERGY` or `GAS`), `f = freeEnergy` or `freeGas`, `s = sustEnergy` or `sustGas`, and `M` Mekanism's `maxUpgradeMultiplier`. Upgrades above `n` have a reduced effect:

```text
n = max(m, f)
sustainRate = 0                                  if s = 0
              1 / max(1, n^(log_2(1/s)) - 1)     if s > 0
decayedFraction = (x <= n ? x : n + (x - n) * sustainRate) / 8

energy per tick = base energy per tick * M^(2m/8 - decayedFraction)
energy buffer   = base buffer * M^decayedFraction  if avoidExcessiveEnergyBuffer
                  base buffer * M^(x/8)            otherwise
```

Thus Energy Upgrades up to `max(m, f)` retain their full effect; each additional upgrade contributes only `sustainRate` of an upgrade. The same decay calculation applies to Gas Upgrades using `freeGas` and `sustGas`. [Explore the decay curve in Desmos](https://www.desmos.com/calculator/bc5e1bd598?lang=ja).

## Building

The development build uses RetroFuturaGradle 1.4.9 with Gradle 8.8. Run Gradle with a JDK 21. Gradle uses a Java 8 toolchain to compile and launch Minecraft 1.12.2; the Foojay resolver can download one when needed.

Set JDK 21 as `JAVA_HOME` or as IntelliJ's Gradle JVM. Do not add a machine-specific `org.gradle.java.home` path to `gradle.properties`.

- `./gradlew build` — the jar is written to `build/libs/`. Mekanism is downloaded from [CurseMaven](https://www.cursemaven.com/) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.
- `./gradlew idea` — generates IntelliJ metadata and attaches the matching Mekanism sources, so Find in Files can search dependency code with the `Project and Libraries` scope. Refresh the Gradle project in IntelliJ after the first setup.

`libs/compile-api-stubs.jar` only holds empty interfaces of the optional IC2, CoFH and ComputerCraft APIs that Mekanism's classes implement, so that the classes can be compiled against.

## Other versions

Every Minecraft version has its own branch, named after the Minecraft version: `1.21.1`, `1.20.4`, `1.20.1`, `1.19.2`, `1.19.1`, `1.16.4`, `1.16.3`, `1.16.1`, `1.15.2`, `1.12.2`, `1.12.1`, `1.12` (Mekanism 9.4 - 9.8), `1.11.2` and `1.10.2` (Mekanism 9.x). The `1.7.10-7.1.2` and `1.7.10-9.1.x` branches target Mekanism 7.1.2 and 9.1.x for Minecraft 1.7.10. The `1.16.5`, `1.18.2` and `master` branches keep the older implementations.

## License

See [LICENSE](LICENSE).
