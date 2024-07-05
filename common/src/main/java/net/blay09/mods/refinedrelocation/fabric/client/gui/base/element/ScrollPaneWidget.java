package net.blay09.mods.refinedrelocation.fabric.client.gui.base.element;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ScrollPaneWidget extends AbstractWidget {

    private final ScrollBarWidget scrollBar;

    public ScrollPaneWidget(ScrollBarWidget scrollBar, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.scrollBar = scrollBar;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY)) {
            scrollBar.forceMouseScrolled(deltaY);
            return true;
        }

        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    }


    @Override
    public void updateWidgetNarration(NarrationElementOutput output) {
    }
}
