package net.blay09.mods.refinedrelocation.fabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;

public class SortingChestTileEntityRenderer extends ChestRenderer<SortingChestBlockEntity> {

    public SortingChestTileEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(SortingChestBlockEntity $$0, float $$1, PoseStack $$2, MultiBufferSource $$3, int $$4, int $$5) {
        super.render($$0, $$1, $$2, $$3, $$4, $$5);
    }

    //    @Override TODO oof
    protected Material getMaterial(SortingChestBlockEntity tileEntity, ChestType chestType) {
        return tileEntity.getChestType().getMaterial();
    }
}
