package net.blay09.mods.refinedrelocation.client.gui.element;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ImageButton;
import net.blay09.mods.refinedrelocation.network.RequestFilterScreenMessage;
import net.minecraft.world.level.block.entity.BlockEntity;

public class OpenFilterButton extends ImageButton {

    public OpenFilterButton(int x, int y, BlockEntity blockEntity, int rootFilterIndex) {
        super(x, y, 12, 12, GuiTextures.OPEN_FILTER, it -> Balm.getNetworking().sendToServer(new RequestFilterScreenMessage(blockEntity.getBlockPos(), rootFilterIndex)));
    }

}
