package net.blay09.mods.refinedrelocation.client.gui.base;

import net.minecraft.client.gui.GuiGraphics;

public class GuiTextureSpriteButton {

    private final GuiTextureSprite texture;
    private final GuiTextureSprite hoveredTexture;
    private final GuiTextureSprite disabledTexture;

    public GuiTextureSpriteButton(GuiTextureSprite texture, GuiTextureSprite hoveredTexture, GuiTextureSprite disabledTexture) {
        this.texture = texture;
        this.hoveredTexture = hoveredTexture;
        this.disabledTexture = disabledTexture;
    }

    public GuiTextureSprite asHovered() {
        return hoveredTexture;
    }

    public GuiTextureSprite asDisabled() {
        return disabledTexture;
    }

    public void draw(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        texture.draw(guiGraphics, x, y, width, height);
    }
}
