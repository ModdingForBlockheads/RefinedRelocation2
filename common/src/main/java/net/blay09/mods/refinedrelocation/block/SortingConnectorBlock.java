package net.blay09.mods.refinedrelocation.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.block.entity.SortingConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class SortingConnectorBlock extends BaseEntityBlock {

    public static final MapCodec<SortingConnectorBlock> CODEC = simpleCodec(SortingConnectorBlock::new);

    private static final VoxelShape CENTER_SHAPE = box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape[] FACING_SHAPES = new VoxelShape[]{
            box(5, 0, 5, 11, 5, 11),         // Down
            box(5, 11, 5, 11, 16, 11),         // Up
            box(5, 5, 0, 11, 11, 5),         // North
            box(5, 5, 11, 11, 11, 16),         // South
            box(0, 5, 5, 5, 11, 11),         // West
            box(11, 5, 5, 16, 11, 11)          // East
    };

    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty CORNER = BooleanProperty.create("corner");
    public static final BooleanProperty[] CONNECTIONS = new BooleanProperty[]{
            DOWN,
            UP,
            NORTH,
            SOUTH,
            WEST,
            EAST
    };

    public SortingConnectorBlock(Properties properties) {
        super(properties.sound(SoundType.METAL).strength(0.3f));

        BlockState state = defaultBlockState();
        for (BooleanProperty property : CONNECTIONS) {
            state = state.setValue(property, false);
        }
        registerDefaultState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        VoxelShape boundingBox = CENTER_SHAPE;
        for (int i = 0; i < CONNECTIONS.length; i++) {
            BooleanProperty property = CONNECTIONS[i];
            if (state.getValue(property)) {
                boundingBox = Shapes.or(boundingBox, FACING_SHAPES[i]);
            }
        }

        return boundingBox;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST, CORNER);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean wat) {
        super.neighborChanged(state, level, pos, block, fromPos, wat);
        BlockState newState = getConnectionState(level, pos, state);
        if (newState != state) {
            level.setBlock(pos, newState, 3);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getConnectionState(context.getLevel(), context.getClickedPos(), defaultBlockState());
    }

    private BlockState getConnectionState(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        Direction.Axis axis = null;
        boolean isCorner = false;
        int connectionCount = 0;
        for (Direction facing : Direction.values()) {
            BlockPos neighbourPos = pos.relative(facing);
            BlockEntity blockEntity = blockGetter.getBlockEntity(neighbourPos);
            if (blockEntity != null && Balm.getProviders().getProvider(blockEntity, ISortingGridMember.class) != null) {
                state = state.setValue(CONNECTIONS[facing.get3DDataValue()], true);
                if (axis != null && axis != facing.getAxis()) {
                    isCorner = true;
                }
                axis = facing.getAxis();
                connectionCount++;
            } else {
                state = state.setValue(CONNECTIONS[facing.get3DDataValue()], false);
            }
        }

        if (connectionCount == 1) {
            isCorner = true;
        }

        return state.setValue(CORNER, isCorner);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SortingConnectorBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
