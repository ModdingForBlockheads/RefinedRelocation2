package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.container.INetworkedMenu;
import net.blay09.mods.refinedrelocation.mixin.AbstractContainerMenuAccessor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class AbstractBaseMenu extends AbstractContainerMenu implements INetworkedMenu {

    protected AbstractBaseMenu(@Nullable MenuType<?> type, int windowId) {
        super(type, windowId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void receivedMessageClient(IMenuMessage message) {

    }

    @Override
    public void receivedMessageServer(IMenuMessage message) {

    }

    protected void addPlayerInventory(Inventory playerInventory, int offsetX, int offsetY) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, offsetX + j * 18, offsetY + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, offsetX + i * 18, offsetY + 58));
        }
    }

    protected List<ContainerListener> containerListeners() {
        return ((AbstractContainerMenuAccessor) this).getContainerListeners();
    }

}
