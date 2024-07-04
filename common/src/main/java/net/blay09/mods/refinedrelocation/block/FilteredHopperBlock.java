package net.blay09.mods.refinedrelocation.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.refinedrelocation.block.entity.FilteredHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FilteredHopperBlock extends FastHopperBlock {

    public static final MapCodec<FilteredHopperBlock> CODEC = simpleCodec(FilteredHopperBlock::new);

    public FilteredHopperBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FilteredHopperBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
