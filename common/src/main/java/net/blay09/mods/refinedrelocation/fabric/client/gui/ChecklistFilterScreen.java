package net.blay09.mods.refinedrelocation.fabric.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IFilterPreviewScreen;
import net.blay09.mods.refinedrelocation.api.filter.ChecklistFilter;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.ScrollBarWidget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.ScrollPaneWidget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.IScrollTarget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.element.ChecklistEntryButton;
import net.blay09.mods.refinedrelocation.menu.ChecklistFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class ChecklistFilterScreen extends FilterScreen<ChecklistFilterMenu> implements IScrollTarget, IFilterPreviewScreen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/checklist_filter.png");

    private final ChecklistFilter filter;
    private final ChecklistEntryButton[] entries = new ChecklistEntryButton[7];

    private int currentOffset;

    public ChecklistFilterScreen(ChecklistFilterMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        this.filter = container.getFilter();
        imageHeight = 210;
        titleLabelX = 8;
        titleLabelY= 6;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 96 + 2;
    }

    @Override
    public void init() {
        super.init();

        ScrollBarWidget scrollBar = new ScrollBarWidget(leftPos + imageWidth - 16, topPos + 28, 75, this);
        addRenderableWidget(scrollBar);

        ScrollPaneWidget scrollPane = new ScrollPaneWidget(scrollBar, leftPos + 8, topPos + 28, 152, 80);
        addWidget(scrollPane);

        int y = topPos + 28;
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new ChecklistEntryButton(leftPos + 8, y, filter);
            addRenderableWidget(entries[i]);
            y += entries[i].getHeight();
        }

        setCurrentOffset(0);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public int getVisibleRows() {
        return entries.length;
    }

    @Override
    public int getRowCount() {
        return filter.getOptionCount();
    }

    @Override
    public int getCurrentOffset() {
        return currentOffset;
    }

    @Override
    public void setCurrentOffset(int offset) {
        this.currentOffset = offset;
        for (int i = 0; i < entries.length; i++) {
            int optionIndex = currentOffset + i;
            if (optionIndex >= filter.getOptionCount()) {
                optionIndex = -1;
            }
            entries[i].setCurrentOption(optionIndex);
        }
    }

    @Override
    public AbstractContainerMenu getFilterContainer() {
        return menu;
    }

    @Override
    public int getFilterLeftPos() {
        return leftPos;
    }

    @Override
    public int getFilterTopPos() {
        return topPos;
    }
}
