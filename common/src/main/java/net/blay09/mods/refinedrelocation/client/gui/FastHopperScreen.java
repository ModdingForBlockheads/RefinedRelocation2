package net.blay09.mods.refinedrelocation.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.client.gui.base.ModContainerScreen;
import net.blay09.mods.refinedrelocation.client.gui.element.OpenFilterButton;
import net.blay09.mods.refinedrelocation.menu.FastHopperMenu;
import net.blay09.mods.refinedrelocation.block.entity.FastHopperBlockEntity;
import net.blay09.mods.refinedrelocation.block.entity.FilteredHopperBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FastHopperScreen extends ModContainerScreen<FastHopperMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/hopper.png");

    private final FastHopperBlockEntity tileEntity;

    public FastHopperScreen(FastHopperMenu container, Inventory inventory, Component displayName) {
        super(container, inventory, displayName);
        this.tileEntity = container.getBlockEntity();
        this.imageHeight = 133;
        inventoryLabelY = imageHeight - 96 + 2;
    }

    @Override
    public void init() {
        super.init();

        if (tileEntity instanceof FilteredHopperBlockEntity) {
            addRenderableWidget(new OpenFilterButton(leftPos + imageWidth - 20, topPos + 4, tileEntity, 0));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

}
