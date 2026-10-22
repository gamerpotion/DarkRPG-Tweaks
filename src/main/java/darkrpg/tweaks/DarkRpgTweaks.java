package darkrpg.tweaks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.class_2960;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Registers the actual, always-on datapack overlay, and gently updates
 *  the single TieredZ reforge odds setting when it is still at stock default. */
public final class DarkRpgTweaks implements ModInitializer {
    private static final Logger LOG = LoggerFactory.getLogger("DarkRPG Tweaks");
    private static final String MOD_ID = "darkrpg_tweaks";
    private static final Pattern REFORGE = Pattern.compile("(\"reforgeModifier\"\\s*:\\s*)([0-9]+(?:\\.[0-9]+)?)");

    @Override
    public void onInitialize() {
        try {
            ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
            boolean registered = ResourceManagerHelper.registerBuiltinResourcePack(
                new class_2960(MOD_ID, "balanced_overrides"), mod,
                ResourcePackActivationType.ALWAYS_ENABLED);
            if (registered) {
                LOG.info("[DarkRPG Tweaks 1.0.0-beta.1] ALWAYS-ON balance datapack registered: 24 Soulslike spell assignments + 144 TieredZ item modifiers.");
            } else {
                LOG.error("[DarkRPG Tweaks] Could not register built-in balance datapack! The rebalanced items may not load.");
            }
        } catch (Exception error) {
            LOG.error("[DarkRPG Tweaks] Built-in balance datapack registration failed: " + error);
        }
        updateTieredReforgeDefaults();
    }

    private static void updateTieredReforgeDefaults() {
        // Respect deliberate user customization. Change ONLY stock defaults, once;
        // never replace other config keys or create a new Tiered config file.
        if (!FabricLoader.getInstance().isModLoaded("tiered")) return;
        try {
            Path config = FabricLoader.getInstance().getConfigDir().resolve("tiered.json5");
            if (!Files.isRegularFile(config)) return;
            String current = Files.readString(config, StandardCharsets.UTF_8);
            Matcher m = REFORGE.matcher(current);
            if (!m.find()) return;
            double old = Double.parseDouble(m.group(2));
            if (Math.abs(old - 0.9) > 0.0001) return;
            Path backup = config.resolveSibling("tiered.json5.before-darkrpg-tweaks.bak");
            if (Files.exists(backup)) return; // don't repeatedly override an admin's choices
            Files.copy(config, backup);
            String revised = current.substring(0, m.start(2)) + "0.65" + current.substring(m.end(2));
            Path temp = config.resolveSibling("tiered.json5.darkrpgtweaks.tmp");
            try {
                Files.writeString(temp, revised, StandardCharsets.UTF_8);
                Files.move(temp, config, StandardCopyOption.REPLACE_EXISTING);
            } finally { Files.deleteIfExists(temp); }
            LOG.info("[DarkRPG Tweaks] Updated default TieredZ reforgeModifier from 0.9 to 0.65; saved a config backup. Restart if TieredZ already loaded its settings.");
        } catch (Exception error) {
            LOG.warn("[DarkRPG Tweaks] Could not update optional TieredZ reforge setting; the built-in datapack remains available: " + error);
        }
    }
}
