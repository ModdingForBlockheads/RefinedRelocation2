package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.filter.RootFilterImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FilteredHopperBlockEntity extends FastHopperBlockEntity {
    private final RootFilter rootFilter = new RootFilterImpl();

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
                new BalmProvider<>(RootFilter.class, rootFilter),
                new BalmProvider<>(SimpleFilter.class, rootFilter)
        );
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.put("RootFilter", rootFilter.serializeNBT());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        rootFilter.deserializeNBT(tag.getCompound("RootFilter"));
    }

}
