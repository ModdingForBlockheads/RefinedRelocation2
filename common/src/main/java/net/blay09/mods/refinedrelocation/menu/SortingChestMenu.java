package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.blay09.mods.refinedrelocation.util.IMenuWithDoor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SortingChestMenu extends AbstractBaseMenu implements IMenuWithDoor {

    private final SortingChestBlockEntity sortingChest;

    public SortingChestMenu(int windowId, Inventory playerInventory, SortingChestBlockEntity sortingChest) {
        super(ModMenus.sortingChest.get(), windowId);

        this.sortingChest = sortingChest;

        SortingChestType chestType = sortingChest.getChestType();
        int rowSize = chestType.getContainerRowSize();
        int rowCount = chestType.getInventorySize() / rowSize;
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < rowSize; j++) {
                addSlot(new Slot(sortingChest.getContainer(), j + i * rowSize, 8 + j * 18, 18 + i * 18));
            }
        }

        addPlayerInventory(playerInventory, (chestType.getGuiWidth() - 162) / 2 + 1, rowCount * 18 + 32);

        sortingChest.openChest(playerInventory.player);
    }

    public SortingChestBlockEntity getTileEntity() {
        return sortingChest;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            int inventoryStartIndex = sortingChest.getChestType().getInventorySize();
            if (index < inventoryStartIndex) {
                if (!moveItemStackTo(slotStack, inventoryStartIndex, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, inventoryStartIndex, false)) {
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
    public boolean stillValid(Player player) {
        return !sortingChest.isRemoved() && player.distanceToSqr(sortingChest.getBlockPos().getX() + 0.5, sortingChest.getBlockPos().getY() + 0.5, sortingChest.getBlockPos().getZ() + 0.5) <= 64;
    }

    @Override
    public boolean matches(BlockEntity blockEntity) {
        return this.sortingChest == blockEntity;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        sortingChest.closeChest(player);
    }

}
