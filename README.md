# DarkRPG Tweaks

Compatibility and balance adjustments for **DarkRPG Fabric — Minecraft 1.20.1**.

**Version:** `1.0.0-beta.2`  
**Mod ID:** `darkrpg_tweaks`  
**Status:** Beta — requires further in-game validation.

## Features

- **Amethyst Core / Spell Power:** compatibility mixin for API differences.
- **Origins:** client-side guard for missing origin layers.
- **Cosmetica / Spell Engine:** targeted compatibility handling for conflicting keybinding mixins.
- **More RPG Library:** protects dedicated servers against recursively reflected player damage in the tested `2.7.2+1.20.1` build.
- **Soulslike Weaponry:** 24 Spell Engine assignment overrides.
- **TieredZ:** 144 balanced modifier definitions with nonnegative attribute values. In Beta 2, normal-roll weights per rarity are Common 30, Uncommon 30, Rare 25, Epic 10, Legendary 4 and Unique 1 (Unique is now rollable). If `tiered.json5` still has `reforgeModifier: 0.9`, an optional migration changes this value to `0.65` and writes a backup; existing nondefault values are preserved.

These are per-definition selection weights, not a guarantee that every item or reforge operation has exactly the stated percentages. Eligibility and reforge/luck handling affect actual results.

The two datapacks are registered as a built-in always-enabled pack. They **do not need separate Paxi installations**.

The original DarkUnbug HP/absorption repair feature and `/trigger darkunbug` command are **not included**.

## Repository layout

| Path | Contents |
| --- | --- |
| `src/main/java/` | Java mod implementation, mixins, Mixin config plugins |
| `src/main/resources/` | `fabric.mod.json`, Mixin configs, built-in datapack JSON, notices |
| `test-stubs/` | Compile-time-only API stubs for standalone validation |
| `build-from-source.py` | Rebuilds the JAR using bundled compile-time API stubs |
| `verify_release.py` | Portable ZIP, metadata, resource, bytecode and balance checks |
| `LICENSE` | GNU GPL version 3 text |

The Java mixins use intermediary Minecraft class names as required by this build. This project is **not yet a Fabric Loom/Gradle project**: the current self-contained Java build uses compile-time-only API stubs. Contributors seeking to move it to Loom will need to replace those stubs with real Minecraft/Fabric development dependencies.

## Building locally

Requirements:
- Python 3.9 or newer (standard library only)
- JDK 17 or newer (`javac` and `javap` on `PATH`)

From the repository root:

```bash
python3 build-from-source.py
python3 verify_release.py
```

The build produces `darkrpg-tweaks-fabric-1.20.1-1.0.0-beta.2.jar` in the repository root.

**Build limitation:** This source bundle compiles using local **compile-only API stubs** instead of downloading actual Minecraft/Fabric dependencies. This checks Java syntax, intended JVM method invocation shapes, the packaged resources and other static properties. It **does not replace compiling against a normal modding toolchain or testing in a real Minecraft client/server**. The stubs are never included in the resulting JAR.

## Installation

1. Back up your test world and configs.
2. Remove the old DarkUnbug JAR to avoid overlapping mixins.
3. Remove the corresponding standalone Soulslike/TieredZ datapack ZIPs from Paxi/world datapack folders to avoid duplicate overrides.
4. Install the built DarkRPG Tweaks JAR in the modpack's `mods/` folder on server **and** client.
5. Keep the original compatible mods installed. Fabric API is required.
6. Restart the game/server. If the TieredZ config was migrated on first launch, restart once more.

## Testing checklist

- Verify the built-in balance datapack appears enabled with `/datapack list enabled`.
- Check Soulslike Weaponry's Spell Engine assignments no longer produce the 24 known warnings.
- Craft and find TieredZ equipment with Common/Uncommon modifiers and inspect the attribute results.
- Reproduce the PvP reflection test **only on a disposable test server**: two Human players with `more_rpg_classes:damage_reflect_modifier` set to `200`, then restore both to the default `100`.
- Check Origins, Amethyst Core and Cosmetica client behaviour independently.

## Compatibility and development notes

This project contains targeted compatibility patches, not copies of third-party mod JARs. Its use with newer versions of the affected mods has not been verified. The release should be treated as **experimental** until client/server acceptance tests complete.

The compatibility code was derived from the earlier DarkUnbug project, minus its original HP-repair feature. See `src/main/resources/META-INF/NOTICE-DarkRPG-Tweaks.txt` for provenance and notices. Retain applicable licence and copyright notices when redistributing or modifying this project.

## Licence

GNU GPL-3.0-or-later, as indicated by `fabric.mod.json`. See [LICENSE](LICENSE) and the notices under `src/main/resources/META-INF/`.

## Uploading to GitHub

See [docs/GITHUB-UPLOAD-UBUNTU.md](docs/GITHUB-UPLOAD-UBUNTU.md) for Ubuntu Git + GitHub CLI instructions. The browser upload limit of 100 files does not apply to `git push`. Extract the archive first and upload **all its contents**, not the ZIP itself.
