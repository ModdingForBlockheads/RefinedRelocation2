package net.blay09.mods.refinedrelocation.fabric.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.refinedrelocation.block.entity.ModBlockEntities;
import net.blay09.mods.refinedrelocation.fabric.client.render.SortingChestBlockEntityRenderer;

public class ModRenderers {
    public static void initialize(BalmRenderers renderers) {
        for (final var sortingChest : ModBlockEntities.sortingChests) {
            renderers.registerBlockEntityRenderer(sortingChest::get, SortingChestBlockEntityRenderer::new);
        }
    }
}
