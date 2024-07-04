package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.refinedrelocation.block.entity.FastHopperBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FastHopperMenu extends AbstractBaseMenu {

    private final FastHopperBlockEntity fastHopper;

    public FastHopperMenu(int windowId, Inventory playerInventory, FastHopperBlockEntity fastHopper) {
        super(ModMenus.fastHopper.get(), windowId);

        this.fastHopper = fastHopper;

        for (int i = 0; i < 5; i++) {
            addSlot(new SlotItemHandlerCareless(fastHopper.getContainer(), i, 44 + i * 18, 20));
        }

        addPlayerInventory(playerInventory, 8, 51);
    }

    public FastHopperBlockEntity getBlockEntity() {
        return fastHopper;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();

            if (index < 5) {
                if (!moveItemStackTo(slotStack, 5, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(slotStack, 0, 5, false)) {
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
        return !fastHopper.isRemoved() && player.distanceToSqr(fastHopper.getBlockPos().getX() + 0.5, fastHopper.getBlockPos().getY() + 0.5, fastHopper.getBlockPos().getZ() + 0.5) <= 64;
    }

}
