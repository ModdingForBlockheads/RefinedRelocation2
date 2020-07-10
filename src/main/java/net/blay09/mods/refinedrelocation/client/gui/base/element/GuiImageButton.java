package net.blay09.mods.refinedrelocation.client.gui.base.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.client.gui.base.GuiTextureSpriteButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class GuiImageButton extends Button {

    private GuiTextureSpriteButton texture;

    public GuiImageButton(int x, int y, int width, int height, GuiTextureSpriteButton texture, IPressable pressable) {
        super(x, y, width, height, new StringTextComponent(""), pressable);
        this.texture = texture;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            texture.bind();
            if (!active) {
                texture.asDisabled().draw(matrixStack, x, y, width, height, getBlitOffset());
            } else {
                if (isHovered) {
                    texture.asHovered().draw(matrixStack, x, y, width, height, getBlitOffset());
                } else {
                    texture.draw(matrixStack, x, y, width, height, getBlitOffset());
                }
            }
        }
    }

    public void setTexture(GuiTextureSpriteButton texture) {
        this.texture = texture;
    }
}
