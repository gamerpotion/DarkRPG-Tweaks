package net.fabricmc.loader.api;
import java.util.*;
import net.fabricmc.loader.api.metadata.ModMetadata;
/** Fake Fabric API with the TRUE interface bytecode shape of Fabric Loader 0.20.1. */
public interface FabricLoader {
    static FabricLoader getInstance() { return Impl.INSTANCE; }
    Map<String,String> mods = new HashMap<>();
    java.nio.file.Path getConfigDir();
    Object getEnvironmentType();
    boolean isModLoaded(String id);
    Optional<ModContainer> getModContainer(String id);
    final class Impl implements FabricLoader {
        private static final Impl INSTANCE = new Impl();
        public java.nio.file.Path getConfigDir() {return java.nio.file.Path.of(System.getProperty("darkrpgtweaks.test.config", "/mnt/data/nonexistent"));}
        public Object getEnvironmentType() { return FakeEnvironment.side; }
        public boolean isModLoaded(String id) { return mods.containsKey(id); }
        public Optional<ModContainer> getModContainer(String id) {
            String version = mods.get(id);
            if (version == null) return Optional.empty();
            return Optional.of(new Container(version));
        }
    }
    record Container(String v) implements ModContainer {
        public ModMetadata getMetadata() { return new Metadata(v); }
    }
    record Metadata(String v) implements ModMetadata {
        public Version getVersion() { return new V(v); }
    }
    record V(String v) implements Version {
        public String getFriendlyString() { return v; }
    }
}
