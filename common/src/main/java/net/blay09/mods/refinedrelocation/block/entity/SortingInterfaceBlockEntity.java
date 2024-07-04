package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.config.RefinedRelocationConfig;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.blay09.mods.refinedrelocation.grid.SortingInventory;
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

public class SortingInterfaceBlockEntity extends BalmBlockEntity implements IDroppableItemHandler, OnLoadHandler {

    private final ISortingInventory sortingInventory = new SortingInventory();
    private final IRootFilter rootFilter = new RootFilter();

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
        } else if (cachedConnectedTile.isRemoved()) {
            cachedConnectedTile = null;
            lastInventory = null;
        }

        sortingInventory.onUpdate(this);

        Container itemHandler = sortingInventory.getContainer();
        int inventorySize = itemHandler.getContainerSize();

        // Create a copy of the target inventory so we can compare and detect changes
        if (lastInventory == null || inventorySize != lastInventory.length) {
            lastInventory = new ItemStack[itemHandler.getContainerSize()];
            for (int i = 0; i < inventorySize; i++) {
                ItemStack currentStack = itemHandler.getItem(i);
                lastInventory[i] = currentStack.isEmpty() ? ItemStack.EMPTY : currentStack.copy();
            }
            currentDetectionSlot = 0;
        }

        // Detect changes in the target inventory, nine slots at a time
        for (int j = 0; j < Math.min(RefinedRelocationConfig.getActive().sortingInterfaceSlotsPerTick, inventorySize); j++) {
            int i = currentDetectionSlot;
            ItemStack prevStack = lastInventory[i];
            ItemStack currentStack = itemHandler.getItem(i);
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

    @Override
    public void setRemoved() {
        super.setRemoved();
        sortingInventory.onInvalidate(this);
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

        sortingInventory.onFirstTick(this);
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(IRootFilter.class, rootFilter),
                new BalmProvider<>(ISimpleFilter.class, rootFilter),
                new BalmProvider<>(ISortingInventory.class, sortingInventory),
                new BalmProvider<>(ISortingGridMember.class, sortingInventory)
        );
    }

//    @Nonnull // TODO dynamic providers
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        LazyOptional<T> result = super.getCapability(cap, side);
//        if (!result.isPresent() && cachedConnectedTile != null && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//            return cachedConnectedTile.getCapability(cap, getFacing().getOpposite()).cast();
//        }
//
//        return result;
//    }

    public String getUnlocalizedName() {
        return "container.refinedrelocation:sorting_interface";
    }

    @Override
    public Collection<Container> getDroppedItemHandlers() {
        // Do not drop the connected inventory's items.
        return Collections.emptyList();
    }

}
