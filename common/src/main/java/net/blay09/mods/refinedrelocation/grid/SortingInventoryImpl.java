package net.blay09.mods.refinedrelocation.grid;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.SortingInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

public class SortingInventoryImpl extends SortingGridMemberImpl implements SortingInventory {

    private final LinkedList<SortingStack> sortingStackList = Lists.newLinkedList();
    private SimpleFilter filter;
    private int priority;

    public SortingInventoryImpl(BlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public Container getContainer() {
        return Balm.getProviders().getProvider(getBlockEntity(), Container.class);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public SimpleFilter getFilter() {
        return filter;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        filter = Balm.getProviders().getProvider(getBlockEntity(), RootFilter.class);
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();
        if (!sortingStackList.isEmpty()) {
            SortingStack sortingStack = sortingStackList.removeFirst();
            ISortingGrid sortingGrid = getSortingGrid();
            if (sortingGrid != null) {
                sortingGrid.setSortingActive(true);
                ItemStack itemStack = sortingStack.getContainer().getItem(sortingStack.getSlotIndex());
                if (ItemStack.isSameItemSameComponents(itemStack, sortingStack.getItemStack())) {
                    RefinedRelocationAPI.insertIntoSortingGrid(this, sortingStack.getSlotIndex(), itemStack);
                }
                sortingGrid.setSortingActive(false);
            }
        }
    }

    @Override
    public void onSlotChanged(int slotIndex) {
        if (getSortingGrid() == null || getSortingGrid().isSortingActive() || isRemote()) {
            return;
        }

        final var container = getContainer();
        final var itemStack = container.getItem(slotIndex);
        if (!itemStack.isEmpty()) {
            sortingStackList.add(new SortingStack(container, slotIndex, itemStack));
        }
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag compound = new CompoundTag();
        compound.putShort("Priority", (short) priority);
        return compound;
    }

    @Override
    public void deserialize(CompoundTag compound) {
        priority = compound.getShort("Priority");
    }
}
