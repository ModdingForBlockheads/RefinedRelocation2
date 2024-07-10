package net.blay09.mods.refinedrelocation.fabric.client.gui.element;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.fabric.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.fabric.client.gui.RootFilterScreen;
import net.blay09.mods.refinedrelocation.fabric.client.gui.base.ITooltipElement;
import net.blay09.mods.refinedrelocation.menu.RootFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.blay09.mods.refinedrelocation.util.TextUtils.formattedTranslation;

public class FilterSlotButton extends Button implements ITooltipElement {

    private final RootFilterScreen parentGui;
    private final IDrawable texture;
    private final RootFilter rootFilter;
    private final int index;

    public FilterSlotButton(int x, int y, RootFilterScreen parentGui, RootFilter rootFilter, int index) {
        super(x, y, 24, 24, Component.empty(), it -> {
        }, DEFAULT_NARRATION);
        this.parentGui = parentGui;
        this.rootFilter = rootFilter;
        this.index = index;
        texture = GuiTextures.FILTER_SLOT;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        texture.draw(guiGraphics, getX(), getY());

        IFilter filter = rootFilter.getFilter(index);
        if (filter != null) {
            IDrawable filterIcon = filter.getFilterIcon();
            if (filterIcon != null) {
                filterIcon.draw(guiGraphics, getX(), getY(), 24, 24);
            }
        }
        if (parentGui.isTopMostElement(this, mouseX, mouseY)) {
            guiGraphics.fill(getX() + 1, getY() + 1, getX() + width - 1, getY() + height - 1, 0x99FFFFFF);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        IFilter filter = rootFilter.getFilter(index);
        if (filter == null) {
            RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_OPEN_ADD_FILTER, 0);
            // Minecraft.getInstance().displayGuiScreen(new AddFilterScreen(parentGui, parentGui.getPlayerInventory(), parentGui.getTitle()));
        } else {
            RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_EDIT_FILTER, index);
        }
    }

    @Override
    public void addTooltip(List<Component> list) {
        IFilter filter = rootFilter.getFilter(index);
        if (filter == null) {
            list.add(formattedTranslation(ChatFormatting.GRAY, "gui.refinedrelocation:root_filter.no_filter_set"));
            list.add(formattedTranslation(ChatFormatting.YELLOW, "gui.refinedrelocation:root_filter.click_to_add_filter"));
        } else {
            list.add(Component.translatable(filter.getLangKey()));
            if (filter.hasConfiguration()) {
                list.add(formattedTranslation(ChatFormatting.YELLOW, "gui.refinedrelocation:root_filter.click_to_configure"));
            } else {
                list.add(formattedTranslation(ChatFormatting.GRAY, "gui.refinedrelocation:root_filter.not_configurable"));
            }
        }
    }

    public int getFilterIndex() {
        return index;
    }

    public boolean hasFilter() {
        return rootFilter.getFilter(index) != null;
    }
}
