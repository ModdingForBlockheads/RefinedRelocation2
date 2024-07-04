package net.blay09.mods.refinedrelocation.client.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.base.ITooltipElement;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ImageButton;
import net.blay09.mods.refinedrelocation.menu.AddFilterMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class AddFilterButton extends ImageButton implements ITooltipElement {

    private IFilter currentFilter;

    public AddFilterButton(int x, int y) {
        super(x, y, 151, 27, GuiTextures.BUTTON_NONE, it -> {
        });
    }

    @Override
    public void onClick(double p_194829_1_, double p_194829_3_) {
        if (currentFilter != null) {
            RefinedRelocationAPI.sendContainerMessageToServer(AddFilterMenu.KEY_ADD_FILTER, currentFilter.getIdentifier());
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (isHovered) {
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xAAFFFFFF);
        }

        if (currentFilter != null) {
            IDrawable filterIcon = currentFilter.getFilterIcon();
            if (filterIcon != null) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                filterIcon.draw(guiGraphics, getX() + 2, getY() + height / 2f - 12, 24, 24);
            }

            Font font = Minecraft.getInstance().font;
            guiGraphics.drawString(font, I18n.get(currentFilter.getLangKey()), getX() + 32, getY() + height / 2 - font.lineHeight / 2, 0xFFFFFF);
        }
    }

    public void setCurrentFilter(@Nullable IFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    @Override
    public void addTooltip(List<Component> tooltip) {
        if (currentFilter != null) {
            for (String text : I18n.get(currentFilter.getDescriptionLangKey()).split("\\\\n")) {
                tooltip.add(Component.literal(text));
            }
        }
    }

}
