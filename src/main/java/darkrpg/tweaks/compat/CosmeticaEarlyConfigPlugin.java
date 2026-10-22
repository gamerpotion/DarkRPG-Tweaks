package darkrpg.tweaks.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * DarkRPG Tweaks 1.0.0-beta.1 test bridge for the *exact* Cosmetica 2.0.1 and
 * Spell Engine 1.10.10 redirect collision on Minecraft Fabric 1.20.1.
 *
 * IMPORTANT: Unlike 1.0.3, runs in the mixin config onLoad callback, not
 * Fabric's (often too late) preLaunch callback. Mixin selects all configs
 * before preparing them; inspect both queued configs and selected-but-not-yet-
 * prepared configs. Only remove Cosmetica's Fabric redirect mixin, never its
 * shared keybinding mixins. Add a separate @Inject to restore SNIPE registration.
 */
public final class CosmeticaEarlyConfigPlugin implements IMixinConfigPlugin {
    private boolean enabled;
    private static final String CONFIG_NAME = "cosmetica-fabric.mixins.json";
    private static final String TARGET_ENTRY = "KeyMappingMixin";
    private static final String TARGET_PACKAGE = "cc.cosmetica.cosmetica.fabric.mixin";

    @Override public void onLoad(String mixinPackage) {
        // This callback happens too early for latest.log to reliably capture stdout.
        // Record the decision; a Fabric client entrypoint logs it after initialization.
        final String reason;
        try {
            reason = compatibilityMismatch();
        } catch (Throwable error) {
            BridgeDiagnostics.error("could not check installed mod versions: " + describe(error));
            return;
        }
        if (reason != null) {
            BridgeDiagnostics.inactive(reason);
            return;
        }
        try {
            List<Object> configs = findConfigs();
            int found = 0;
            Object candidate = null;
            for (Object c : configs) {
                if (!CONFIG_NAME.equals(configName(c))) continue;
                found++;
                candidate = c;
            }
            if (found != 1) {
                BridgeDiagnostics.inactive("expected 1 Cosmetica config, found " + found);
                return;
            }
            if (Boolean.TRUE.equals(readField(candidate, "prepared"))) {
                BridgeDiagnostics.inactive("Cosmetica config was already prepared; redirect left intact");
                return;
            }
            Object packageValue = readField(candidate, "mixinPackage");
            if (!TARGET_PACKAGE.equals(String.valueOf(packageValue).replaceAll("\\.$", ""))) {
                BridgeDiagnostics.inactive("unexpected Cosmetica mixin package: " + packageValue);
                return;
            }
            Object clientMixins = readField(candidate, "mixinClassesClient");
            if (!(clientMixins instanceof List<?>)) {
                BridgeDiagnostics.inactive("Cosmetica client mixin list not available");
                return;
            }
            @SuppressWarnings("unchecked") List<Object> names = (List<Object>) clientMixins;
            if (!names.contains(TARGET_ENTRY)) {
                BridgeDiagnostics.inactive("Cosmetica redirect not found; no changes made");
                return;
            }
            if (!names.remove(TARGET_ENTRY)) {
                BridgeDiagnostics.error("could not remove Cosmetica redirect");
                return;
            }
            enabled = true;
            BridgeDiagnostics.active("removed Cosmetica KeyMappingMixin from unprepared config; "
                    + "Spell Engine redirect retained; DarkRPG Tweaks SNIPE shim permitted");
        } catch (Throwable error) {
            BridgeDiagnostics.error("Mixin configuration inspection failed: " + describe(error));
        }
    }

    private static String describe(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getSimpleName() + (message == null ? "" : " (" + message + ")");
    }

