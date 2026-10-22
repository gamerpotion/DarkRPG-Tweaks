package darkrpg.tweaks.compat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * DarkRPG Tweaks: only apply the Amethyst Core compatibility mixin on the exact
 * tested family of affected mods. Keeps DarkRPG Tweaks independent of both optional mods.
 *
 * Versions intentionally scoped to:
 *   Minecraft 1.20.1
 *   Amethyst Core 1.5.0
 *   Spell Power Attributes 1.6.x
 */
public final class AmethystSpellPowerMixinPlugin implements IMixinConfigPlugin {
    private boolean active;

    @Override
    public void onLoad(String mixinPackage) {
        this.active = detectAffectedInstall();
        if (this.active) {
            System.out.println("[DarkRPG Tweaks 1.0.0-beta.2] Enabling Amethyst Core 1.5.0 / Spell Power 1.6.x compatibility Mixin");
        }
    }

    private static boolean detectAffectedInstall() {
        try {
            Class<?> loaderType = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object loader = loaderType.getMethod("getInstance").invoke(null);
            Method present = loaderType.getMethod("isModLoaded", String.class);
            for (String id : new String[]{"amethyst_core", "spell_power", "minecraft"}) {
                if (!Boolean.TRUE.equals(present.invoke(loader, id))) return false;
            }
            String mc = versionOf(loaderType, loader, "minecraft");
            String amethyst = versionOf(loaderType, loader, "amethyst_core");
            String spell = versionOf(loaderType, loader, "spell_power");
            return mc.equals("1.20.1")
                && (amethyst.equals("1.5.0") || amethyst.startsWith("1.5.0+"))
                && (spell.equals("1.6.0") || spell.startsWith("1.6.0+")
                    || spell.equals("1.6.1") || spell.startsWith("1.6.1+")
                    || spell.equals("1.6.2") || spell.startsWith("1.6.2+"));
        } catch (ReflectiveOperationException | LinkageError ex) {
            System.err.println("[DarkRPG Tweaks] Skipping optional Amethyst Core compatibility mixin: " + ex.getClass().getSimpleName());
            return false;
        }
    }

    private static String versionOf(Class<?> loaderType, Object loader, String id) throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Optional<Object> container = (Optional<Object>) loaderType.getMethod("getModContainer", String.class).invoke(loader, id);
        if (container.isEmpty()) return "";
        Object metadata = Class.forName("net.fabricmc.loader.api.ModContainer").getMethod("getMetadata").invoke(container.get());
        Object version = Class.forName("net.fabricmc.loader.api.metadata.ModMetadata").getMethod("getVersion").invoke(metadata);
        return (String) Class.forName("net.fabricmc.loader.api.Version").getMethod("getFriendlyString").invoke(version);
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return active;
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
