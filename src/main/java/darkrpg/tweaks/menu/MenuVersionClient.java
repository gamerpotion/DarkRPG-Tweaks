package darkrpg.tweaks.menu;

import java.util.Locale;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_442;

/**
 * Client-only title/menu-screen version overlay.
 *
 * Register AFTER_RENDER per screen rather than drawing from the TitleScreen
 * itself; DarkRPG may use a custom menu in place of vanilla's title screen.
 * Never reference this entrypoint from the common/server initializer.
 */
public final class MenuVersionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isMainMenu(client, screen)) {
                return;
            }
            ScreenEvents.afterRender(screen).register((renderedScreen, drawContext, mouseX, mouseY, tickDelta) -> {
                if (client.field_1755 == screen && client.field_1687 == null) {
                    MenuVersionRenderer.render(drawContext);
                }
            });
        });
        System.out.println("[DarkRPG Tweaks] Configurable main-menu version overlay registered");
    }

    static boolean isMainMenu(class_310 client, class_437 screen) {
        if (client == null || client.field_1687 != null || screen == null) {
            return false;
        }
        if (screen instanceof class_442) {
            return true;
        }
        String simpleName = screen.getClass().getSimpleName().toLowerCase(Locale.ROOT);
        return simpleName.contains("titlescreen")
                || simpleName.contains("mainmenu")
                || simpleName.contains("mainscreen");
    }
}
