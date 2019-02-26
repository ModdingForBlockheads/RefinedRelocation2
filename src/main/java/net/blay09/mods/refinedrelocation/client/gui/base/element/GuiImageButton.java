package net.blay09.mods.refinedrelocation.client.gui.base.element;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.client.ClientProxy;
import net.blay09.mods.refinedrelocation.client.gui.base.IParentScreen;
import net.blay09.mods.refinedrelocation.client.util.TextureAtlasRegion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;

public class GuiImageButton extends GuiElement {

    protected String textureName;
    private TextureAtlasRegion background;
    private TextureAtlasRegion backgroundHover;
    private TextureAtlasRegion backgroundDisabled;
    private boolean enabled = true;

    public GuiImageButton(int x, int y, String textureName) {
        setPosition(x, y);
        setButtonTexture(textureName);
        setSize(background.getWidth(), background.getHeight());
    }

    public void setButtonTexture(String textureName) {
        this.textureName = textureName;
        background = ClientProxy.TEXTURE_ATLAS.getSprite(getNormalTexture());
        backgroundHover = ClientProxy.TEXTURE_ATLAS.getSprite(getHoverTexture());
        backgroundDisabled = ClientProxy.TEXTURE_ATLAS.getSprite(getDisabledTexture());
    }

    protected String getNormalTexture() {
        return RefinedRelocation.MOD_ID + ":" + textureName;
    }

    protected String getHoverTexture() {
        return RefinedRelocation.MOD_ID + ":" + textureName + "_hover";
    }

    protected String getDisabledTexture() {
        return RefinedRelocation.MOD_ID + ":" + textureName + "_disabled";
    }

    @Override
    public final boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1f));
        actionPerformed(mouseButton);
        return true;
    }

    @Override
    public void drawBackground(IParentScreen parentScreen, int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(parentScreen, mouseX, mouseY, partialTicks);
        if (isVisible()) {
            GlStateManager.color4f(1f, 1f, 1f, 1f);
            if (!enabled) {
                backgroundDisabled.draw(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), zLevel);
            } else {
                if (isInside(mouseX, mouseY)) {
                    backgroundHover.draw(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), zLevel);
                } else {
                    background.draw(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), zLevel);
                }
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void actionPerformed(int mouseButton) {

    }

}
