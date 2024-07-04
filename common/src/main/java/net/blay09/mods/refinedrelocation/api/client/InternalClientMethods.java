package net.blay09.mods.refinedrelocation.api.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface InternalClientMethods {
    Button createOpenFilterButton(AbstractContainerScreen<?> guiContainer, BlockEntity blockEntity, int rootFilterIndex);
}
