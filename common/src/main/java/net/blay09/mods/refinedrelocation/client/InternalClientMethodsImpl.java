package net.blay09.mods.refinedrelocation.client;

import net.blay09.mods.balm.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.refinedrelocation.api.client.InternalClientMethods;
import net.blay09.mods.refinedrelocation.client.gui.element.OpenFilterButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.block.entity.BlockEntity;

public class InternalClientMethodsImpl implements InternalClientMethods {
    @Override
    public Button createOpenFilterButton(AbstractContainerScreen<?> guiContainer, BlockEntity blockEntity, int rootFilterIndex) {
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) guiContainer;
        return new OpenFilterButton(accessor.getLeftPos() + accessor.getImageWidth() - 18, accessor.getTopPos() + 4, blockEntity, rootFilterIndex);
    }
}
