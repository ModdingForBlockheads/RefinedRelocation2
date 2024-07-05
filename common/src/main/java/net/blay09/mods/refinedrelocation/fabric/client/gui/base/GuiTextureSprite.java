package net.blay09.mods.refinedrelocation.fabric.client.gui.base;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class GuiTextureSprite implements IDrawable {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "textures/gui/gui.png");

    private final int spriteX;
    private final int spriteY;
    private final int spriteWidth;
    private final int spriteHeight;

    public GuiTextureSprite(int spriteX, int spriteY, int spriteWidth, int spriteHeight) {
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, double x, double y) {
        guiGraphics.blit(TEXTURE, (int) x, (int) y, spriteX, spriteY, spriteWidth, spriteHeight, 256, 256);
    }

    @Override
    public void draw(GuiGraphics guiGraphics, double x, double y, double width, double height) {
        guiGraphics.blit(TEXTURE, (int) x, (int) y, (int) width, (int) height, spriteX, spriteY, spriteWidth, spriteHeight, 256, 256);
    }
}
