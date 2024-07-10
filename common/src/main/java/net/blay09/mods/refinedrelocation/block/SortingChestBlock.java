package net.blay09.mods.refinedrelocation.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.RefinedRelocationUtils;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.block.entity.ModBlockEntities;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class SortingChestBlock extends AbstractChestBlock<SortingChestBlockEntity> {

    public static final MapCodec<SortingChestBlock> CODEC = RecordCodecBuilder.mapCodec((it) -> it.group(SortingChestType.CODEC.fieldOf("type")
            .forGetter(SortingChestBlock::getChestType), propertiesCodec()).apply(it, SortingChestBlock::new));

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private final SortingChestType chestType;

    public SortingChestBlock(SortingChestType chestType, Properties properties) {
        super(properties.sound(SoundType.WOOD).strength(3f), chestType::getBlockEntityType);
        this.chestType = chestType;
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState state, Level level, BlockPos pos, boolean p_225536_4_) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext useContext) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, useContext.getHorizontalDirection().getOpposite());
    }

    // TODO @Override
    // TODO public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    // TODO     if (itemStack.hasCustomHoverName()) {
    // TODO         if (level.getBlockEntity(pos) instanceof SortingChestBlockEntity sortingChest) {
    // TODO             sortingChest.setCustomName(itemStack.getDisplayName().getString());
    // TODO         }
    // TODO     }
    // TODO }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof SortingChestBlockEntity sortingChest) {
                Balm.getNetworking().openGui(player, sortingChest);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean what) {
        if (state.getBlock() != newState.getBlock()) {
            RefinedRelocationUtils.dropItemHandler(level, pos);
            level.updateNeighbourForOutputSignal(pos, this);
        }

        super.onRemove(state, level, pos, newState, what);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SortingChestBlockEntity(chestType, pos, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return RefinedRelocationUtils.getComparatorInputOverride(state, level, pos);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? createTickerHelper(type,
                ModBlockEntities.sortingChests.get(chestType.ordinal()).get(),
                SortingChestBlockEntity::lidAnimateTick) : createTickerHelper(type,
                ModBlockEntities.sortingChests.get(chestType.ordinal()).get(),
                SortingChestBlockEntity::serverTick);
    }

    public SortingChestType getChestType() {
        return chestType;
    }

    @Override
    protected MapCodec<? extends AbstractChestBlock<SortingChestBlockEntity>> codec() {
        return CODEC;
    }
}
