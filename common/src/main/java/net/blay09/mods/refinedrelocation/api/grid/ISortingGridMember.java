package net.blay09.mods.refinedrelocation.api.grid;

import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public interface ISortingGridMember {
	BlockEntity getBlockEntity();

	boolean isInvalid();

	void setSortingGrid(@Nullable ISortingGrid grid);
	@Nullable
	ISortingGrid getSortingGrid();

	/**
	 * Implementing tile entities MUST call this on their first tick
	 */
	void onFirstTick(BlockEntity blockEntity);

	/**
	 * Implementing tile entities CAN call this from update, if they are tickable. Required for sorting inventories.
	 */
	void onUpdate(BlockEntity blockEntity);

	/**
	 * Implementing tile entities MUST call this from invalidate() and onChunkUnload()
	 */
	void onInvalidate(BlockEntity blockEntity);
}
