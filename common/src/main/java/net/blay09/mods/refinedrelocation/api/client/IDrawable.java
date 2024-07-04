package net.blay09.mods.refinedrelocation.api.client;

import net.minecraft.client.gui.GuiGraphics;

public interface IDrawable {
    void draw(GuiGraphics guiGraphics, double x, double y);

    void draw(GuiGraphics guiGraphics, double x, double y, double width, double height);
}
