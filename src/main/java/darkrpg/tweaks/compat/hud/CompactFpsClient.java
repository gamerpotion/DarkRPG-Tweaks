package darkrpg.tweaks.compat.hud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_4587;

/**
 * Compact right-aligned FPS HUD for Minecraft 1.20.1.
 * Does not interact with Sodium Extra or Traveler's Backpack's own HUD.
 * Disable Sodium Extra's larger FPS display separately to avoid duplication.
 */
public final class CompactFpsClient implements ClientModInitializer {
    private static final float SCALE = 0.65F;
    private static final int RIGHT_PADDING = 1;
    private static final int BOTTOM_PADDING = 0;
    private static final int COLOR = 0xFFFFFF;
    private static boolean failureLogged;

    @Override
    public void onInitializeClient() {
        try {
            HudRenderCallback.EVENT.register(CompactFpsClient::render);
        } catch (RuntimeException | LinkageError failure) {
            logOnce("Cannot register compact FPS HUD", failure);
        }
    }

    private static void render(class_332 drawContext, float tickDelta) {
        try {
            class_310 client = class_310.method_1551();
            if (client == null || client.field_1724 == null || client.field_1755 != null
                    || client.field_1690.field_1842 || client.field_1690.field_1866) {
                return;
            }
            class_327 font = client.field_1772;
            if (font == null) return;

            String message = client.method_47599() + " FPS";
            int screenWidth = client.method_22683().method_4486();
            int screenHeight = client.method_22683().method_4502();
            double textWidth = font.method_1727(message) * SCALE;
            double textHeight = 9.0 * SCALE;
            double x = screenWidth - RIGHT_PADDING - textWidth;
            double y = screenHeight - BOTTOM_PADDING - textHeight;

            class_4587 matrices = drawContext.method_51448();
            matrices.method_22903();
            try {
                matrices.method_22904(x, y, 0.0);
                matrices.method_22905(SCALE, SCALE, 1.0F);
                drawContext.method_25303(font, message, 0, 0, COLOR);
            } finally {
                matrices.method_22909();
            }
        } catch (RuntimeException | LinkageError failure) {
            // Optional HUD should never be able to crash the Minecraft client.
            logOnce("Unable to draw compact FPS HUD", failure);
        }
    }

    private static void logOnce(String message, Throwable failure) {
        if (!failureLogged) {
            failureLogged = true;
            System.err.println("[DarkRPG Tweaks] " + message + ": " + failure);
        }
    }
}
