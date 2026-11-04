package darkrpg.tweaks.compat.tiered.mixin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.google.gson.Gson;
import draylar.tiered.api.PotentialAttribute;
import net.minecraft.class_2960;

/**
 * Enforces the balanced entries in Tiered's final attribute registry after
 * its reload listener has parsed all datapacks. This corrects the low-priority
 * builtin resourcepack issue without editing Tiered or changing every pack.
 * The same 144 JSON definitions from beta.4 are intentionally retained.
 */
@Mixin(targets = "draylar.tiered.data.AttributeDataLoader", remap = false)
public abstract class TieredBalancedRegistryMixin {
    @Shadow(remap = false) private Map<class_2960, PotentialAttribute> itemAttributes;
    @Shadow(remap = false) @Final private static Gson GSON;

    @Inject(method = "apply", at = @At("TAIL"), remap = false, require = 1)
    private void darkrpg$tweaks$forceBalance(CallbackInfo ci) {
        final String prefix = "resourcepacks/balanced_overrides/data/tiered/item_attributes/";
        int count = 0;
        int errors = 0;
        try (InputStream indexStream = getClass().getClassLoader()
                    .getResourceAsStream("assets/darkrpg_tweaks/tiered-override-index.txt")) {
            if (indexStream == null) {
                System.err.println("[DarkRPG Tweaks beta.6] ERROR: Tiered modifier index missing");
                return;
            }
            try (BufferedReader indexReader = new BufferedReader(new InputStreamReader(indexStream, StandardCharsets.UTF_8))) {
                String path;
                while ((path = indexReader.readLine()) != null) {
                    if (path.isEmpty() || path.startsWith("#")) continue;
                    try (InputStream entryStream = getClass().getClassLoader()
                            .getResourceAsStream(prefix + path)) {
                        if (entryStream == null) { errors++; continue; }
                        try (InputStreamReader reader = new InputStreamReader(entryStream, StandardCharsets.UTF_8)) {
                            PotentialAttribute entry = GSON.fromJson(reader, PotentialAttribute.class);
                            if (entry == null || entry.getID() == null) { errors++; continue; }
                            itemAttributes.put(new class_2960(entry.getID()), entry);
                            count++;
                        }
                    } catch (RuntimeException | java.io.IOException problem) {
                        errors++;
                        System.err.println("[DarkRPG Tweaks beta.6] Invalid Tiered balance entry " + path + ": " + problem);
                    }
                }
            }
        } catch (java.io.IOException problem) {
            System.err.println("[DarkRPG Tweaks beta.6] Failed to enforce Tiered balance: " + problem);
        }
        System.out.println("[DarkRPG Tweaks beta.6] Tiered registry balance applied: " + count
                + " positive-only modifier definitions, " + errors + " errors.");
    }
}
