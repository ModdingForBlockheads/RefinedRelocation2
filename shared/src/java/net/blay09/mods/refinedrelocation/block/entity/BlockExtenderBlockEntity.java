package net.blay09.mods.refinedrelocation.block.entity;

import net.blay09.mods.balm.api.block.entity.BalmBlockEntity;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.item.ModItems;
import net.blay09.mods.refinedrelocation.api.Capabilities;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.menu.BlockExtenderMenu;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.blay09.mods.refinedrelocation.util.RelativeSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

public class BlockExtenderBlockEntity extends BalmBlockEntity implements IDroppableItemHandler, IMultiRootFilter {

    private class ItemHandlerWrapper implements IItemHandler {
        private final BlockEntity blockEntity;
        private final Direction facing;
        private IItemHandler baseHandler;

        public ItemHandlerWrapper(BlockEntity blockEntity, @Nullable Direction facing, IItemHandler baseHandler) {
            this.blockEntity = blockEntity;
            this.facing = facing;
            this.baseHandler = baseHandler;
        }

        public boolean revalidate() {
            LazyOptional<IItemHandler> itemHandlerCap = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
            itemHandlerCap.ifPresent(itemHandler -> baseHandler = itemHandler);
            return itemHandlerCap.isPresent();
        }

        @Override
        public int getSlots() {
            return baseHandler.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return baseHandler.getStackInSlot(slot);
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack itemStack) {
            if (hasSlotLock) {
                if (itemStack.isEmpty() || getStackInSlot(slot).isEmpty()) {
                    return false;
                }
            }

            if (hasInputFilter) {
                //noinspection RedundantIfStatement
                if (itemStack.isEmpty() || !inputFilter.passes(blockEntity, itemStack, itemStack)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) {
                return stack;
            }

            if (hasStackLimiter) {
                int space = stackLimiterLimit - getStackInSlot(slot).getCount();
                if (space <= 0) {
                    return stack;
                }
                int amount = Math.min(stack.getCount(), space);
                if (amount < stack.getCount()) {
                    ItemStack insertStack = ItemHandlerHelper.copyStackWithSize(stack, amount);
                    ItemStack restStack = baseHandler.insertItem(slot, insertStack, simulate);
                    int initialRest = stack.getCount() - amount;
                    if (initialRest > 0) {
                        ItemStack otherRestStack = ItemHandlerHelper.copyStackWithSize(stack, initialRest);
                        if (restStack.isEmpty()) {
                            return otherRestStack;
                        }
                        if (ItemHandlerHelper.canItemStacksStack(stack, restStack)) {
                            restStack.grow(initialRest);
                        } else if (!level.isClientSide) {
                            // If the remainder item is different than the input item upon failed insertion that's most likely a bug or bad game mechanic, so drop the other rest item in the world rather than having it disappear.
                            level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, otherRestStack));
                        }
                    }
                    return restStack;
                }
            }

            return baseHandler.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (hasOutputFilter) {
                final ItemStack itemStack = getStackInSlot(slot);
                if (!outputFilter.passes(blockEntity, itemStack, itemStack)) {
                    return ItemStack.EMPTY;
                }
            }

