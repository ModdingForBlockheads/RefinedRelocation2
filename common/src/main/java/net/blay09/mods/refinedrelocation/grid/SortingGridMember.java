package net.blay09.mods.refinedrelocation.grid;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public class SortingGridMember implements ISortingGridMember {

	private final BlockEntity blockEntity;
	private boolean isInvalid;
	private ISortingGrid sortingGrid;

    public SortingGridMember(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }


    @Override
	public BlockEntity getBlockEntity() {
		return blockEntity;
	}

	@Override
	public boolean isInvalid() {
		return isInvalid;
	}

	@Override
	public void setSortingGrid(@Nullable ISortingGrid grid) {
		this.sortingGrid = grid;
	}

	@Override
	public ISortingGrid getSortingGrid() {
		return sortingGrid;
	}

	@Override
	public final void invalidate() {
		onInvalidate();
		isInvalid = true;
		RefinedRelocationAPI.removeFromSortingGrid(this);
	}

	@Override
	public final void update() {
		onUpdate();
	}

	@Override
	public final void firstTick() {
		RefinedRelocationAPI.addToSortingGrid(this);
		onLoad();
	}

	protected void onLoad() {}
	protected void onInvalidate() {}
	protected void onUpdate() {}

	public final boolean isRemote() {
		Level level = blockEntity.getLevel();
		return level != null && level.isClientSide;
	}
}
