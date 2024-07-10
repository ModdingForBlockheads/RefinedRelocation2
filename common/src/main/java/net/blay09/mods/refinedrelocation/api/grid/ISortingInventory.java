package net.blay09.mods.refinedrelocation.api.grid;

import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import org.jetbrains.annotations.Nullable;

public interface ISortingInventory extends ISortingGridMember {

    @Nullable Container getContainer();

    SimpleFilter getFilter();

    void setPriority(int priority);

    int getPriority();

    void onSlotChanged(int slotIndex);

    void deserialize(CompoundTag tag);

    CompoundTag serialize();
}
