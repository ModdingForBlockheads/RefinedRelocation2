package net.blay09.mods.refinedrelocation.api.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RefinedRelocationClientAPI {

    private static InternalClientMethods internalMethods;

    public static void __internal__setupAPI(InternalClientMethods internalMethods) {
        RefinedRelocationClientAPI.internalMethods = internalMethods;
    }

    public static Button createOpenFilterButton(AbstractContainerScreen<?> guiContainer, BlockEntity blockEntity, int rootFilterIndex) {
        return internalMethods.createOpenFilterButton(guiContainer, blockEntity, rootFilterIndex);
    }

}