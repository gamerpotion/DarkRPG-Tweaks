package net.spell_power.api;
public class SpellPower {
 public static float getHaste(net.minecraft.class_1309 entity, SpellSchool school) {
    if (!"GENERIC".equals(school.name)) throw new IllegalStateException("wrong school");
    return 1.35f;
 }
}
