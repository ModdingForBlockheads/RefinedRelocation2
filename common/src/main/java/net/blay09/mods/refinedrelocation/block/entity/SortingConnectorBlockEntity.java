package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.grid.SortingGridMember;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SortingConnectorBlockEntity extends BalmBlockEntity implements OnLoadHandler {

    private final ISortingGridMember sortingGridMember = new SortingGridMember();

    public SortingConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.sortingConnector.get(), pos, state);
    }

    @Override
    public void onLoad() {
        sortingGridMember.onFirstTick(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        sortingGridMember.onInvalidate(this);
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(ISortingGridMember.class, sortingGridMember)
        );
    }

}
