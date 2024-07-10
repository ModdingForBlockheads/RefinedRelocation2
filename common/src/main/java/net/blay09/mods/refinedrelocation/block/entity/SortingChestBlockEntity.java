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
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.Nullable;
import java.util.List;


public class SortingChestBlockEntity extends BalmBlockEntity implements BalmMenuProvider<BlockPos>,
        BalmContainerProvider,
        OnLoadHandler,
        Nameable,
        LidBlockEntity,
        Clearable {

    private static final int EVENT_NUM_PLAYERS = 1;

    private final DefaultContainer container;

    private final ISortingInventory sortingInventory = new SortingInventory(this);
    private final IRootFilter rootFilter = new RootFilter();
    private final SortingChestType chestType;

    private float lidAngle;
    private float prevLidAngle;
    private int numPlayersUsing;
    private int ticksSinceSync;

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

        ticksSinceSync++;
        if (numPlayersUsing != 0 && (ticksSinceSync + worldPosition.getX() + worldPosition.getY() + worldPosition.getZ()) % 200 == 0) {
            numPlayersUsing = calculatePlayersUsing();
        }

        prevLidAngle = lidAngle;
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();
        if (numPlayersUsing > 0 && lidAngle == 0f) {
            level.playSound(null,
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    SoundEvents.CHEST_OPEN,
                    SoundSource.BLOCKS,
                    0.5f,
                    level.random.nextFloat() * 0.1f + 0.9f);
        }

        if (numPlayersUsing == 0 && lidAngle > 0f || numPlayersUsing > 0 && lidAngle < 1f) {
            float oldLidAngle = lidAngle;
            if (numPlayersUsing > 0) {
                lidAngle += 0.1f;
            } else {
                lidAngle -= 0.1f;
            }

            if (lidAngle > 1f) {
                lidAngle = 1f;
            }

            if (lidAngle < 0.5f && oldLidAngle >= 0.5f) {
                level.playSound(null,
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        SoundEvents.CHEST_CLOSE,
                        SoundSource.BLOCKS,
                        0.5f,
                        level.random.nextFloat() * 0.1f + 0.9f);
            }

            if (lidAngle < 0f) {
                lidAngle = 0f;
            }
        }
    }

    private int calculatePlayersUsing() {
        int result = 0;
        float distance = 5f;

        for (Player player : level.getEntitiesOfClass(Player.class,
                new AABB((float) worldPosition.getX() - distance,
                        (float) worldPosition.getY() - distance,
                        (float) worldPosition.getZ() - distance,
                        (float) (worldPosition.getX() + 1) + distance,
                        (float) (worldPosition.getY() + 1) + distance,
                        (float) (worldPosition.getZ() + 1) + distance))) {
            if (player.containerMenu instanceof SortingChestMenu menu) {
                if (menu.getTileEntity() == this) {
                    ++result;
                }
            }
        }

        return result;
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
                new BalmProvider<>(IRootFilter.class, rootFilter),
                new BalmProvider<>(ISimpleFilter.class, rootFilter)
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

    @Override
    public boolean triggerEvent(int id, int value) {
        if (id == EVENT_NUM_PLAYERS) {
            numPlayersUsing = value;
            return true;
        }

        return super.triggerEvent(id, value);
    }

    public void openChest(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing = Math.max(0, numPlayersUsing + 1);
            level.blockEvent(worldPosition, Blocks.ENDER_CHEST, EVENT_NUM_PLAYERS, numPlayersUsing);
        }
    }

    public void closeChest(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing = Math.max(0, numPlayersUsing - 1);
            level.blockEvent(worldPosition, Blocks.ENDER_CHEST, EVENT_NUM_PLAYERS, numPlayersUsing);
        }
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
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
}
