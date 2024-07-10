package net.blay09.mods.refinedrelocation.grid;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

public class SortingInventory extends SortingGridMember implements ISortingInventory {

    private final LinkedList<SortingStack> sortingStackList = Lists.newLinkedList();
    private ISimpleFilter filter;
    private int priority;

    public SortingInventory(BlockEntity blockEntity) {
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
    public ISimpleFilter getFilter() {
        return filter;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        filter = Balm.getProviders().getProvider(getBlockEntity(), IRootFilter.class);
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
