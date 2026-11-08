package darkrpg.tweaks.compat.simplyswords.mixin;

import java.util.Optional;
import net.minecraft.class_1320;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_6880;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.sweenus.simplyswords.compat.SpellScalingAssignments;
import net.sweenus.simplyswords.compat.SpellSchoolDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Version-specific bridge for Simply Swords 1.70.2 and Spell Power 1.6.x.
 * Intercepts the two legacy field-reading methods before their original body.
 */
@Mixin(targets = "net/sweenus/simplyswords/fabric/FabricHelperMethods", remap = false)
public abstract class SimplySwordsAttributeCompatMixin {
    @Inject(method = "spellPowerAttribute", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void darkrpg$spellPowerAttribute(class_2960 scalingId, CallbackInfoReturnable<class_1320> result) {
        result.setReturnValue(darkrpg$getModernAttribute(darkrpg$findSchool(scalingId)));
    }

    @Inject(method = "activeSpellSchoolDisplay", at = @At("HEAD"), cancellable = true, remap = false, require = 1)
    private static void darkrpg$spellSchoolDisplay(class_2960 scalingId, CallbackInfoReturnable<SpellSchoolDisplay> result) {
        SpellSchool school = darkrpg$findSchool(scalingId);
        if (school == null) {
            class_2960 fallback = SpellScalingAssignments.defaultSpellPowerSchool(scalingId)
                    .orElse(new class_2960("spell_power", "arcane"));
            result.setReturnValue(new SpellSchoolDisplay(fallback, class_2561.method_43470(fallback.toString())));
            return;
        }
        class_1320 attribute = darkrpg$getModernAttribute(school);
        class_2561 name = attribute == null
                ? class_2561.method_43470(school.id.toString())
                : class_2561.method_43471(attribute.method_26830());
        result.setReturnValue(new SpellSchoolDisplay(school.id, name));
    }

    @Unique
    private static class_1320 darkrpg$getModernAttribute(SpellSchool school) {
        if (school == null) return null;
        class_6880<class_1320> attributeEntry = school.getAttributeEntry();
        return attributeEntry == null ? null : attributeEntry.comp_349();
    }

    @Unique
    private static SpellSchool darkrpg$findSchool(class_2960 scalingId) {
        if (scalingId == null) return null;
        class_2960 configured = SpellScalingAssignments.spellPowerSchool(scalingId).orElse(null);
        SpellSchool result = configured == null ? null : SpellSchools.getSchool(configured.toString());
        if (result != null) return result;
        class_2960 fallback = SpellScalingAssignments.defaultSpellPowerSchool(scalingId).orElse(null);
        return fallback == null ? null : SpellSchools.getSchool(fallback.toString());
    }
}
