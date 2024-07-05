package net.blay09.mods.refinedrelocation.fabric.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.client.IFilterPreviewScreen;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.ScrollBarWidget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.ScrollPaneWidget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.MultiLineTextFieldWidget;
import net.blay09.mods.refinedrelocation.menu.NameFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class NameFilterScreen extends FilterScreen<NameFilterMenu> implements IFilterPreviewScreen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/root_filter.png");

    private static final int UPDATE_INTERVAL = 20;

    private MultiLineTextFieldWidget filterTextWidget;

    private int ticksSinceUpdate;
    private String lastSentText = "";

    public NameFilterScreen(NameFilterMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);

        imageHeight = 210;
        inventoryLabelY = imageHeight - 96 + 2;
        shouldKeyRepeat = true;
    }

    @Override
    public void init() {
        super.init();

        String initialText = filterTextWidget != null ? filterTextWidget.getValue() : menu.getValue();
        filterTextWidget = new MultiLineTextFieldWidget(leftPos + 8, topPos + 20, 150, 84);
        filterTextWidget.setMaxLength(Short.MAX_VALUE);
        filterTextWidget.setCanLoseFocus(false);
        filterTextWidget.setValue(initialText);
        filterTextWidget.addHistoryEntry(initialText);
        addRenderableWidget(filterTextWidget);
        setInitialFocus(filterTextWidget);

        ScrollBarWidget scrollBar = new ScrollBarWidget(leftPos + 161, topPos + 20, 83, filterTextWidget);
        addRenderableWidget(scrollBar);

        ScrollPaneWidget scrollPane = new ScrollPaneWidget(scrollBar, leftPos + 8, topPos + 20, 150, 84);
        addRenderableWidget(scrollPane);
    }

    @Override
    public void containerTick() {
        super.containerTick();

        // Sync from Server
        if (menu.doesGuiNeedUpdate()) {
            filterTextWidget.setValue(menu.getValue());
            lastSentText = menu.getValue();
            menu.markGuiNeedsUpdate(false);
        }

        // Sync to Server
        ticksSinceUpdate++;
        if (ticksSinceUpdate >= UPDATE_INTERVAL) {
            if (!lastSentText.equals(filterTextWidget.getValue())) {
                menu.sendValueToServer(filterTextWidget.getValue());
                lastSentText = filterTextWidget.getValue();
            }
            ticksSinceUpdate = 0;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean onGuiAboutToClose() {
        super.onGuiAboutToClose();
        menu.sendValueToServer(filterTextWidget.getValue());
        RefinedRelocationAPI.returnToParentContainer();
        return false;
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
