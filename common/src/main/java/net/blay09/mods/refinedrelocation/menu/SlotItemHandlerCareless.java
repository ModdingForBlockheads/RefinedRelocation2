package net.blay09.mods.refinedrelocation.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandlerCareless extends Slot {
	public SlotItemHandlerCareless(Container container, int index, int xPosition, int yPosition) {
		super(container, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return true;
	}
}
