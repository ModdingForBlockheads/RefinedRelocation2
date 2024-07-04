package net.blay09.mods.refinedrelocation.client.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.menu.ChecklistFilterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class ChecklistEntryButton extends Button {

    private final IChecklistFilter filter;
    private final IDrawable texture;
    private final IDrawable textureChecked;

    private int currentOption = -1;

    public ChecklistEntryButton(int x, int y, IChecklistFilter filter) {
        super(x, y, 151, 11, Component.empty(), it -> {
        }, DEFAULT_NARRATION);
        this.filter = filter;
        texture = GuiTextures.CHECKLIST;
        textureChecked = GuiTextures.CHECKLIST_CHECKED;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (currentOption != -1) {
            boolean oldState = filter.isOptionChecked(currentOption);
            filter.setOptionChecked(currentOption, !oldState);
            if (!oldState) {
                RefinedRelocationAPI.sendContainerMessageToServer(ChecklistFilterMenu.KEY_CHECK, currentOption);
            } else {
                RefinedRelocationAPI.sendContainerMessageToServer(ChecklistFilterMenu.KEY_UNCHECK, currentOption);
            }
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x66FFFFFF);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if (currentOption != -1) {
            if (filter.isOptionChecked(currentOption)) {
                textureChecked.draw(guiGraphics, getX() + 1, getY() + height / 2f - 11 / 2f);
            } else {
                texture.draw(guiGraphics, getX() + 1,  getY()+ height / 2f - 11 / 2f);
            }

            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, I18n.get(filter.getOptionLangKey(currentOption)), getX() + 14, getY() + height / 2 - font.lineHeight / 2, 0xFFFFFF);
        }
    }

    public void setCurrentOption(int currentOption) {
        this.currentOption = currentOption;
    }

}
