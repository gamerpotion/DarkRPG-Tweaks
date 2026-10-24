package darkrpg.tweaks.menu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Lazy client-side properties config. It does not run on dedicated servers and
 * existing files are never overwritten when the mod version is updated.
 */
public final class MenuVersionConfig {
    private static final String FILE_NAME = "darkrpg-tweaks-menu.properties";
    private static final String DEFAULT_FILE =
            "# DarkRPG Tweaks main menu version display\n"
          + "# Edit the two versions below whenever you update your modpack.\n"
          + "# Restart Minecraft after changing this file.\n"
          + "# Include this file in your CurseForge modpack overrides/config folder.\n"
          + "darkrpg_version=9.1.5\n"
          + "darkrpg_tweaks_version=1.0.0-beta4\n"
          + "enabled=true\n"
          + "x=8\n"
          + "y=8\n";

    private static volatile MenuVersionConfig current;

    public final String darkRpgVersion;
    public final String tweaksVersion;
    public final boolean enabled;
    public final int x;
    public final int y;

    private MenuVersionConfig(String darkRpgVersion, String tweaksVersion,
                              boolean enabled, int x, int y) {
        this.darkRpgVersion = darkRpgVersion;
        this.tweaksVersion = tweaksVersion;
        this.enabled = enabled;
        this.x = x;
        this.y = y;
    }

    public static MenuVersionConfig get() {
        MenuVersionConfig result = current;
        if (result == null) {
            synchronized (MenuVersionConfig.class) {
                result = current;
                if (result == null) {
                    result = load(FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME));
                    current = result;
                }
            }
        }
        return result;
    }

    static MenuVersionConfig load(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createDirectories(file.getParent());
                Files.writeString(file, DEFAULT_FILE, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            } catch (FileAlreadyExistsException ignored) {
                // Another process/thread may have created it in the meantime.
            } catch (IOException e) {
                System.err.println("[DarkRPG Tweaks] Could not create menu version config: " + e);
            }
        }

        Properties properties = new Properties();
        if (Files.isRegularFile(file)) {
            try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("[DarkRPG Tweaks] Could not read menu version config: " + e);
            }
        }
        return new MenuVersionConfig(
                text(properties, "darkrpg_version", "9.1.5"),
                text(properties, "darkrpg_tweaks_version", "1.0.0-beta4"),
                Boolean.parseBoolean(properties.getProperty("enabled", "true").trim()),
                number(properties, "x", 8),
                number(properties, "y", 8)
        );
    }

    private static String text(Properties properties, String key, String fallback) {
        String value = properties.getProperty(key, fallback).trim();
        return value.isEmpty() ? fallback : value.replace('\n', ' ').replace('\r', ' ');
    }

    private static int number(Properties properties, String key, int fallback) {
        try {
            return Math.max(0, Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)).trim()));
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
