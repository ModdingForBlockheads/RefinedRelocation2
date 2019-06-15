package net.blay09.mods.refinedrelocation.client.gui.element;

import net.blay09.mods.refinedrelocation.api.Priority;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.client.gui.base.ITickableElement;
import net.blay09.mods.refinedrelocation.client.gui.base.ITooltipElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class GuiButtonPriority extends Button implements ITooltipElement, ITickableElement {

    private static final Priority.Enum[] values = Priority.Enum.values();
    private final ISortingInventory sortingInventory;
    private int currentIndex = 2;

    public GuiButtonPriority(int x, int y, int width, int height, ISortingInventory sortingInventory) {
        super(x, y, width, height, I18n.format(values[2].getLangKey()), it -> {
        });
        this.sortingInventory = sortingInventory;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isMouseOver(mouseX, mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundHandler());
            onClick(mouseX, mouseY, mouseButton);
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onClick(double mouseX, double mouseY, int mouseButton) {
        if (!Screen.hasShiftDown()) {
            int oldIndex = currentIndex != -1 ? currentIndex : (sortingInventory.getPriority() + 1000) / 500;
            if (mouseButton == 0) {
                oldIndex++;
            } else if (mouseButton == 1) {
                oldIndex--;
            }

            currentIndex = Math.max(0, Math.min(values.length - 1, oldIndex));
            setMessage(I18n.format(values[currentIndex].getLangKey()));
            sortingInventory.setPriority(values[currentIndex].getPriority());
        } else {
            currentIndex = -1;
            int oldPriority = sortingInventory.getPriority();
            if (mouseButton == 0) {
                oldPriority += 10;
            } else if (mouseButton == 1) {
                oldPriority -= 10;
            }

            oldPriority = Math.max(-999, Math.min(999, oldPriority));
            sortingInventory.setPriority(oldPriority);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        currentIndex = -1;
        int oldPriority = sortingInventory.getPriority();
        if (delta > 0) {
            oldPriority++;
        } else if (delta < 0) {
            oldPriority--;
        }

        oldPriority = Math.max(-999, Math.min(999, oldPriority));
        sortingInventory.setPriority(oldPriority);
        return true;
    }

    @Override
    public void tick() {
        currentIndex = -1;
        for (int i = 0; i < values.length; i++) {
            if (values[i].getPriority() == sortingInventory.getPriority()) {
                currentIndex = i;
                break;
            }
        }

        setMessage(currentIndex != -1 ? I18n.format(values[currentIndex].getLangKey()) : I18n.format("gui.refinedrelocation:root_filter.priority_custom", sortingInventory.getPriority()));
    }

    @Override
    public void addTooltip(List<String> list) {
        if (!Screen.hasShiftDown()) {
            list.add(TextFormatting.GREEN + I18n.format("gui.refinedrelocation:root_filter.priority_increase"));
            list.add(TextFormatting.RED + I18n.format("gui.refinedrelocation:root_filter.priority_decrease"));
        } else {
            list.add(TextFormatting.GREEN + I18n.format("gui.refinedrelocation:root_filter.priority_increase10"));
            list.add(TextFormatting.RED + I18n.format("gui.refinedrelocation:root_filter.priority_decrease10"));
        }
        if (currentIndex != -1) {
            list.add("");
            list.add(I18n.format("gui.refinedrelocation:root_filter.priority_tooltip", values[currentIndex].getPriority()));
        }
    }

}
