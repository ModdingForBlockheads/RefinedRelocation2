package net.blay09.mods.refinedrelocation.client.gui.base.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.base.ITickableElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationSupplier;
import net.minecraft.network.chat.Component;

public class ScrollBarWidget extends Button implements ITickableElement {

    private final IDrawable scrollbarTop;
    private final IDrawable scrollbarMiddle;
    private final IDrawable scrollbarBottom;
    private final IScrollTarget scrollTarget;

    private int barY;
    private int barHeight;
    private int indexWhenClicked;
    private int lastNumberOfMoves;
    private double mouseClickY = -1;

    private int lastRowCount;
    private int lastVisibleRows;
    private int lastOffset;

    public ScrollBarWidget(int x, int y, int height, IScrollTarget scrollTarget) {
        super(x, y, 7, height, Component.empty(), it -> {
        }, DEFAULT_NARRATION);
        this.scrollTarget = scrollTarget;
        updateBarPosition();

        scrollbarTop = GuiTextures.SCROLLBAR_TOP;
        scrollbarMiddle = GuiTextures.SCROLLBAR_MIDDLE;
        scrollbarBottom = GuiTextures.SCROLLBAR_BOTTOM;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (isMouseOver(mouseX, mouseY)) {
            forceMouseScrolled(deltaY);
            return true;
        }

        return false;
    }

    public void forceMouseScrolled(double delta) {
        setCurrentOffset(delta > 0 ? scrollTarget.getCurrentOffset() - 1 : scrollTarget.getCurrentOffset() + 1);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (mouseClickY != -1) {
            mouseClickY = -1;
            indexWhenClicked = 0;
            lastNumberOfMoves = 0;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (mouseX >= getX() && mouseX < getX() + getWidth() && mouseY >= barY && mouseY < barY + barHeight) {
            mouseClickY = mouseY;
            indexWhenClicked = scrollTarget.getCurrentOffset();
        }
    }

    private void updateBarPosition() {
        barHeight = (int) (height * Math.min(1f, ((float) scrollTarget.getVisibleRows() / (Math.ceil(scrollTarget.getRowCount())))));
        barY = getY() + ((height - barHeight) * scrollTarget.getCurrentOffset() / Math.max(1,
                (int) Math.ceil((scrollTarget.getRowCount())) - scrollTarget.getVisibleRows()));
    }

    @Override
    public void tick() {
        if (lastRowCount != scrollTarget.getRowCount() || lastVisibleRows != scrollTarget.getVisibleRows() || lastOffset != scrollTarget.getCurrentOffset()) {
            updateBarPosition();
            lastRowCount = scrollTarget.getRowCount();
            lastVisibleRows = scrollTarget.getVisibleRows();
            lastOffset = scrollTarget.getCurrentOffset();
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (mouseClickY != -1) {
            float pixelsPerFilter = (height - barHeight) / (float) Math.max(1, (int) Math.ceil(scrollTarget.getRowCount()) - scrollTarget.getVisibleRows());
            if (pixelsPerFilter != 0) {
                int numberOfFiltersMoved = (int) ((mouseY - mouseClickY) / pixelsPerFilter);
                if (numberOfFiltersMoved != lastNumberOfMoves) {
                    setCurrentOffset(indexWhenClicked + numberOfFiltersMoved);
                    lastNumberOfMoves = numberOfFiltersMoved;
                }
            }
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        scrollbarTop.draw(guiGraphics, getX() - 2, getY() - 1);
        scrollbarBottom.draw(guiGraphics, getX() - 2, getY() + height - 1);
        scrollbarMiddle.draw(guiGraphics, getX() - 2, getY() + 2, 11, height - 3);

        guiGraphics.fill(getX(), barY, getX() + getWidth(), barY + barHeight, 0xFFAAAAAA);
    }

    public void setCurrentOffset(int offset) {
        int currentOffset = Math.max(0, Math.min(offset, (int) (Math.ceil(scrollTarget.getRowCount()) - scrollTarget.getVisibleRows())));
        scrollTarget.setCurrentOffset(currentOffset);
        updateBarPosition();
    }

}
