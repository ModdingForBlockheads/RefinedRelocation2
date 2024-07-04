package net.blay09.mods.refinedrelocation.client.render;

import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.state.properties.ChestType;

public class SortingChestTileEntityRenderer extends ChestRenderer<SortingChestBlockEntity> {

    public SortingChestTileEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

//    @Override TODO oof
    protected Material getMaterial(SortingChestBlockEntity tileEntity, ChestType chestType) {
        return tileEntity.getChestType().getMaterial();
    }
}
