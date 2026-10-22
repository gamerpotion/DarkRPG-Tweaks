package darkrpg.tweaks.compat;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A deliberately late diagnostic; does not modify runtime keybindings. */
public final class CosmeticaBridgeClientDiagnostics implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("DarkRPG Tweaks");

    @Override public void onInitializeClient() {
        BridgeDiagnostics.Snapshot status = BridgeDiagnostics.read();
        String prefix = "[DarkRPG Tweaks 1.0.0-beta.1] Cosmetica/Spell Engine bridge ";
        switch (status.state()) {
            case ACTIVE -> LOGGER.info(prefix + "ACTIVE: " + status.reason()
                    + ". Note: gameplay keybindings are not verified by this diagnostic.");
            case INACTIVE -> LOGGER.warn(prefix + "INACTIVE: " + status.reason());
            case ERROR -> LOGGER.error(prefix + "ERROR: " + status.reason());
            case NOT_EVALUATED -> LOGGER.error(prefix + "ERROR: Mixin onLoad did not report a result "
                    + "before client initialization. Cannot confirm Cosmetica compatibility patch.");
        }
    }
}
