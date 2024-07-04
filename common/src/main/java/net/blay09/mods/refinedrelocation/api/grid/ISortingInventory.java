package net.blay09.mods.refinedrelocation.api.grid;

import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;

public interface ISortingInventory extends ISortingGridMember {

    Container getContainer();

    ISimpleFilter getFilter();

    void setPriority(int priority);

    int getPriority();

    void onSlotChanged(int slotIndex);

    void deserialize(CompoundTag tag);

    CompoundTag serialize();
}
