package darkrpg.tweaks.compat.origins.mixin;

import darkrpg.tweaks.compat.origins.OriginsMissingLayerGuard;
import io.github.apace100.origins.origin.OriginLayer;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Only replaces the getLayer call in Origins 1.10.0's synthetic predicate
 * lambda. The existing original source already checks `if(layer == null) return false`.
 * Its remaining behaviour is deliberately left unchanged.
 */
@Mixin(targets = "io.github.apace100.origins.power.OriginsEntityConditions", remap = false)
public abstract class OriginsEntityConditionsMixin {
    @Redirect(
        method = "lambda$register$1",
        at = @At(
            value = "INVOKE",
            target = "Lio/github/apace100/origins/origin/OriginLayers;getLayer(Lnet/minecraft/class_2960;)Lio/github/apace100/origins/origin/OriginLayer;",
            remap = false
        ),
        remap = false,
        require = 1
    )
    private static OriginLayer darkrpgtweaks$missingLayerIsFalse(class_2960 layerId) {
        return OriginsMissingLayerGuard.getOrNull(layerId);
    }
}
