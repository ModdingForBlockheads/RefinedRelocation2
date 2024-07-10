package net.blay09.mods.refinedrelocation.grid;

import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class SortingInventoryDelegate extends SortingInventory implements ISortingInventory {

    @Nullable
    private Container container;

    public SortingInventoryDelegate(BlockEntity blockEntity) {
        super(blockEntity);
    }

    public void setContainer(@Nullable Container container) {
        this.container = container;
    }

    @Override
    public @Nullable Container getContainer() {
        return container;
    }
}
