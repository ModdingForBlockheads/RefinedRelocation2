package net.blay09.mods.refinedrelocation.client;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.refinedrelocation.block.ModBlocks;
import net.blay09.mods.refinedrelocation.block.entity.ModBlockEntities;
import net.blay09.mods.refinedrelocation.client.render.SortingChestTileEntityRenderer;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRenderers {
    public static void initialize(BalmRenderers renderers) {
        for (DeferredObject<BlockEntityType<SortingChestBlockEntity>> sortingChest : ModBlockEntities.sortingChests) {
            renderers.registerBlockEntityRenderer(sortingChest::get, SortingChestTileEntityRenderer::new);
        }

        renderers.setBlockRenderType(() -> ModBlocks.sortingInterface, RenderType.cutout());
    }
}
