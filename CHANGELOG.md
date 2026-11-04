# Changelog

## 1.0.0-beta.6 — Main-menu version display removed

- Removed the DarkRPG Tweaks client-side main-menu version renderer, client entrypoint, and menu configuration generator. The separate **PackIdentity** mod now handles version labels.
- No longer creates or reads `config/darkrpg-tweaks-menu.properties`. Old copies can be deleted manually; the mod will not delete user files automatically.
- Updated the Fabric mod version and startup diagnostic to `1.0.0-beta.6`.
- Preserved the beta.5 Apoli/Origins disconnection safeguard and TieredZ 144-modifier registry reapply behavior.
- Preserved all existing Amethyst Core, Spell Power, Cosmetica/Spell Engine, PvP reflection and 24 Soulslike assignment fixes.

**Validation:** Checked the JAR archive, metadata, absence of all three menu classes, 144 TieredZ modifiers and 24 Soulslike overrides; verified unchanged byte contents for all remaining features. **Minecraft integration testing still required.**

## 1.0.0-beta.5 — Experimental

- Added a targeted Apoli/Origins guard to skip power synchronization when a server-side player's network handler has already been cleared after disconnecting. Intended to address the delayed-action server crash observed on November 4, 2026.
- Added TieredZ 1.3.3 post-reload registry enforcement for the existing 144 balanced modifier definitions, rather than relying only on built-in datapack ordering.
- Preserved the existing 144 modifier JSONs, 24 Soulslike/Spell Engine assignments, rarity weights and reforge configuration migration.
- Corrected the stale `1.0.0-beta.2` text in the datapack-registration log message.
- Updated default main-menu labels to DarkRPG `9.1.7hf` and Tweaks `1.0.0-beta5`; existing properties files remain unchanged unless edited manually.
- Not included: Open Parties and Claims compatibility fix, Spell Engine projectile-data fix, packet serialization changes or server-performance optimization.

**Validation:** Archive and JSON validation, bytecode compilation using a local stub harness, and isolated logic tests. **Not yet verified in Minecraft client/server gameplay.** Test with backups before public distribution.

## 1.0.0-beta.4

- Added configurable client-side main-menu version display (DarkRPG and DarkRPG Tweaks versions).
- Versions, visibility, and position are editable in `config/darkrpg-tweaks-menu.properties`.
- ScreenEvents after-render hook supports the vanilla title screen and commonly named custom main-menu screens.
- Corrected Fabric Event and FabricLoader method invocation types after runtime crash reports.
- All existing beta.2 compatibility mixins and the 24 Soulslike + 144 TieredZ datapack overrides remained unchanged.
- DungeonZ beta.3 experimental hooks were **not** part of beta.4.
- Menu label is client-only and does not read server modpack versions automatically.

## 1.0.0-beta.2

- Updated the 144 built-in TieredZ modifier `weight` fields: Common 30, Uncommon 30, Rare 25, Epic 10, Legendary 4, Unique 1.
- Unique modifiers received positive selection weight for normal crafting and eligible loot rolls.
- Kept the TieredZ attribute amounts, item eligibility/verifiers, slots and rarity colors unchanged from beta.1.
- Kept the 24 Soulslike/Spell Engine assignment overrides and compatibility patch logic unchanged.
- Kept the optional TieredZ `reforgeModifier` migration behavior unchanged.
- Updated the mod version and diagnostic text to beta.2 at that time.

**Earlier validation:** Source/JAR consistency, Java compilation and static bytecode checks. Minecraft gameplay testing was still required.
