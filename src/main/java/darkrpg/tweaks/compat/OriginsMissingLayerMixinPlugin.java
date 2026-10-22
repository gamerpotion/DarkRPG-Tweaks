package darkrpg.tweaks.compat;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** A client-only, version-restricted patch for Origins 1.10.0 / MC 1.20.1. */
public final class OriginsMissingLayerMixinPlugin implements IMixinConfigPlugin {
    private boolean active;

    @Override
    public void onLoad(String mixinPackage) {
        active = detectAffectedInstall();
        if (active) {
            System.out.println("[DarkRPG Tweaks Origins] Enabling Origins 1.10.0 missing-layer crash guard (client-only)");
        }
    }

    private static boolean detectAffectedInstall() {
        try {
            Class<?> loaderType = Class.forName("net.fabricmc.loader.api.FabricLoader");
            Object loader = loaderType.getMethod("getInstance").invoke(null);
            Method isLoaded = loaderType.getMethod("isModLoaded", String.class);
            if (!Boolean.TRUE.equals(isLoaded.invoke(loader, "minecraft"))
                    || !Boolean.TRUE.equals(isLoaded.invoke(loader, "origins"))) return false;
            Object side = loaderType.getMethod("getEnvironmentType").invoke(loader);
            if (!"CLIENT".equals(String.valueOf(side))) return false;
            return "1.20.1".equals(versionOf(loaderType, loader, "minecraft"))
                && "1.10.0".equals(versionOf(loaderType, loader, "origins"));
        } catch (ReflectiveOperationException | LinkageError ex) {
            System.err.println("[DarkRPG Tweaks Origins] Skipping Origins patch: " + ex.getClass().getSimpleName());
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

    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return active; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
