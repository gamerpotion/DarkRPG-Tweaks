package darkrpg.tweaks.menu;

import net.minecraft.class_310;
import net.minecraft.class_332;

/** Draws the client-only label after the title screen is rendered. */
public final class MenuVersionRenderer {
    private static final int WHITE = 0xFFFFFF;

    private MenuVersionRenderer() { }

    public static void render(class_332 context) {
        MenuVersionConfig config = MenuVersionConfig.get();
        if (!config.enabled) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1772 == null) {
            return;
        }
        context.method_25303(client.field_1772, "DarkRPG Version: " + config.darkRpgVersion,
                config.x, config.y, WHITE);
        context.method_25303(client.field_1772, "DarkRPG Tweaks Version: " + config.tweaksVersion,
                config.x, config.y + 11, WHITE);
    }
}
