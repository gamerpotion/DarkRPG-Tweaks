package darkrpg.tweaks.compat.backpack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import net.minecraft.class_490;
import net.minecraft.class_4185;
import net.minecraft.class_7919;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_4587;

/**
 * Client-only Traveler's Backpack 9.1.56 compatibility button for DarkRPG.
 * Keeps the separate backpack equipment system (Trinkets integration OFF).
 * Hides Traveler's Backpack's original inventory icon only after our own button
 * has been successfully installed. Uses the backpack's existing server packet.
 * This optional GUI integration fails closed without crashing Minecraft.
 */
public final class BackpackInventoryButtonClient implements ClientModInitializer {
    private static final String BACKPACK_VERSION = "9.1.56";
    private static final int BUTTON_OFFSET_X = 123;
    private static final int BUTTON_OFFSET_Y = 71;
    private static final int BUTTON_SIZE = 10;
    private static volatile boolean failureLogged;
    private static volatile class_1799 genericBackpackIcon;
    private static volatile Method isWearingBackpack;

    @Override
    public void onInitializeClient() {
        try {
            FabricLoader loader = FabricLoader.getInstance();
            if (!loader.isModLoaded("travelersbackpack")
                    || !BACKPACK_VERSION.equals(loader.getModContainer("travelersbackpack")
                        .map(container -> container.getMetadata().getVersion().getFriendlyString())
                        .orElse(""))) {
                return;
            }
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (!(screen instanceof class_490) || client.field_1724 == null) {
                    return;
                }
                try {
                    installButton(client, (class_490) screen);
                } catch (RuntimeException | LinkageError problem) {
                    logOnce("Unable to install backpack inventory button", problem);
                }
            });
        } catch (RuntimeException | LinkageError problem) {
            // An optional icon must never prevent a modpack from starting.
            logOnce("Unable to register backpack inventory compatibility", problem);
        }
    }

    private static void installButton(class_310 client, class_490 screen) {
        int x = (screen.field_22789 - 176) / 2 + BUTTON_OFFSET_X;
        int y = (screen.field_22790 - 166) / 2 + BUTTON_OFFSET_Y;
        class_4185 button = class_4185.method_46430(class_2561.method_43470(""), pressed -> {
            try {
                if (isWearingBackpack(client)) {
                    openEquippedBackpack();
                }
            } catch (RuntimeException | LinkageError problem) {
                logOnce("Unable to open equipped backpack", problem);
            }
        }).method_46434(x, y, BUTTON_SIZE, BUTTON_SIZE)
          .method_46436(class_7919.method_47407(class_2561.method_43470("Open equipped Traveler's Backpack")))
          .method_46431();
        button.field_22763 = isWearingBackpack(client);

        ScreenEvents.afterTick(screen).register(s -> {
            try {
                button.field_22763 = isWearingBackpack(client);
            } catch (RuntimeException | LinkageError problem) {
                button.field_22763 = false;
                logOnce("Unable to update backpack button", problem);
            }
        });
        ScreenEvents.afterRender(screen).register((s, draw, mouseX, mouseY, tickDelta) -> {
            try {
                if (button.field_22764) {
                    class_1799 icon = getGenericBackpackIcon();
                    if (icon != null) {
                        // Vanilla item models are 16x16; scale to 8x8 for an IPN-sized control.
                        class_4587 matrices = draw.method_51448();
                        matrices.method_22903();
                        try {
                            matrices.method_22904(x + 1.0, y + 1.0, 0.0);
                            matrices.method_22905(0.5F, 0.5F, 1.0F);
                            draw.method_51427(icon, 0, 0);
                        } finally {
                            matrices.method_22909();
                        }
                    }
                }
            } catch (RuntimeException | LinkageError problem) {
                logOnce("Unable to draw backpack button", problem);
            }
        });
        Screens.getButtons(screen).add(button);
        // Only disable Traveler's Backpack's original icon once this new button exists.
        hideOriginalIcon();
    }

    private static void hideOriginalIcon() {
        try {
            Class<?> config = Class.forName("com.tiviacz.travelersbackpack.config.TravelersBackpackConfig");
            Object data = config.getMethod("getConfig").invoke(null);
            Object client = data.getClass().getField("client").get(data);
            Field show = client.getClass().getField("showBackpackIconInInventory");
            if (show.getBoolean(client)) {
                show.setBoolean(client, false);
                config.getMethod("saveConfig").invoke(null);
            }
        } catch (ReflectiveOperationException | LinkageError problem) {
            logOnce("Could not hide Traveler's Backpack original icon", problem);
        }
    }

    private static boolean isWearingBackpack(class_310 client) {
        if (client.field_1724 == null) return false;
        try {
            Method query = isWearingBackpack;
            if (query == null) {
                // Verified against ComponentUtils from Traveler's Backpack 9.1.56.
                Class<?> components = Class.forName("com.tiviacz.travelersbackpack.component.ComponentUtils");
                for (Method method : components.getMethods()) {
                    if (method.getName().equals("isWearingBackpack") && method.getParameterCount() == 1) {
                        isWearingBackpack = query = method;
                        break;
                    }
                }
            }
            return query != null && Boolean.TRUE.equals(query.invoke(null, client.field_1724));
        } catch (ReflectiveOperationException | LinkageError problem) {
            logOnce("Could not check equipped backpack", problem);
            return false;
        }
    }

    private static void openEquippedBackpack() {
        try {
            // Traveler's Backpack keybind dispatches OPEN_SCREEN=1; create() sends the packet.
            Class.forName("com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket")
                .getMethod("create", int.class, Object[].class)
                .invoke(null, 1, (Object) new Object[0]);
        } catch (ReflectiveOperationException | LinkageError problem) {
            logOnce("Could not open equipped backpack", problem);
        }
    }

    private static class_1799 getGenericBackpackIcon() {
        if (genericBackpackIcon != null) return genericBackpackIcon;
        try {
            Object item = Class.forName("com.tiviacz.travelersbackpack.init.ModItems")
                .getField("STANDARD_TRAVELERS_BACKPACK").get(null);
            if (item instanceof class_1792 backpack) {
                genericBackpackIcon = backpack.method_7854();
            }
        } catch (ReflectiveOperationException | LinkageError problem) {
            logOnce("Could not obtain Traveler's Backpack button icon", problem);
        }
        return genericBackpackIcon;
    }

    private static void logOnce(String message, Throwable problem) {
        if (!failureLogged) {
            failureLogged = true;
            System.err.println("[DarkRPG Tweaks] " + message + ": " + problem);
        }
    }
}
