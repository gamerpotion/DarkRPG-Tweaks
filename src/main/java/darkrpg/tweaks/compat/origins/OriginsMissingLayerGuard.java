package darkrpg.tweaks.compat.origins;

import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import net.minecraft.class_2960;

/**
 * Origins 1.10.0's origin condition already handles a null OriginLayer by
 * returning false, but OriginLayers.getLayer throws on an unknown id. This
 * helper exposes the intended null result ONLY for the missing-layer case.
 * No player data, layer registry, origin selection, or server state is changed.
 */
public final class OriginsMissingLayerGuard {
    private OriginsMissingLayerGuard() { }

    public static OriginLayer getOrNull(class_2960 id) {
        // A not-yet-synchronized client registry is the most common failure.
        if (OriginLayers.size() == 0) return null;
        try {
            return OriginLayers.getLayer(id);
        } catch (IllegalArgumentException ex) {
            String message = ex.getMessage();
            if (message != null
                    && message.startsWith("Could not get layer from id '")
                    && message.endsWith("', as it doesn't exist!")) {
                return null;
            }
            throw ex;
        }
    }
}
