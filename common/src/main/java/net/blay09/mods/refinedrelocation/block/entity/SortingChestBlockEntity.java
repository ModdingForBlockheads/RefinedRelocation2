package net.blay09.mods.refinedrelocation.block.entity;


import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.filter.RootFilterImpl;
import net.blay09.mods.refinedrelocation.grid.SortingInventory;
import net.blay09.mods.refinedrelocation.menu.SortingChestMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.util.List;


public class SortingChestBlockEntity extends BalmBlockEntity implements BalmMenuProvider<BlockPos>,
        BalmContainerProvider,
        OnLoadHandler,
        Nameable,
        LidBlockEntity,
        Clearable {


    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, state, SoundEvents.CHEST_OPEN);
        }

        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, state, SoundEvents.CHEST_CLOSE);
        }

        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int a, int b) {
            signalOpenCount(level, pos, state, a, b);
        }

        protected boolean isOwnContainer(Player player) {
            if (!(player.containerMenu instanceof SortingChestMenu sortingChestMenu)) {
                return false;
            } else {
                final var container = sortingChestMenu.getContainer();
                return container == SortingChestBlockEntity.this.getContainer();
            }
        }
    };
    private final ChestLidController chestLidController = new ChestLidController();

    private static final int EVENT_NUM_PLAYERS = 1;

    private final DefaultContainer container;

    private final ISortingInventory sortingInventory = new SortingInventory(this);
    private final RootFilter rootFilter = new RootFilterImpl();
    private final SortingChestType chestType;

    private Component customName;

    public SortingChestBlockEntity(SortingChestType chestType, BlockPos pos, BlockState state) {
        super(chestType.getBlockEntityType(), pos, state);
        this.chestType = chestType;

        container = new DefaultContainer(chestType.getInventorySize()) {
            @Override
            public void slotChanged(int slot) {
                SortingChestBlockEntity.this.setChanged();
                sortingInventory.onSlotChanged(slot);
            }
        };
    }

    public SortingChestType getChestType() {
        return chestType;
    }

    @Override
    public void onLoad() {
        sortingInventory.firstTick();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SortingChestBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        sortingInventory.update();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        sortingInventory.invalidate();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag itemHandlerCompound = tag.getCompound("ItemHandler");
        itemHandlerCompound.putInt("Size", chestType.getInventorySize());
        this.container.deserialize(itemHandlerCompound, provider);

        sortingInventory.deserialize(tag.getCompound("SortingInventory"));

        rootFilter.deserializeNBT(tag.getCompound("RootFilter"));

        customName = tag.contains("CustomName")
                ? Component.literal(tag.getString("CustomName"))
                : null;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("ItemHandler", container.serialize(provider));
        tag.put("SortingInventory", sortingInventory.serialize());
        tag.put("RootFilter", rootFilter.serializeNBT());
        if (customName != null) {
            tag.putString("CustomName", customName.getString());
        }
    }

    @Override
    public void writeUpdateTag(CompoundTag tag) {
        saveAdditional(tag, level.registryAccess());
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(ISortingGridMember.class, sortingInventory),
                new BalmProvider<>(ISortingInventory.class, sortingInventory),
                new BalmProvider<>(RootFilter.class, rootFilter),
                new BalmProvider<>(SimpleFilter.class, rootFilter)
        );
    }

    public String getUnlocalizedName() {
        return "container.refinedrelocation:" + chestType.getRegistryName();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getUnlocalizedName());
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new SortingChestMenu(i, playerInventory, this);
    }

    public void setCustomName(String customName) {
        this.customName = Component.literal(customName);
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Override
    public Component getName() {
        return customName != null ? customName : Component.translatable(getUnlocalizedName());
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void restoreItems(ListTag items, HolderLookup.Provider provider) {
        for (Tag item : items) {
            CompoundTag compound = (CompoundTag) item;
            int slot = compound.getByte("Slot");
            ItemStack itemStack = ItemStack.parseOptional(provider, compound);
            ItemStack rest = ContainerUtils.insertItem(container, slot, itemStack, false);
            if (!rest.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5, rest));
            }
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < container.getContainerSize(); i++) {
            container.setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer serverPlayer) {
        return worldPosition;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getScreenStreamCodec() {
        return BlockPos.STREAM_CODEC.cast();
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return chestLidController.getOpenness(partialTicks);
    }

    public static int getOpenCount(BlockGetter blockGetter, BlockPos pos) {
        final var state = blockGetter.getBlockState(pos);
        if (state.hasBlockEntity()) {
            final var blockEntity = blockGetter.getBlockEntity(pos);
            if (blockEntity instanceof SortingChestBlockEntity sortingChest) {
                return sortingChest.openersCounter.getOpenerCount();
            }
        }

        return 0;
    }

    public void recheckOpen() {
        if (!remove) {
            openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
        }
    }

    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int wat, int wot) {
        final var block = state.getBlock();
        level.blockEvent(pos, block, 1, wot);
    }


    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, SortingChestBlockEntity blockEntity) {
        blockEntity.chestLidController.tickLid();
    }

    static void playSound(Level level, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        level.playSound(null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                soundEvent,
                SoundSource.BLOCKS,
                0.5f,
                level.random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public boolean triggerEvent(int event, int data) {
        if (event == EVENT_NUM_PLAYERS) {
            chestLidController.shouldBeOpen(data > 0);
            return true;
        } else {
            return super.triggerEvent(event, data);
        }
    }

    public void startOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openersCounter.incrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }

    }

    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator()) {
            openersCounter.decrementOpeners(player, getLevel(), getBlockPos(), getBlockState());
        }

    }
}
