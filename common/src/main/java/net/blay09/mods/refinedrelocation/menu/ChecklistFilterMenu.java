package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.container.IHasReturnCallback;
import net.blay09.mods.refinedrelocation.api.container.ReturnCallback;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public class ChecklistFilterMenu extends AbstractFilterMenu implements IHasReturnCallback {

	public static final String KEY_CHECK = "Check";
	public static final String KEY_UNCHECK = "Uncheck";
	public static final String KEY_STATES = "States";

	private static final int UPDATE_INTERVAL = 20;

	private final Player player;
	private final BlockEntity blockEntity;
	private final int rootFilterIndex;
	private final IChecklistFilter filter;

	private final byte[] lastStates;
	private int ticksSinceUpdate = UPDATE_INTERVAL;
	private ReturnCallback returnCallback;

	public ChecklistFilterMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity, int rootFilterIndex, IChecklistFilter filter) {
		super(ModMenus.checklistFilter.get(), windowId);

		this.player = playerInventory.player;
		this.blockEntity = blockEntity;
		this.rootFilterIndex = rootFilterIndex;
		this.filter = filter;
		lastStates = new byte[filter.getOptionCount()];

		addPlayerInventory(playerInventory, 8, 128);
	}

	public IChecklistFilter getFilter() {
		return filter;
	}

	@Override
	public void broadcastChanges() {
		super.broadcastChanges();

		ticksSinceUpdate++;
		if(ticksSinceUpdate >= UPDATE_INTERVAL) {
			boolean anyChanges = false;
			for (int i = 0; i < lastStates.length; i++) {
				byte isChecked = (byte) (filter.isOptionChecked(i) ? 1 : 0);
				if (lastStates[i] != isChecked) {
					anyChanges = true;
					lastStates[i] = isChecked;
				}
			}
			if (anyChanges) {
				RefinedRelocationAPI.syncContainerValue(KEY_STATES, lastStates, containerListeners());
				RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
			}
			ticksSinceUpdate = 0;
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

	@Override
	public BlockEntity getBlockEntity() {
		return blockEntity;
	}

	@Override
	public void receivedMessageServer(IMenuMessage message) {
		if(message.getKey().equals(KEY_CHECK)) {
			int index = message.getIntValue();
			if(index < 0 || index >= lastStates.length) {
				// Client tried to check an option that doesn't exist. Bad client!
				return;
			}
			filter.setOptionChecked(index, true);
			blockEntity.setChanged();
			lastStates[index] = 1;
			RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
		} else if(message.getKey().equals(KEY_UNCHECK)) {
			int index = message.getIntValue();
			if(index < 0 || index >= lastStates.length) {
				// Client tried to check an option that doesn't exist. Bad client!
				return;
			}
			filter.setOptionChecked(index, false);
			blockEntity.setChanged();
			lastStates[index] = 0;
			RefinedRelocationAPI.updateFilterPreview(player, blockEntity, filter);
		}
	}

	@Override
	public void receivedMessageClient(IMenuMessage message) {
		if(message.getKey().equals(KEY_STATES)) {
			byte[] states = message.getByteArrayValue();
			for(int i = 0; i < states.length; i++) {
				filter.setOptionChecked(i, states[i] == 1);
			}
		}
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
