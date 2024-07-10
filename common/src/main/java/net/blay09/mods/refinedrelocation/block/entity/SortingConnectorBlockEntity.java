package net.blay09.mods.refinedrelocation.block.entity;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.block.entity.OnLoadHandler;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.api.grid.SortingGridMember;
import net.blay09.mods.refinedrelocation.grid.SortingGridMemberImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SortingConnectorBlockEntity extends BalmBlockEntity implements OnLoadHandler {

    private final SortingGridMember sortingGridMember = new SortingGridMemberImpl(this);

    public SortingConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.sortingConnector.get(), pos, state);
    }

    @Override
    public void onLoad() {
        sortingGridMember.firstTick();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        sortingGridMember.invalidate();
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(
                new BalmProvider<>(SortingGridMember.class, sortingGridMember)
        );
    }

}
