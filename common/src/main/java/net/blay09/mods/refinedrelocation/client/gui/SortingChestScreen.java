package net.blay09.mods.refinedrelocation.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.client.gui.base.ModContainerScreen;
import net.blay09.mods.refinedrelocation.client.gui.element.OpenFilterButton;
import net.blay09.mods.refinedrelocation.menu.SortingChestMenu;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SortingChestScreen extends ModContainerScreen<SortingChestMenu> {

    private final SortingChestBlockEntity tileEntity;

    public SortingChestScreen(SortingChestMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        this.tileEntity = container.getTileEntity();
        this.imageWidth = tileEntity.getChestType().getGuiWidth();
        this.imageHeight = tileEntity.getChestType().getGuiHeight();
    }

    @Override
    public void init() {
        super.init();

        addRenderableWidget(new OpenFilterButton(leftPos + imageWidth - 20, topPos + 4, tileEntity, 0));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        SortingChestType chestType = tileEntity.getChestType();
        if (chestType == SortingChestType.WOOD || chestType == SortingChestType.IRON) {
            int inventoryRows = chestType.getInventorySize() / chestType.getContainerRowSize();
            guiGraphics.blit(chestType.getGuiTextureLocation(), leftPos, topPos, 0, 0, imageWidth, inventoryRows * 18 + 17);
            guiGraphics.blit(chestType.getGuiTextureLocation(), leftPos, topPos + inventoryRows * 18 + 17, 0, 125, imageWidth, 97);
        } else {
            int textureSizeX = chestType.getGuiTextureWidth();
            int textureSizeY = chestType.getGuiTextureHeight();
            guiGraphics.blit(chestType.getGuiTextureLocation(), leftPos, topPos, 0, 0, imageWidth, imageHeight, textureSizeX, textureSizeY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, getTitle().getVisualOrderText(), 8, 6, 4210752);

        int inventoryTitleX = (tileEntity.getChestType().getGuiWidth() - 162) / 2;
        guiGraphics.drawString(font, I18n.get("container.inventory"), inventoryTitleX, imageHeight - 96 + 2, 4210752);
    }

}
