package de.kb1000.multiwindow.accessor.client;

import de.kb1000.multiwindow.client.gui.ScreenBreakout;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface ScreenAccessor {
    @NotNull Identifier multi_window_getBreakoutId();
    @NotNull ScreenBreakout multi_window_getBreakout();
    Screen multi_window_getParentScreen();
}
