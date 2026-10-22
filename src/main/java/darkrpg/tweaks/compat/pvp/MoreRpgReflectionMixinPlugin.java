package darkrpg.tweaks.compat.pvp;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/** Only enable the narrow PvP guard for the offending, inspected library build. */
public final class MoreRpgReflectionMixinPlugin implements IMixinConfigPlugin {
    private boolean applicable;

    @Override
    public void onLoad(String mixinPackage) {
        FabricLoader loader = FabricLoader.getInstance();
        applicable = "1.20.1".equals(versionOf(loader, "minecraft"))
            && "2.7.2+1.20.1".equals(versionOf(loader, "more_rpg_classes"));
    }

    private static String versionOf(FabricLoader loader, String id) {
        return loader.getModContainer(id)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("");
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return applicable
            && "net.minecraft.class_1309".equals(targetClassName)
            && "darkrpg.tweaks.compat.pvp.mixin.PvPReflectionLoopGuardMixin".equals(mixinClassName);
    }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass,
            String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass,
            String mixinClassName, IMixinInfo mixinInfo) {}
}
