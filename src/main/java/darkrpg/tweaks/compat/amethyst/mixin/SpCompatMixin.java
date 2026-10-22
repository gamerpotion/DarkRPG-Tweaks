package darkrpg.tweaks.compat.amethyst.mixin;

import me.fzzyhmstrs.amethyst_core.compat.spell_power.SpChecker;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Compatibility for Amethyst Core 1.5.0 (1.20.1) with Spell Power 1.6.x (1.20.1).
 *
 * Amethyst Core was compiled against SpellSchool.attribute, removed in 1.6.x.
 * SpellSchool.ownedAttribute() supplies the same raw attribute for the standard schools.
 * We deliberately leave mechanics CRITICAL_CHANCE / CRITICAL_DAMAGE / HASTE
 * on Amethyst Core's original path because Entry.attribute still exists in 1.6.1.
 *
 * SpellPower.getHaste(LivingEntity) also became getHaste(LivingEntity, SpellSchool).
 * Use GENERIC school, which carries standard haste calculation on 1.6.1.
 *
 * Reflection only calls classes owned by Spell Power; it avoids compile-time
 * dependencies on optional mod classes and remapping of third-party APIs.
 */
@Mixin(targets = "me.fzzyhmstrs.amethyst_core.compat.spell_power.SpCompat", remap = false)
public abstract class SpCompatMixin {

    @Inject(method = "getAttributeFromEnum", at = @At("HEAD"), cancellable = true, remap = false)
    private void darkrpg$fixSchoolAttribute(SpChecker.Power attr, CallbackInfoReturnable<Object> cir) {
        String name = attr.name();
        switch (name) {
            case "ARCANE": case "FIRE": case "FROST":
            case "HEALING": case "LIGHTNING": case "SOUL":
                break;
            default:
                return; // mechanics still expose .attribute
        }
        try {
            Class<?> schools = Class.forName("net.spell_power.api.SpellSchools");
            Object school = schools.getField(name).get(null);
            Object result = school.getClass().getMethod("ownedAttribute").invoke(school);
            if (result == null) {
                throw new IllegalStateException("Spell Power school " + name + " has no owned attribute");
            }
            cir.setReturnValue(result);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Amethyst Core / Spell Power 1.6 compatibility failed for " + name, unwrap(ex));
        }
    }

    @Inject(method = "getHaste", at = @At("HEAD"), cancellable = true, remap = false)
    private void darkrpg$fixHaste(class_1309 user, class_1799 stack, CallbackInfoReturnable<Double> cir) {
        try {
            Class<?> schoolClass = Class.forName("net.spell_power.api.SpellSchool");
            Class<?> schools = Class.forName("net.spell_power.api.SpellSchools");
            Class<?> power = Class.forName("net.spell_power.api.SpellPower");
            Object generic = schools.getField("GENERIC").get(null);
            Method haste = power.getMethod("getHaste", class_1309.class, schoolClass);
            Object result = haste.invoke(null, user, generic);
            cir.setReturnValue(((Number) result).doubleValue());
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Amethyst Core / Spell Power 1.6 haste compatibility failed", unwrap(ex));
        }
    }

    private static Throwable unwrap(ReflectiveOperationException ex) {
        if (ex instanceof InvocationTargetException && ((InvocationTargetException) ex).getCause() != null) {
            return ((InvocationTargetException) ex).getCause();
        }
        return ex;
    }
}
