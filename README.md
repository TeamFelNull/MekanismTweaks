# MekanismTweaks

**Higher Consumption, Higher Performance** — this branch (`1.7.10-7.1.2`) targets **Minecraft 1.7.10** and **Mekanism 7.1.2 only**. Other Mekanism versions for 1.7.10 (such as 9.1.0) are not supported and are refused at startup.

Mekanism's Speed and Energy Upgrades are capped at 8 each. This mod lifts that cap, lets machines run faster than one operation per tick, and makes the energy cost grow with the speed, so more upgrades really mean more performance *and* more consumption.

## Requirements

- Minecraft 1.7.10 with Forge 10.13.4.1614
- [Mekanism 7.1.2](https://github.com/mekanism/Mekanism/releases/tag/v7.1.2) for 1.7.10 (and its own dependencies, e.g. ForgeMultipart). **Not** Mekanism 9.x. This version is only available from the GitHub release page linked here, not from CurseForge (which only has 9.1.x for 1.7.10).
- [UniMixins](https://github.com/LegacyModdingMC/UniMixins) (or GTNHMixins / SpongeMixins)

## Features

- **Configurable upgrade limits.** The maximum number of Speed and Energy Upgrades a machine can hold is set in the config (64 by default). The upgrade items' stack size follows it (never above 64).
- **Bulk install.** The whole stack in the upgrade slot is installed at once, up to the limit, instead of one upgrade every 40 ticks.
- **Beyond one operation per tick.** When the required time drops below one tick, the excess progress turns into extra operations in the same tick (one extra operation per 20 excess progress). The progress bar shows full while a machine runs this fast.
- **Energy costs scale with speed.** The energy per tick grows with the Speed Upgrades. Energy Upgrades save energy and enlarge the energy buffer, but the effect of Energy Upgrades beyond the Speed Upgrades decays (see `freeEnergy` and `sustEnergy`). Gas consumption of the Chemical Injection-type machines scales the same way.
- **Warning icon.** When a machine has 10 or more Speed Upgrades than Energy Upgrades, a blinking yellow warning icon appears below the upgrade panel of its GUI. Hover it: *Insert Energy Upgrades!* Without enough Energy Upgrades the energy needed per tick can exceed what the machine can store, and it stops working.

Supported machines: electric machines (Enrichment Chamber, Crusher, ...), advanced electric machines (Purification Chamber, Chemical Injection Chamber, ...), chance machines, Pressurized Reaction Chamber, Metallurgic Infuser, Chemical Oxidizer, Chemical Dissolution Chamber and Chemical Crystallizer. Factories and the Digital Miner get the upgrade limits and the energy scaling, but never take less than one tick per operation.

## Config

`config/mekanismtweaks.cfg` (restart the game after changing it, since it decides the stack size of the upgrade items):

| Option | Default | Description |
| --- | --- | --- |
| `maxSpeed` | 64 | Maximum Speed Upgrades installed. `2147483647` means unlimited. |
| `maxEnergy` | 64 | Maximum Energy Upgrades installed. `2147483647` means unlimited. |
| `bulkInstall` | true | Install the whole stack in the upgrade slot at once. |
| `energyBuffer` | true | Avoid an excessive energy buffer: the buffer increment of surplus Energy Upgrades decays like their saving effect. |
| `freeEnergy` | 8 | Minimum number of Energy Upgrades whose effect never decays. |
| `sustEnergy` | 0.5 | How much of the Energy Upgrades' effect is sustained against the Speed Upgrades. At 1 nothing decays (as in vanilla Mekanism); at 0 the effect decays fully. At 0.5, overcoming the Speed Upgrades needs Energy Upgrades of at least their square. |

The effect per 8 upgrades is Mekanism's own `UpgradeModifier` (`mekanism.cfg`).

## Building

Requires JDK 25 to run Gradle (the build compiles for Java 8).

1. Download `Mekanism-1.7.10-7.1.2.jar` from the [Mekanism release](https://github.com/mekanism/Mekanism/releases/tag/v7.1.2) and put it in `libs/`.
2. `./gradlew build` — the jar is written to `build/libs/`.
3. `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

`libs/compile-api-stubs.jar` only holds empty interfaces of the optional IC2, CoFH and ComputerCraft APIs that Mekanism's classes implement, so that the classes can be compiled against.

## Other versions

The `1.12.2`, `1.16.5`, `1.18.2` and `master` branches target other Minecraft and Mekanism versions.

## License

See [LICENSE](LICENSE).
