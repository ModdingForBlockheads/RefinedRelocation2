package net.blay09.mods.refinedrelocation.client.gui.element;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.base.ITickableElement;
import net.blay09.mods.refinedrelocation.client.gui.base.ITooltipElement;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ImageButton;
import net.blay09.mods.refinedrelocation.menu.RootFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.blay09.mods.refinedrelocation.util.TextUtils.formattedTranslation;

public class DeleteFilterButton extends ImageButton implements ITickableElement, ITooltipElement {

    private static boolean shiftGuard;

    private final FilterSlotButton parentSlot;

    public DeleteFilterButton(int x, int y, FilterSlotButton parentSlot) {
        super(x, y, 8, 8, GuiTextures.DELETE_FILTER, it -> {
        });
        this.parentSlot = parentSlot;
        visible = false;
    }

    @Override
    public void tick() {
        visible = parentSlot.hasFilter();

        boolean isShiftDown = Screen.hasShiftDown();
        active = !shiftGuard && isShiftDown;
        if (!isShiftDown) {
            shiftGuard = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && !active) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        shiftGuard = true;
        RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_DELETE_FILTER, parentSlot.getFilterIndex());
    }

    @Override
    public void addTooltip(List<Component> list) {
        list.add(formattedTranslation(ChatFormatting.RED, "gui.refinedrelocation:root_filter.delete_filter"));
        if (!active) {
            list.add(formattedTranslation(ChatFormatting.YELLOW, "gui.refinedrelocation:root_filter.hold_shift_to_delete"));
        } else {
            list.add(formattedTranslation(ChatFormatting.YELLOW, "gui.refinedrelocation:root_filter.delete_cannot_be_undone"));
        }
    }

}
