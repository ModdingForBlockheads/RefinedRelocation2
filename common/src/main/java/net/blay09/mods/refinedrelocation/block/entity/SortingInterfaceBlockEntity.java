package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.config.RefinedRelocationConfig;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.filter.RootFilterImpl;
import net.blay09.mods.refinedrelocation.grid.SortingInventoryDelegate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SortingInterfaceBlockEntity extends BalmBlockEntity implements IDroppableContainer, OnLoadHandler {

    private final SortingInventoryDelegate sortingInventory = new SortingInventoryDelegate(this);
    private final RootFilter rootFilter = new RootFilterImpl();

    private BlockEntity cachedConnectedTile;
    private ItemStack[] lastInventory;
    private int currentDetectionSlot;

    public SortingInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.sortingInterface.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SortingInterfaceBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (cachedConnectedTile == null) {
            cachedConnectedTile = level.getBlockEntity(worldPosition.relative(getFacing()));
            if (cachedConnectedTile != null) {
                final var container = Balm.getProviders().getProvider(cachedConnectedTile, Container.class);
                sortingInventory.setContainer(container);
            }
        } else if (cachedConnectedTile.isRemoved()) {
            cachedConnectedTile = null;
            sortingInventory.setContainer(null);
            lastInventory = null;
        }

        sortingInventory.update();

        final var container = sortingInventory.getContainer();
        if (container != null) {
            int inventorySize = container.getContainerSize();

            // Create a copy of the target inventory so that we can compare and detect changes
            if (lastInventory == null || inventorySize != lastInventory.length) {
                lastInventory = new ItemStack[container.getContainerSize()];
                for (int i = 0; i < inventorySize; i++) {
                    ItemStack currentStack = container.getItem(i);
                    lastInventory[i] = currentStack.isEmpty() ? ItemStack.EMPTY : currentStack.copy();
                }
                currentDetectionSlot = 0;
            }

            // Detect changes in the target inventory, nine slots at a time
            for (int j = 0; j < Math.min(RefinedRelocationConfig.getActive().sortingInterfaceSlotsPerTick, inventorySize); j++) {
                int i = currentDetectionSlot;
                ItemStack prevStack = lastInventory[i];
                ItemStack currentStack = container.getItem(i);
                if (!ItemStack.isSameItemSameComponents(prevStack, currentStack)) {
                    sortingInventory.onSlotChanged(i);
                    lastInventory[i] = currentStack.isEmpty() ? ItemStack.EMPTY : currentStack.copy();
                }

                currentDetectionSlot++;
                if (currentDetectionSlot >= inventorySize) {
                    currentDetectionSlot = 0;
                }
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        sortingInventory.invalidate();
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        sortingInventory.deserialize(compound.getCompound("SortingInventory"));
        rootFilter.deserializeNBT(compound.getCompound("RootFilter"));
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        compound.put("SortingInventory", sortingInventory.serialize());
        compound.put("RootFilter", rootFilter.serializeNBT());
    }

    @Override
    public void onLoad() {
        cachedConnectedTile = level.getBlockEntity(worldPosition.relative(getFacing()));

        sortingInventory.firstTick();
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(RootFilter.class, rootFilter),
                new BalmProvider<>(SimpleFilter.class, rootFilter),
                new BalmProvider<>(ISortingInventory.class, sortingInventory),
                new BalmProvider<>(ISortingGridMember.class, sortingInventory)
        );
    }

    @Override
    public Collection<Container> getDroppedContainers() {
        // Do not drop the connected inventory's items.
        return Collections.emptyList();
    }

}
