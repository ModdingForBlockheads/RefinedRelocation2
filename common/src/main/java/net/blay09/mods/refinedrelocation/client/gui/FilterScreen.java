package net.blay09.mods.refinedrelocation.client.gui;

import net.blay09.mods.refinedrelocation.client.gui.base.ModContainerScreen;
import net.blay09.mods.refinedrelocation.client.gui.element.OpenFilterButton;
import net.blay09.mods.refinedrelocation.menu.AbstractFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class FilterScreen<T extends AbstractFilterMenu> extends ModContainerScreen<T> {
    public FilterScreen(T menu, Inventory inventory, Component displayName) {
        super(menu, inventory, displayName);
    }

    @Override
    protected void init() {
        super.init();

        if (hasOpenFilterButton()) {
            addRenderableWidget(new OpenFilterButton(leftPos + imageWidth - 17, topPos + 5, menu.getBlockEntity(), menu.getRootFilterIndex()));
        }
    }

    protected boolean hasOpenFilterButton() {
        return true;
    }

}
