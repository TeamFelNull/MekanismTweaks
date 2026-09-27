# MekanismTweaks

This mod adds nothing but changes the Mekanism upgrade system.

Speed Upgrade and Energy Upgrade can now be installed up to 64, keeping the default speedup and capacity-up rate.
When, as a result of excessive speedup, a machine should operate multiple times in one tick, it simply does. 64 operations/tick like IC2exp is now possible!

Energy Upgrade can save energy up to 8; over 8 it counts up to the number of Speed Upgrades.
For example, 8 Energy Upgrades with 4 Speed Upgrades give the energy saving of 8 Energy Upgrades as usual, but 64 Energy Upgrades with 16 Speed Upgrades give that of 16 Energy Upgrades. Without this restriction you could insert 64 Energy Upgrades with 32 Speed Upgrades and get a far too good deal.

**Higher Consumption, Higher Performance.**

## Update 1.2

- Factories (all tiers) now do several operations per tick too, and their progress bar shows full while they run that fast.
- Many more versions: Minecraft 1.21.1, 1.20.4, 1.20.1, 1.19.2, 1.19.1, 1.18.2, 1.16.5, 1.16.4, 1.16.3, 1.16.1, 1.15.2, 1.12.2, 1.12.1, 1.12, 1.11.2, 1.10.2 and 1.7.10.
- Upgrade limits are configurable on the newer versions (64 by default), and surplus Energy / Gas Upgrades decay against the Speed Upgrades.

## Supported versions

Every Minecraft version has its own branch, named after the Minecraft version. This is the `master` branch, which holds the Minecraft 1.19.2 line (the original implementation).

| Minecraft | Branch | Loader | Mekanism |
| --- | --- | --- | --- |
| 1.21.1 | `1.21.1` | NeoForge | 10.7.x |
| 1.20.4 | `1.20.4` | NeoForge | 10.5.x |
| 1.20.1 | `1.20.1` | Forge | 10.4.x |
| 1.19.2 | `master`, `1.19.2` | Forge | 10.3.x |
| 1.19.1 | `1.19.1` | Forge | 10.3.x |
| 1.18.2 | `1.18.2` | Forge | 10.2.x |
| 1.16.5 | `1.16.5` | Forge | 10.1.x |
| 1.16.4 / 1.16.3 / 1.16.1 | `1.16.4` / `1.16.3` / `1.16.1` | Forge | 10.0.x |
| 1.15.2 | `1.15.2` | Forge | 9.10.x |
| 1.12.2 | `1.12.2` | Forge | 9.8.x |
| 1.12.1 / 1.12 | `1.12.1` / `1.12` | Forge | 9.4.x |
| 1.11.2 | `1.11.2` | Forge | 9.3.x |
| 1.10.2 | `1.10.2` | Forge | 9.2.x (needs MCMultiPart) |
| 1.7.10 | `1.7.10-9.1.x` | Forge | 9.1.x |
| 1.7.10 | `1.7.10-7.1.2` | Forge | 7.1.2 only (needs UniMixins) |

Mixin is included in the mod on 1.12.2 and older; Forge and NeoForge already ship it on the newer versions. Apart from Mekanism (and its own dependencies) nothing else is required, except UniMixins for the 1.7.10 Mekanism 7.1.2 version.

## Notes for 1.15.2 - 1.21.1

- Several operations per tick: recipe machines, Factories, Digital Miner, Electric Pump, Fluidic Plenisher and Formulaic Assemblicator (the caps are configurable on most versions).
- The Digital Miner accepts Muffling Upgrades.
- On 1.16.x and 1.15.2 there is no warning tab. On the other versions the yellow warning tab shows "Insert Energy Upgrades!" when a machine has too many more Speed Upgrades than Energy Upgrades.
- On 1.21.1, Mekanism 10.7 already does several operations per tick itself (including Factories); the mod keeps the upgrade effect consistent with it.
- The `1.16.5` and `1.18.2` branches keep the original implementation: no config file, Speed / Energy Upgrades up to 64.

## 1.7.10 for Mekanism 9.1.x (`1.7.10-9.1.x`)

Supports Mekanism 9.1.x only (tested with 9.1.0.281 and 9.1.1.1031). With any other version the game stops at startup with a missing-mod message. No Mixin library is needed: it is a plain core mod.

- Bulk install: the whole stack in the upgrade slot is installed at once (up to the limit), instead of one upgrade every 40 ticks.
- Warning icon: a blinking warning icon under the redstone control tab shows "Insert Energy Upgrades!" when a machine has too many more Speed Upgrades than Energy Upgrades.
- Gas and Muffling Upgrades can also be raised (64 and 4 by default), and Gas Upgrades work like Energy Upgrades for the gas consumption.
- Factories (all tiers), Electric Pump, Fluidic Plenisher, Formulaic Assemblicator and Digital Miner operate several times per tick when they are faster than one tick.
- The Digital Miner accepts Muffling Upgrades; fully muffled, its block break effect makes no sound.
- Config (`config/mekanismtweaks.cfg`): `maxSpeed`, `maxEnergy`, `maxGas`, `maxMuffling`, `bulkInstall`, `energyBuffer`, `freeEnergy`, `freeGas`, `sustEnergy`, `sustGas`, `maxMinerOperations`, `maxPumpOperations`, `maxPlenisherOperations`, `maxAssemblicatorOperations`. Restart the game after changing it.

## 1.7.10 for Mekanism 7.1.2 (`1.7.10-7.1.2`)

Supports Mekanism 7.1.2 only, which is available from [GitHub](https://github.com/mekanism/Mekanism/releases/tag/v7.1.2) (download `Mekanism-1.7.10-7.1.2.jar`), not from CurseForge. UniMixins is also required.

- Bulk install and the warning icon, as above.
- Factories (all tiers) and the Digital Miner do several operations per tick when they are faster than one tick.
- Config (`config/mekanismtweaks.cfg`): `maxSpeed`, `maxEnergy`, `bulkInstall`, `energyBuffer`, `freeEnergy`, `sustEnergy`, `maxMinerOperations`. Restart the game after changing it.

## This branch (`master`, Minecraft 1.19.2)

- Requires Minecraft 1.19.2 with Forge 43 or later, and Mekanism 10.3.x (developed and tested with 10.3.8.477).
- Up to 64 Speed and Energy Upgrades; the effect of every 8 upgrades is kept (the base is `maxUpgradeMultiplier` in Mekanism's general config), and the time per operation shrinks exponentially with the Speed Upgrades.
- Several operations per tick for every recipe machine, all tiers of Factories, the Electric Pump, the Fluidic Plenisher, the Formulaic Assemblicator and the Digital Miner. A Factory that is faster than one operation per tick shows a full progress bar.
- The energy needed per tick grows with the Speed Upgrades. Energy Upgrades save energy only up to the number of Speed Upgrades (at least 8) and also enlarge the energy buffer.
- The upgrade screen shows the effect that is really applied, in exponential notation.
- This branch has no config file of its own.
- Known issue: with more than 56 Speed Upgrades, some machines can make the server TPS drop.

### Building

Requires JDK 17 (ForgeGradle 5).

- `./gradlew jar` — the jar is written to `build/libs/`. Mekanism is downloaded from [ModMaven](https://modmaven.dev/) automatically.
- `./gradlew runClient` / `./gradlew runServer` to try it in a development environment.

## Credits

Original implementation by nin8995.

## License

See [LICENSE](LICENSE).
