package net.blay09.mods.refinedrelocation.api.client;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IFilterPreviewScreen {
    AbstractContainerMenu getFilterContainer();

    int getFilterLeftPos();

    int getFilterTopPos();
}
