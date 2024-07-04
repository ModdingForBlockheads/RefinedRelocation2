package net.blay09.mods.refinedrelocation.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public abstract class AbstractFilterMenu extends AbstractBaseMenu {
    protected AbstractFilterMenu(@Nullable MenuType<?> type, int windowId) {
        super(type, windowId);
    }

    public abstract BlockEntity getBlockEntity();

    public abstract int getRootFilterIndex();
}