            return baseHandler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return baseHandler.getSlots();
        }
    }

    private final ItemStackHandler itemHandlerUpgrades = new ItemStackHandler(3) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!isUpgradeItem(stack)) {
                return false;
            }

            for (int i = 0; i < getSlots(); i++) {
                if (getStackInSlot(i).getItem() == stack.getItem()) {
                    return false;
                }
            }

            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            updateUpgrades();
            setChanged();
        }
    };

    private boolean isUpgradeItem(ItemStack stack) {
        return stack.getItem() == ModItems.stackLimiter || stack.getItem() == ModItems.outputFilter || stack.getItem() == ModItems.inputFilter || stack.getItem() == ModItems.slotLock;
    }

    private final Direction[] sideMappings = new Direction[5];
    private final IRootFilter inputFilter = new RootFilter();
    private final IRootFilter outputFilter = new RootFilter();
    private int stackLimiterLimit = 64;

    private boolean hasStackLimiter;
    private boolean hasSlotLock;
    private boolean hasInputFilter;
    private boolean hasOutputFilter;
    private BlockEntity cachedConnectedBlockEntity;
    private final ItemHandlerWrapper[] cachedItemHandlers = new ItemHandlerWrapper[6];
    private final Direction[] cachedFacingToFacingMappings = new Direction[6];

    public BlockExtenderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.blockExtender.get(), pos, state);
    }

    @Override
    public IRootFilter getRootFilter(int index) {
        return index == 0 ? inputFilter : outputFilter;
    }

    @Nullable
    public Direction getSideMapping(RelativeSide side) {
        return sideMappings[side.ordinal()];
    }

    public void setSideMapping(RelativeSide side, @Nullable Direction facing) {
        sideMappings[side.ordinal()] = facing;
        cachedFacingToFacingMappings[side.toFacing(getFacing()).ordinal()] = facing;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockExtenderBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (cachedConnectedBlockEntity == null) {
            cachedConnectedBlockEntity = level.getBlockEntity(worldPosition.relative(getFacing()));
        } else if (cachedConnectedBlockEntity.isRemoved()) {
            cachedConnectedBlockEntity = null;
        }
    }

    @Override
    public void balmOnLoad() {
        cachedConnectedBlockEntity = level.getBlockEntity(worldPosition.relative(getFacing()));
        if (cachedConnectedBlockEntity instanceof BlockExtenderBlockEntity) {
            cachedConnectedBlockEntity = null; // Disallow connecting block extenders to each other
        }
        for (int i = 0; i < sideMappings.length; i++) {
            cachedFacingToFacingMappings[RelativeSide.fromIndex(i).toFacing(getFacing()).ordinal()] = sideMappings[i];
        }
        updateUpgrades();
    }

    @Nullable
    public Direction getSideMapping(@Nullable Direction facing) {
        if (facing == null) {
            return getFacing().getOpposite();
        }
        return cachedFacingToFacingMappings[facing.ordinal()];
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.FACING);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        byte[] mappings = new byte[5];
        for (int i = 0; i < sideMappings.length; i++) {
            mappings[i] = sideMappings[i] == null ? -1 : (byte) sideMappings[i].get3DDataValue();
        }

        compound.putByteArray("SideMappings", mappings);
        compound.put("Upgrades", itemHandlerUpgrades.serializeNBT());
        compound.putByte("StackLimiter", (byte) stackLimiterLimit);
        compound.put("InputFilter", inputFilter.serializeNBT());
        compound.put("OutputFilter", outputFilter.serializeNBT());
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        byte[] mappings = compound.getByteArray("SideMappings");
        if (mappings.length == 5) {
            for (int i = 0; i < mappings.length; i++) {
                if (mappings[i] != -1) {
                    sideMappings[i] = Direction.from3DDataValue(mappings[i]);
                } else {
                    sideMappings[i] = null;
                }
            }
        }

        itemHandlerUpgrades.deserializeNBT(compound.getCompound("Upgrades"));
        stackLimiterLimit = compound.getByte("StackLimiter");
        inputFilter.deserializeNBT(compound.getCompound("InputFilter"));
        outputFilter.deserializeNBT(compound.getCompound("OutputFilter"));
        updateUpgrades();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.MULTI_ROOT_FILTER) {
            return LazyOptional.of(() -> this).cast();
        }

        if (cachedConnectedBlockEntity != null) {
            Direction ioSide = getSideMapping(side);
            if (ioSide != null) {
                if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && requiresItemHandlerWrapping()) {
                    int cacheIdx = ioSide.get3DDataValue();
                    if (cachedItemHandlers[cacheIdx] != null) {
                        if (!cachedItemHandlers[cacheIdx].revalidate()) {
                            cachedItemHandlers[cacheIdx] = null;
                            return LazyOptional.empty();
                        }
                    } else {
                        LazyOptional<IItemHandler> itemHandlerCap = cachedConnectedBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, ioSide);
                        itemHandlerCap.ifPresent(itemHandler -> cachedItemHandlers[cacheIdx] = new ItemHandlerWrapper(cachedConnectedBlockEntity, ioSide, itemHandler));
                    }
                    return LazyOptional.of(() -> cachedItemHandlers[cacheIdx]).cast();
                }

                return cachedConnectedBlockEntity.getCapability(cap, ioSide);
            }
        }

        return super.getCapability(cap, side);
    }

    private boolean requiresItemHandlerWrapping() {
        return hasStackLimiter || hasSlotLock || hasInputFilter || hasOutputFilter;
    }

    public String getUnlocalizedName() {
        return "container.refinedrelocation:block_extender";
    }

    public ItemStackHandler getItemHandlerUpgrades() {
        return itemHandlerUpgrades;
    }

    private void updateUpgrades() {
        hasStackLimiter = false;
        hasSlotLock = false;
        hasInputFilter = false;
        hasOutputFilter = false;
        for (int i = 0; i < itemHandlerUpgrades.getSlots(); i++) {
            ItemStack itemStack = itemHandlerUpgrades.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() == ModItems.stackLimiter) {
                    hasStackLimiter = true;
                } else if (itemStack.getItem() == ModItems.slotLock) {
                    hasSlotLock = true;
                } else if (itemStack.getItem() == ModItems.inputFilter) {
                    hasInputFilter = true;
                } else if (itemStack.getItem() == ModItems.outputFilter) {
                    hasOutputFilter = true;
                }
            }
        }
    }

    @Override
    public Collection<IItemHandler> getDroppedItemHandlers() {
        return Collections.singletonList(itemHandlerUpgrades);
    }

    public int getStackLimiterLimit() {
        return stackLimiterLimit;
    }

    public void setStackLimiterLimit(int stackLimiterLimit) {
        this.stackLimiterLimit = stackLimiterLimit;
    }

    public LazyOptional<IRootFilter> getInputFilter() {
        return LazyOptional.of(() -> inputFilter);
    }

    public LazyOptional<IRootFilter> getOutputFilter() {
        return LazyOptional.of(() -> outputFilter);
    }

    public boolean hasInputFilter() {
        return hasInputFilter;
    }

    public boolean hasOutputFilter() {
        return hasOutputFilter;
    }

    public BalmMenuProvider withClickedFace(Direction direction) {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent(getUnlocalizedName());
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new BlockExtenderMenu(i, playerInventory, BlockExtenderBlockEntity.this);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(getBlockPos());
                buf.writeByte(direction.get3DDataValue());
            }
        };
    }

}