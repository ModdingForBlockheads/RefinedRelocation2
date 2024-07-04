package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.container.IHasReturnCallback;
import net.blay09.mods.refinedrelocation.api.container.ReturnCallback;
import net.blay09.mods.refinedrelocation.filter.NameFilter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public class NameFilterMenu extends AbstractFilterMenu implements IHasReturnCallback {

    public static final String KEY_VALUE = "Value";

    private final Player player;
    private final BlockEntity blockEntity;
    private final NameFilter filter;
    private final int rootFilterIndex;

    private String lastValue = "";

    private boolean guiNeedsUpdate;
    private ReturnCallback returnCallback;

    public NameFilterMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity, int rootFilterIndex, NameFilter filter) {
        super(ModMenus.nameFilter.get(), windowId);
        this.player = playerInventory.player;
        this.blockEntity = blockEntity;
        this.rootFilterIndex = rootFilterIndex;
        this.filter = filter;

        addPlayerInventory(playerInventory, 8, 128);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (!lastValue.equals(filter.getValue())) {
            RefinedRelocationAPI.syncContainerValue(KEY_VALUE, filter.getValue(), containerListeners());
            RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
            lastValue = filter.getValue();
        }
    }

    @Override
    public void receivedMessageClient(IMenuMessage message) {
        if (message.getKey().equals(KEY_VALUE)) {
            filter.setValue(message.getStringValue());
            markGuiNeedsUpdate(true);
        }
    }

    @Override
    public void receivedMessageServer(IMenuMessage message) {
        if (message.getKey().equals(KEY_VALUE)) {
            filter.setValue(message.getStringValue());
            blockEntity.setChanged();
            lastValue = filter.getValue();
            RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        super.clicked(slotId, dragType, clickType, player);
        RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
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

    public void sendValueToServer(String value) {
        RefinedRelocationAPI.sendContainerMessageToServer(KEY_VALUE, value);
    }

    public String getValue() {
        return filter.getValue();
    }

    public void markGuiNeedsUpdate(boolean dirty) {
        this.guiNeedsUpdate = dirty;
    }

    public boolean doesGuiNeedUpdate() {
        return guiNeedsUpdate;
    }

    @Override
    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public void setReturnCallback(@Nullable ReturnCallback callback) {
        this.returnCallback = callback;
    }

    @Nullable
    @Override
    public ReturnCallback getReturnCallback() {
        return returnCallback;
    }

    @Override
    public int getRootFilterIndex() {
        return rootFilterIndex;
    }
}
