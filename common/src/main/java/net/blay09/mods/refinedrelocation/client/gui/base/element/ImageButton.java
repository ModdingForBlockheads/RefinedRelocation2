package net.blay09.mods.refinedrelocation.client.gui.base.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.client.gui.base.GuiTextureSpriteButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ImageButton extends Button {

    private GuiTextureSpriteButton texture;

    public ImageButton(int x, int y, int width, int height, GuiTextureSpriteButton texture, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            if (!active) {
                texture.asDisabled().draw(guiGraphics, getX(), getY(), width, height);
            } else {
                if (isHovered) {
                    texture.asHovered().draw(guiGraphics, getX(), getY(), width, height);
                } else {
                    texture.draw(guiGraphics, getX(), getY(), width, height);
                }
            }
        }
    }

    public void setTexture(GuiTextureSpriteButton texture) {
        this.texture = texture;
    }
}
