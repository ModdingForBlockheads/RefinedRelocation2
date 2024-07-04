package net.blay09.mods.refinedrelocation.grid;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class SortingStack {

	private final Container container;
	private final int slotIndex;
	private final ItemStack itemStack;

	public SortingStack(Container container, int slotIndex, ItemStack itemStack) {
		this.container = container;
		this.slotIndex = slotIndex;
		this.itemStack = itemStack;
	}

	public Container getContainer() {
		return container;
	}

	public int getSlotIndex() {
		return slotIndex;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

}
