package net.blay09.mods.refinedrelocation.fabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blay09.mods.refinedrelocation.block.SortingChestBlock;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;

public class SortingChestBlockEntityRenderer extends ChestRenderer<SortingChestBlockEntity> {

    private static final String BOTTOM = "bottom";
    private static final String LID = "lid";
    private static final String LOCK = "lock";

    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public SortingChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);

        final var modelPart = context.bakeLayer(ModelLayers.CHEST);
        bottom = modelPart.getChild(BOTTOM);
        lid = modelPart.getChild(LID);
        lock = modelPart.getChild(LOCK);
    }

    @Override
    public void render(SortingChestBlockEntity sortingChest, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int brightness, int overlay) {
        final var state = sortingChest.getBlockState();
        poseStack.pushPose();
        float yRot = state.getValue(SortingChestBlock.FACING).toYRot();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        final var openness = sortingChest.getOpenNess(partialTicks);
        final var material = getMaterial(sortingChest);
        final var buffer = material.buffer(multiBufferSource, RenderType::entityCutout);

        lid.xRot = -(openness * 1.5707964F);
        lock.xRot = lid.xRot;
        lid.render(poseStack, buffer, brightness, overlay);
        lock.render(poseStack, buffer, brightness, overlay);
        bottom.render(poseStack, buffer, brightness, overlay);

        poseStack.popPose();
    }

    private Material getMaterial(SortingChestBlockEntity blockEntity) {
        return blockEntity.getChestType().getMaterial();
    }
}
