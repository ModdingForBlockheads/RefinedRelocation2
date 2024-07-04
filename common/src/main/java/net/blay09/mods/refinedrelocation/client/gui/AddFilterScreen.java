package net.blay09.mods.refinedrelocation.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.client.gui.base.ModContainerScreen;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ScrollBarWidget;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ScrollPaneWidget;
import net.blay09.mods.refinedrelocation.client.gui.base.element.IScrollTarget;
import net.blay09.mods.refinedrelocation.client.gui.element.AddFilterButton;
import net.blay09.mods.refinedrelocation.menu.AddFilterMenu;
import net.blay09.mods.refinedrelocation.filter.FilterRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class AddFilterScreen extends ModContainerScreen<AddFilterMenu> implements IScrollTarget {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/add_filter.png");

    private final AddFilterButton[] filterButtons = new AddFilterButton[3];
    private final List<IFilter> filterList;

    private int currentOffset;

    public AddFilterScreen(AddFilterMenu container, Inventory playerInventory, Component displayName) {
        super(container, playerInventory, displayName);
        filterList = FilterRegistry.getApplicableFilters(t -> t.isFilterUsable(container.getTileEntity()));
        imageHeight = 210;
        inventoryLabelY = imageHeight - 96 + 2;
    }

    @Override
    public void init() {
        super.init();

        ScrollBarWidget scrollBar = new ScrollBarWidget(leftPos + imageWidth - 16, topPos + 28, 78, this);
        addRenderableWidget(scrollBar);

        ScrollPaneWidget scrollPane = new ScrollPaneWidget(scrollBar, leftPos + 8, topPos + 28, 152, 80);
        addWidget(scrollPane);

        int y = topPos + 28;
        for (int i = 0; i < filterButtons.length; i++) {
            filterButtons[i] = new AddFilterButton(leftPos + 8, y);
            addRenderableWidget(filterButtons[i]);
            y += filterButtons[i].getHeight();
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
        return filterButtons.length;
    }

    @Override
    public int getRowCount() {
        return filterList.size();
    }

    @Override
    public int getCurrentOffset() {
        return currentOffset;
    }

    @Override
    public void setCurrentOffset(int offset) {
        this.currentOffset = offset;

        for (int i = 0; i < filterButtons.length; i++) {
            int filterIndex = currentOffset + i;
            IFilter filter = null;
            if (filterIndex >= 0 && filterIndex < filterList.size()) {
                filter = filterList.get(filterIndex);
            }
            filterButtons[i].setCurrentFilter(filter);
        }
    }

}
