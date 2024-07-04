package net.blay09.mods.refinedrelocation.client.gui.base.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class LabelWidget extends AbstractWidget {

    private final Font font;
    public int x;
    public int y;
    public Component text;
    public int textColor;

    public LabelWidget(Font font, int x, int y, Component text, int textColor) {
        super(x, y, font.width(text), font.lineHeight, text);
        this.font = font;
        this.x = x;
        this.y = y;
        this.text = text;
        this.textColor = textColor;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        guiGraphics.drawString(font, text, x, y, textColor);
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narration) {
    }
}
