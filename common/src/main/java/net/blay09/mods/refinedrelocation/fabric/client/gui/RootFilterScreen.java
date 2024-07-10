package net.blay09.mods.refinedrelocation.fabric.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.client.IFilterPreviewScreen;
import net.blay09.mods.refinedrelocation.api.grid.SortingInventory;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.element.LabelWidget;
import net.blay09.mods.refinedrelocation.fabric.client.gui.element.*;
import net.blay09.mods.refinedrelocation.menu.RootFilterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RootFilterScreen extends FilterScreen<RootFilterMenu> implements IFilterPreviewScreen {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/root_filter.png");
    private static final ResourceLocation TEXTURE_NO_PRIORITY = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/root_filter_no_priority.png");
    private static final int UPDATE_INTERVAL = 20;

    private final IDrawable textureSeparator = GuiTextures.FILTER_SEPARATOR;
    private final Inventory inventory;

    private int ticksSinceUpdate;
    private int lastSentPriority;

    public RootFilterScreen(RootFilterMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        this.inventory = inventory;

        if (container.hasSortingInventory()) {
            imageHeight = 210;
        }
    }

    public Inventory getPlayerInventory() {
        return inventory;
    }

    @Override
    public void init() {
        super.init();

        final FilterSlotButton[] filterSlots = new FilterSlotButton[3];
        final DeleteFilterButton[] deleteButtons = new DeleteFilterButton[3];
        final AllowListButton[] whitelistButtons = new AllowListButton[3];

        int x = leftPos + 10;
        for (int i = 0; i < filterSlots.length; i++) {
            filterSlots[i] = new FilterSlotButton(x, topPos + 30, this, menu.getRootFilter(), i);
            addRenderableWidget(filterSlots[i]);

            deleteButtons[i] = new DeleteFilterButton(x + 19, topPos + 27, filterSlots[i]);
            addRenderableWidget(deleteButtons[i]);

            whitelistButtons[i] = new AllowListButton(x + 1, topPos + 55, this, filterSlots[i]);
            addRenderableWidget(whitelistButtons[i]);
            x += 40;
        }

        if (menu.canReturnFromFilter()) {
            addRenderableWidget(new ReturnFromFilterButton(leftPos + imageWidth - 20, topPos + 4));
        }

        if (menu.hasSortingInventory()) {
            addRenderableWidget(new LabelWidget(font,
                    leftPos + 10,
                    topPos + 65,
                    Component.translatable("gui.refinedrelocation:root_filter.priority_label"),
                    0x404040));
            addRenderableWidget(new PriorityButton(leftPos + 10, topPos + 80, 100, 20, menu.getSortingInventory()));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();

        ticksSinceUpdate++;
        if (ticksSinceUpdate >= UPDATE_INTERVAL) {
            SortingInventory sortingInventory = menu.getSortingInventory();
            if (lastSentPriority != sortingInventory.getPriority()) {
                RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_PRIORITY, sortingInventory.getPriority());
                lastSentPriority = sortingInventory.getPriority();
            }
            ticksSinceUpdate = 0;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        final var texture = menu.hasSortingInventory() ? TEXTURE : TEXTURE_NO_PRIORITY;
        guiGraphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        final int x = leftPos + 10;
        final int y = topPos + 37;
        RenderSystem.enableBlend();
        textureSeparator.draw(guiGraphics, x + 30, y);
        textureSeparator.draw(guiGraphics, x + 70, y);
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        BlockEntity blockEntity = menu.getBlockEntity();
        Component displayName = null;
        if (blockEntity instanceof Nameable nameable) {
            displayName = nameable.getDisplayName();
        }

        final var title = displayName != null ? Component.translatable("container.refinedrelocation:root_filter_with_name",
                displayName) : Component.translatable("container.refinedrelocation:root_filter");
        guiGraphics.drawString(font, title.getVisualOrderText(), 8, 6, 4210752);
        guiGraphics.drawString(font, I18n.get("container.inventory"), 8, imageHeight - 96 + 2, 4210752);
    }

    @Override
    public boolean onGuiAboutToClose() {
        RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_PRIORITY, menu.getSortingInventory().getPriority());
        return true;
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

    @Override
    protected boolean hasOpenFilterButton() {
        return false;
    }
}
