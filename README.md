# DarkRPG Tweaks

Compatibility fixes and equipment-balance adjustments for the **DarkRPG Fabric** modpack, maintained by GamerPotion.

**Minecraft:** 1.20.1 · **Loader:** Fabric · **Current test build:** 1.0.0-beta.6 · **Side:** Client and server

DarkRPG Tweaks is included with recent DarkRPG Fabric releases and is primarily intended to resolve mod interactions in that pack. Some fixes may also be useful in other modpacks using the same mod versions.

## Features

| Component | Included behavior |
| --- | --- |
| Amethyst Core / Spell Power | Compatibility fix for the affected spell-power integration. |
| Origins / Apoli | Missing-layer protection, plus a **beta.5 safeguard** against synchronizing powers after a player disconnects. |
| Cosmetica / Spell Engine | Compatibility protection for the affected client integration. |
| More RPG Library | Protection against a PvP damage-reflection crash. |
| Soulslike Weaponry / Spell Engine | 24 built-in spell-assignment overrides. |
| TieredZ 1.3.3 | 144 balanced modifier definitions, with positive attribute amounts in the bundled entries. |
| TieredZ reforging | Existing reforge-odds migration, plus a **beta.5 registry enforcement** after datapack reloads. |
| Main menu | No version display or configuration. Use the separate PackIdentity mod to display version information. |

## What changed in beta.6

- Removed the client-side main-menu version display completely; PackIdentity now owns that feature.
- Removed the menu client entrypoint, renderer and configuration classes, so `darkrpg-tweaks-menu.properties` is no longer generated or used.
- Updated the mod version and startup log label to `1.0.0-beta.6`.
- Preserved all beta.5 Origins/Apoli and TieredZ fixes, plus earlier crash guards and the 24 spell assignments.

**Cleanup:** Existing `config/darkrpg-tweaks-menu.properties` files are left on disk because Minecraft mods should not delete user configuration automatically. You can delete that unused file manually on clients. PackIdentity has its own configuration.

## Changes retained from beta.5

- Added a targeted Apoli/Origins synchronization guard for a server crash caused by a delayed power action trying to send data to a disconnected player.
- Added TieredZ 1.3.3 registry enforcement: after TieredZ loads modifier data, the 144 balanced entries bundled with DarkRPG Tweaks are reapplied. This is intended to make balancing independent of datapack priority.
- Corrected an outdated beta.2 startup message. The message in the older build did **not** mean the installed JAR was beta.2.
- Kept the earlier compatibility fixes, Soulslike assignments, modifier definitions and reforge weighting.

**Testing status:** beta.6 is an experimental build. Its JAR archive and static content comparison checks passed, but it has **not** been verified by a full Minecraft client/dedicated-server integration run. Test on a backup before deploying publicly.

## Installation

1. Use Minecraft **1.20.1 with Fabric** and the relevant dependency mods.
2. Place the DarkRPG Tweaks JAR in the client and server `mods/` folders; remove the older DarkRPG Tweaks JAR first.
3. Keep **TieredZ 1.3.3** installed if you use the TieredZ adjustments.
4. Restart the game/server. Check the startup log for the TieredZ registry-balance result if TieredZ is installed.
5. Test newly obtained items and newly performed reforges. Previously obtained items can retain negative attributes stored in their existing item data.

The mod registers a built-in, always-enabled datapack; users do not need to copy the 24 Soulslike or 144 TieredZ JSONs into the world datapacks folder.

## Compatibility and known limitations

- The TieredZ registry patch targets **TieredZ 1.3.3 for Minecraft 1.20.1**. It is not a general-purpose patch for other TieredZ versions.
- The Apoli guard targets the disconnected-player synchronization crash. It does not fix every Origins power or datapack issue.
- **Open Parties and Claims' drop-owner capture warning is not fixed** by this mod. Whether separate ServerCore optimizations contribute remains unconfirmed.
- Spell Engine `projectileData == null` exceptions, packet serialization issues and unrelated server lag are **not** fixed in beta.6.
- PackIdentity handles the main-menu version display independently. DarkRPG Tweaks no longer owns that feature.

## Development

The source is maintained in this repository. The old `src/main/java/darkrpg/tweaks/menu/` classes were removed in beta.6. The project uses **Fabric Loom 1.6.12** with Minecraft intermediary-identity mappings. Java 17 and Gradle 8.8 are used by its GitHub Actions workflow.

Build with the existing CI workflow under `.github/workflows/gradle.yml`. For manual builds, use a compatible Gradle installation and run:

```bash
gradle clean build --no-daemon --console plain
```

The source includes optional, compile-only dependencies for integration targets; those are not bundled into DarkRPG Tweaks.

## Links

- [DarkRPG Fabric modpack](https://www.curseforge.com/minecraft/modpacks/darkrpg)
- [DarkRPG Tweaks on CurseForge](https://www.curseforge.com/minecraft/mc-mods/darkrpg-tweaks)
- [Changelog](CHANGELOG.md)

**Author:** GamerPotion · **License:** GPL-3.0-or-later
