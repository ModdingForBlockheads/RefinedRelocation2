package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FilteredHopperBlockEntity extends FastHopperBlockEntity {
    private final IRootFilter rootFilter = new RootFilter();

    public FilteredHopperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.filteredHopper.get(), pos, state);
    }

    @Override
    protected DefaultContainer createContainer() {
        return new DefaultContainer(5) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack itemStack) {
                return !itemStack.isEmpty() && rootFilter.passes(FilteredHopperBlockEntity.this, itemStack, itemStack);
            }
        };
    }

    @Override
    public String getUnlocalizedName() {
        return "container.refinedrelocation:filtered_hopper";
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(IRootFilter.class, rootFilter),
                new BalmProvider<>(ISimpleFilter.class, rootFilter)
        );
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("RootFilter", rootFilter.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        rootFilter.deserializeNBT(tag.getCompound("RootFilter"));
    }

}