    private static List<Object> findConfigs() throws Exception {
        List<Object> result = new ArrayList<>();
        // Queue contains mixin configs not yet selected, including configs
        // registered by Fabric after ours. During selectConfigs() iteration,
        // some configs may already have been moved into pendingConfigs.
        Class<?> mixins = Class.forName("org.spongepowered.asm.mixin.Mixins");
        Object queue = mixins.getMethod("getConfigs").invoke(null);
        addConfigs(result, queue, true);

        Class<?> environment = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment");
        Object env = environment.getMethod("getCurrentEnvironment").invoke(null);
        if (env != null) {
            Object transformer = environment.getMethod("getActiveTransformer").invoke(env);
            if (transformer != null) {
                Object processor = readField(transformer, "processor");
                if (processor != null) {
                    addConfigs(result, readField(processor, "pendingConfigs"), false);
                    addConfigs(result, readField(processor, "configs"), false);
                }
            }
        }
        return result;
    }

    private static void addConfigs(List<Object> list, Object objects, boolean wrapped) throws Exception {
        if (!(objects instanceof Iterable<?>)) return;
        for (Object obj : (Iterable<?>)objects) {
            if (obj == null) continue;
            Object config = wrapped ? obj.getClass().getMethod("getConfig").invoke(obj) : obj;
            if (config != null && !containsIdentity(list, config)) list.add(config);
        }
    }

    private static boolean containsIdentity(Collection<Object> list, Object needle) {
        for (Object value : list) if (value == needle) return true;
        return false;
    }

    private static String configName(Object config) throws Exception {
        // MixinConfig#getName() is public, but the class itself is package-private.
        // Using a declared method and setAccessible avoids IllegalAccessException.
        Method method = findMethod(config.getClass(), "getName");
        if (method == null) return "";
        method.setAccessible(true);
        return (String)method.invoke(config);
    }

    private static Method findMethod(Class<?> type, String name) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try { return c.getDeclaredMethod(name); } catch (NoSuchMethodException ignored) { }
        }
        return null;
    }

    private static Object readField(Object instance, String name) throws Exception {
        if (instance == null) return null;
        for (Class<?> c = instance.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field field = c.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(instance);
            } catch (NoSuchFieldException ignored) { }
        }
        return null;
    }

    // null means this is the exact tested combination. A non-null reason
    // represents an intentional no-op and is logged at Fabric client startup.
    private static String compatibilityMismatch() throws ReflectiveOperationException {
        Class<?> api = Class.forName("net.fabricmc.loader.api.FabricLoader");
        Object loader = api.getMethod("getInstance").invoke(null);
        if (!"CLIENT".equals(String.valueOf(api.getMethod("getEnvironmentType").invoke(loader)))) {
            return "dedicated server: client-side compatibility bridge not needed";
        }
        String mc = version(api, loader, "minecraft");
        String spell = version(api, loader, "spell_engine");
        String cosmetics = version(api, loader, "cosmetica");
        if ("1.20.1".equals(mc)
                && "1.10.10+1.20.1".equals(spell)
                && "2.0.1-1.20.1".equals(cosmetics)) return null;
        return "version mismatch / mod absent (minecraft=" + mc
                + ", spell_engine=" + spell + ", cosmetica=" + cosmetics + ")";
    }

    private static String version(Class<?> loaderClass, Object loader, String id) throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        Optional<Object> container = (Optional<Object>) loaderClass.getMethod("getModContainer",String.class).invoke(loader,id);
        if (container.isEmpty()) return "";
        Object metadata = Class.forName("net.fabricmc.loader.api.ModContainer").getMethod("getMetadata").invoke(container.get());
        Object v = Class.forName("net.fabricmc.loader.api.metadata.ModMetadata").getMethod("getVersion").invoke(metadata);
        return (String)Class.forName("net.fabricmc.loader.api.Version").getMethod("getFriendlyString").invoke(v);
    }


    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return enabled; }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> mine, Set<String> other) { }
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String target, ClassNode cls, String mixin, IMixinInfo info) { }
    @Override public void postApply(String target, ClassNode cls, String mixin, IMixinInfo info) { }
}
