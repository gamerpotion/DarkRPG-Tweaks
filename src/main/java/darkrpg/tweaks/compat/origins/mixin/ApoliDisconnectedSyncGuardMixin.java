package darkrpg.tweaks.compat.origins.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevents Apoli's delayed action from synchronizing a power to a player
 * whose networkHandler was already cleared by disconnection. This is the
 * specific cause of the 2026-11-04 DarkRPG server crash, not a catch-all.
 * Uses intermediary signatures because the packaged 1.20.1 mods are remapped.
 */
@Mixin(targets = "io.github.apace100.apoli.component.PowerHolderComponent", remap = false)
public interface ApoliDisconnectedSyncGuardMixin {
    @Inject(
        method = "syncPower(Lnet/minecraft/class_1297;Lio/github/apace100/apoli/power/PowerType;)V",
        at = @At("HEAD"), cancellable = true, remap = false, require = 1
    )
    private static void darkrpg$tweaks$skipDisconnectedPlayer(
            class_1297 entity, io.github.apace100.apoli.power.PowerType<?> powerType, CallbackInfo ci) {
        if (entity instanceof class_3222 player && player.field_13987 == null) {
            ci.cancel();
        }
    }
}
