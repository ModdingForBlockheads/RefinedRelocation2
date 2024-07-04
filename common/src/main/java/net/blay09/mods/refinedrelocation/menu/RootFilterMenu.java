package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.RefinedRelocationUtils;
import net.blay09.mods.refinedrelocation.api.Priority;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.container.ReturnCallback;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.blay09.mods.refinedrelocation.grid.SortingInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public class RootFilterMenu extends AbstractFilterMenu implements IRootFilterMenu {

    public static final String KEY_ROOT_FILTER = "RootFilter";
    public static final String KEY_OPEN_ADD_FILTER = "OpenAddFilter";
    public static final String KEY_EDIT_FILTER = "EditFilter";
    public static final String KEY_DELETE_FILTER = "DeleteFilter";
    public static final String KEY_PRIORITY = "Priority";
    public static final String KEY_BLACKLIST = "Blacklist";
    public static final String KEY_BLACKLIST_INDEX = "FilterIndex";

    private final Player player;
    private final BlockEntity blockEntity;
    private final IRootFilter rootFilter;
    private final int rootFilterIndex;

    private ReturnCallback returnCallback;
    private ISortingInventory sortingInventory;

    private int lastFilterCount = -1;
    private int lastPriority;
    private final boolean[] lastBlacklist = new boolean[3];

    public RootFilterMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity, int rootFilterIndex) {
        super(ModMenus.rootFilter.get(), windowId);

        this.player = playerInventory.player;
        this.blockEntity = blockEntity;
        this.rootFilter = RefinedRelocationUtils.getRootFilter(blockEntity, rootFilterIndex).orElseGet(RootFilter::new);
        this.rootFilterIndex = rootFilterIndex;
        sortingInventory = Balm.getProviders().getProvider(blockEntity, ISortingInventory.class);
        if(sortingInventory == null) {
            sortingInventory = new SortingInventory();
        }

        addPlayerInventory(playerInventory, 8, hasSortingInventory() ? 128 : 84);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (rootFilter.getFilterCount() != lastFilterCount) {
            syncFilterList();
            RefinedRelocationAPI.updateFilterPreview(player, blockEntity, rootFilter);
        }

        for (int i = 0; i < lastBlacklist.length; i++) {
            boolean nowBlacklist = rootFilter.isBlacklist(i);
            if (lastBlacklist[i] != nowBlacklist) {
                CompoundTag compound = new CompoundTag();
                compound.putInt(KEY_BLACKLIST_INDEX, i);
                compound.putBoolean(KEY_BLACKLIST, nowBlacklist);
                RefinedRelocationAPI.syncContainerValue(KEY_BLACKLIST, compound, containerListeners());
                lastBlacklist[i] = nowBlacklist;
            }
        }

        if (sortingInventory.getPriority() != lastPriority) {
            RefinedRelocationAPI.syncContainerValue(KEY_PRIORITY, sortingInventory.getPriority(), containerListeners());
            lastPriority = sortingInventory.getPriority();
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        super.clicked(slotId, dragType, clickTypeIn, player);
        RefinedRelocationAPI.updateFilterPreview(player, blockEntity, rootFilter);
    }

    private void syncFilterList() {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.put(KEY_ROOT_FILTER, rootFilter.serializeNBT());
        RefinedRelocationAPI.syncContainerValue(KEY_ROOT_FILTER, tagCompound, containerListeners());
        lastFilterCount = rootFilter.getFilterCount();
        for (int i = 0; i < lastBlacklist.length; i++) {
            lastBlacklist[i] = rootFilter.isBlacklist(i);
        }
    }

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index < 27) {
                if (!moveItemStackTo(slotStack, 27, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public void receivedMessageServer(IMenuMessage message) {
        switch (message.getKey()) {
            case KEY_OPEN_ADD_FILTER:
                MenuProvider menuProvider = new BalmMenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.refinedrelocation:add_filter");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new AddFilterMenu(windowId, playerInventory, blockEntity, rootFilterIndex);
                    }

                    @Override
                    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                        buf.writeBlockPos(blockEntity.getBlockPos());
                        buf.writeByte(rootFilterIndex);
                    }
                };
                Balm.getNetworking().openGui(player, menuProvider);
                break;
            case KEY_EDIT_FILTER: {
                int index = message.getIntValue();
                if (index < 0 || index >= rootFilter.getFilterCount()) {
                    // Client tried to edit a filter that doesn't exist. Bad client!
                    return;
                }
                IFilter filter = rootFilter.getFilter(index);
                if (filter != null) {
                    MenuProvider filterConfig = filter.getConfiguration(player, blockEntity, rootFilterIndex, index);
                    if (filterConfig != null) {
                        Balm.getNetworking().openGui(player, filterConfig);
                    }
                }
                break;
            }
            case KEY_DELETE_FILTER: {
                int index = message.getIntValue();
                if (index < 0 || index >= rootFilter.getFilterCount()) {
                    // Client tried to delete a filter that doesn't exist. Bad client!
                    return;
                }
                rootFilter.removeFilter(index);
                blockEntity.setChanged();
                break;
            }
            case KEY_PRIORITY:
                int value = message.getIntValue();
                if (value < Priority.LOWEST || value > Priority.HIGHEST) {
                    // Client tried to set an invalid priority. Bad client!
                    return;
                }

                sortingInventory.setPriority(value);
                blockEntity.setChanged();
                break;
            case KEY_BLACKLIST: {
                CompoundTag tagCompound = message.getNBTValue();
                int index = tagCompound.getInt(KEY_BLACKLIST_INDEX);
                if (index < 0 || index >= rootFilter.getFilterCount()) {
                    // Client tried to delete a filter that doesn't exist. Bad client!
                    return;
                }
                rootFilter.setIsBlacklist(index, tagCompound.getBoolean(KEY_BLACKLIST));
                blockEntity.setChanged();
                RefinedRelocationAPI.updateFilterPreview(player, blockEntity, rootFilter);
                break;
            }
        }
    }

    @Override
    public void receivedMessageClient(IMenuMessage message) {
        switch (message.getKey()) {
            case KEY_ROOT_FILTER:
                rootFilter.deserializeNBT(message.getNBTValue().getCompound(KEY_ROOT_FILTER));
                break;
            case KEY_PRIORITY:
                sortingInventory.setPriority(message.getIntValue());
                break;
            case KEY_BLACKLIST:
                CompoundTag compound = message.getNBTValue();
                rootFilter.setIsBlacklist(compound.getInt(KEY_BLACKLIST_INDEX), compound.getBoolean(KEY_BLACKLIST));
                break;
        }
    }

    @Override
    public IRootFilter getRootFilter() {
        return rootFilter;
    }

    public boolean hasSortingInventory() {
        return Balm.getProviders().getProvider(blockEntity, ISortingInventory.class) != null;
    }

    public ISortingInventory getSortingInventory() {
        return sortingInventory;
    }

    @Nullable
    public ReturnCallback getReturnCallback() {
        return returnCallback;
    }

    public RootFilterMenu setReturnCallback(@Nullable ReturnCallback returnCallback) {
        this.returnCallback = returnCallback;
        return this;
    }

    public boolean canReturnFromFilter() {
        return blockEntity instanceof MenuProvider;
    }

    @Override
    public int getRootFilterIndex() {
        return rootFilterIndex;
    }
}
