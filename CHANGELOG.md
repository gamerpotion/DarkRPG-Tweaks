# Changelog

## 1.0.0-beta.4

- Added configurable client-side main-menu version display (DarkRPG and DarkRPG Tweaks versions).
- Versions, visibility, and position are editable in `config/darkrpg-tweaks-menu.properties`.
- ScreenEvents after-render hook supports the vanilla title screen and commonly named custom main menu screens.
- Corrected Fabric Event and FabricLoader method invocation types after runtime crash reports.
- All existing Beta 2 compatibility mixins and the 24 Soulslike + 144 TieredZ datapack overrides remain unchanged.
- DungeonZ beta 3 experimental hooks are not part of this beta 4 build.
- Menu label is client-only and does not read server modpack versions automatically.


## 1.0.0-beta.2

- Updated the 144 built-in TieredZ modifier `weight` fields: Common 30, Uncommon 30, Rare 25, Epic 10, Legendary 4, Unique 1.
- Unique modifiers now have positive selection weight for normal crafting and eligible loot rolls.
- Kept TieredZ's attribute amounts, item eligibility/verifiers, slots and rarity colors unchanged from Beta 1.
- Kept the 24 Soulslike/Spell Engine assignment overrides and compatibility patch logic unchanged.
- Kept the optional TieredZ `reforgeModifier` migration behaviour unchanged.
- Updated the mod version and its diagnostic text to Beta 2.

**Validation:** Source/JAR consistency, Java compilation and static bytecode checks. Minecraft client/server gameplay remains to be tested.
