package net.blay09.mods.refinedrelocation.client.gui.element;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ImageButton;
import net.blay09.mods.refinedrelocation.network.ReturnToParentScreenMessage;
import net.blay09.mods.refinedrelocation.network.ModNetworking;

public class ReturnFromFilterButton extends ImageButton {

    public ReturnFromFilterButton(int x, int y) {
        super(x, y, 16, 16, GuiTextures.CHEST_BUTTON, it -> {
        });
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        Balm.getNetworking().sendToServer(new ReturnToParentScreenMessage());
    }

}
