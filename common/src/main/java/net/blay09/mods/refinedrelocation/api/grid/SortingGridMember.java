package net.blay09.mods.refinedrelocation.api.grid;

import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public interface SortingGridMember {
	BlockEntity getBlockEntity();

	boolean isInvalid();

	void setSortingGrid(@Nullable ISortingGrid grid);
	@Nullable
	ISortingGrid getSortingGrid();

	/**
	 * Implementing tile entities MUST call this on their first tick
	 */
	void firstTick();

	/**
	 * Implementing tile entities CAN call this from update, if they are tickable. Required for sorting inventories.
	 */
	void update();

	/**
	 * Implementing tile entities MUST call this from invalidate() and onChunkUnload()
	 */
	void invalidate();
}
