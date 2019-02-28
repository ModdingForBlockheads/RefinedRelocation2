package net.blay09.mods.refinedrelocation.client.render;

import net.blay09.mods.refinedrelocation.ModBlocks;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.RefinedRelocationConfig;
import net.blay09.mods.refinedrelocation.block.BlockSortingChest;
import net.blay09.mods.refinedrelocation.tile.TileSortingChest;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSortingChest extends TileEntityRenderer<TileSortingChest> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RefinedRelocation.MOD_ID, "textures/entity/sorting_chest/normal.png");

    public static final TileEntityItemStackRenderer sortingChestItemRenderer = new TileEntityItemStackRenderer() {
        private TileSortingChest sortingChest;

        @Override
        public void renderByItem(ItemStack itemStack) {
            // Lazy-load the tile entity to prevent it from being loaded in client setup before capabilities are initialized
            if (sortingChest == null) {
                sortingChest = new TileSortingChest();
            }

            TileEntityRendererDispatcher.instance.renderAsItem(sortingChest);
        }
    };

    private final ModelChest model = new ModelChest();

    @Override
    public void render(TileSortingChest tileEntity, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.enableDepthTest();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.depthMask(true);

        IBlockState state = tileEntity.hasWorld() ? tileEntity.getBlockState() : ModBlocks.sortingChest.getDefaultState().with(BlockChest.FACING, EnumFacing.SOUTH);

        if (destroyStage >= 0) {
            bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4f, 4f, 1f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        } else {
            bindTexture(TEXTURE);
            GlStateManager.color4f(1f, 1f, 1f, 1f);
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        GlStateManager.translatef((float) x, (float) y + 1f, (float) z + 1f);
        GlStateManager.scalef(1f, -1f, -1f);

        float angle = state.get(BlockSortingChest.FACING).getHorizontalAngle();
        if (Math.abs(angle) > 0f) {
            GlStateManager.translatef(0.5f, 0.5f, 0.5f);
            GlStateManager.rotatef(angle, 0f, 1f, 0f);
            GlStateManager.translatef(-0.5f, -0.5f, -0.5f);
        }

        updateLidAngle(tileEntity, partialTicks, model);

        model.renderAll();

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color4f(1f, 1f, 1f, 1f);

        if (destroyStage >= 0) {
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }
    }

    @Override
    protected void drawNameplate(TileSortingChest tileEntity, String name, double x, double y, double z, int maxDistance) {
        if (RefinedRelocationConfig.CLIENT.renderChestNameTags.get()) {
            super.drawNameplate(tileEntity, name, x, y, z, maxDistance);
        }
    }

    private void updateLidAngle(IChestLid lid, float partialTicks, ModelChest model) {
        float f = lid.getLidAngle(partialTicks);
        f = 1f - f;
        f = 1f - f * f * f;
        model.getLid().rotateAngleX = -(f * ((float) Math.PI / 2f));
    }
}
