package darkrpg.tweaks.compat.cosmetica.mixin;

import cc.cosmetica.cosmetica.Keybinds;
import cc.cosmetica.cosmetica.mixin.keybinds.KeyMappingAccessor;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cosmetica's original redirect prevents its SNIPE binding entering vanilla's
 * key-to-binding map and puts it into SPECIAL_MAP. This shim runs only when the
 * exact original conflicting redirect was removed BEFORE Mixin preparation.
 * Spell Engine's own GUI-scoped redirect remains untouched.
 */
@Mixin(targets = "net.minecraft.class_304", remap = false)
public abstract class SnipeKeyBindingMixin {
    @Shadow(remap=false) @Final private static Map<Object, Object> field_1658;

    @Inject(method="method_1426", at=@At("TAIL"), remap=false, require=1)
    private static void darkrpgtweaks$restoreCosmeticaSnipe(CallbackInfo ci) {
        net.minecraft.class_304 snipe = Keybinds.SNIPE;
        if (snipe == null) return;
        net.minecraft.class_3675.class_306 key =
            ((KeyMappingAccessor)(Object)snipe).cosmetica$getKey();
        if (key == null) return;
        // Remove stale mappings of the same SNIPE binding when reassigned.
        Keybinds.SPECIAL_MAP.entrySet().removeIf(e -> e.getValue() == snipe && !e.getKey().equals(key));
        Keybinds.SPECIAL_MAP.put(key, snipe);
        // Don't delete another mod's binding with this key by accident.
        if (field_1658.get(key) == snipe) field_1658.remove(key);
    }
}
