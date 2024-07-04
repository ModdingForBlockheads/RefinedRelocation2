package net.blay09.mods.refinedrelocation.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.client.BlockHighlightDrawEvent;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.mixin.LevelRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockHighlightHandler {

    public static void onBlockHighlight(BlockHighlightDrawEvent event) {
        final var player = Minecraft.getInstance().player;
        if (player == null || !player.isShiftKeyDown()) {
            return;
        }

        final var level = player.level();
        final var hitResult = event.getHitResult();
        final var blockEntity = level.getBlockEntity(hitResult.getBlockPos());
        if (blockEntity != null) {
            ISortingGridMember sortingMember = Balm.getProviders().getProvider(blockEntity, ISortingGridMember.class);
            if (sortingMember != null) {
                ISortingGrid sortingGrid = sortingMember.getSortingGrid();
                if (sortingGrid != null) {
                    Camera camera = event.getCamera();
                    double camX = camera.getPosition().x;
                    double camY = camera.getPosition().y;
                    double camZ = camera.getPosition().z;
                    for (ISortingGridMember member : sortingGrid.getMembers()) {
                        BlockEntity memberTile = member.getBlockEntity();
                        BlockPos pos = memberTile.getBlockPos();
                        BlockState blockState = level.getBlockState(pos);
                        VoxelShape shape = blockState.getShape(level, pos, CollisionContext.of(player));
                        VertexConsumer vertexBuilder = event.getMultiBufferSource().getBuffer(RenderType.lines());
                        LevelRendererAccessor.callRenderShape(event.getPoseStack(), vertexBuilder, shape, pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, 1f, 1f, 0f, 0.75f);
                    }
                    event.setCanceled(true);
                }
            }
        }
    }

}
