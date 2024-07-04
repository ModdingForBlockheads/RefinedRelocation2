package net.blay09.mods.refinedrelocation.client.gui.element;

import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.client.gui.RootFilterScreen;
import net.blay09.mods.refinedrelocation.client.gui.base.ITickableElement;
import net.blay09.mods.refinedrelocation.client.gui.base.ITooltipElement;
import net.blay09.mods.refinedrelocation.client.gui.base.element.ImageButton;
import net.blay09.mods.refinedrelocation.menu.RootFilterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.blay09.mods.refinedrelocation.util.TextUtils.formattedTranslation;

public class AllowListButton extends ImageButton implements ITickableElement, ITooltipElement {

    private final RootFilterScreen parentGui;
    private final FilterSlotButton parentSlot;

    private boolean lastBlacklist;

    public AllowListButton(int x, int y, RootFilterScreen parentGui, FilterSlotButton parentSlot) {
        super(x, y, 8, 8, GuiTextures.FILTER_WHITELIST, it -> {
        });
        this.parentGui = parentGui;
        this.parentSlot = parentSlot;
        visible = false;
    }

    @Override
    public void tick() {
        boolean nowBlacklist = parentGui.getMenu().getRootFilter().isBlacklist(parentSlot.getFilterIndex());
        if (lastBlacklist != nowBlacklist) {
            setTexture(nowBlacklist ? GuiTextures.FILTER_BLACKLIST : GuiTextures.FILTER_WHITELIST);
            lastBlacklist = nowBlacklist;
        }

        visible = parentSlot.hasFilter();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        boolean isBlacklist = !parentGui.getMenu().getRootFilter().isBlacklist(parentSlot.getFilterIndex());
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt(RootFilterMenu.KEY_BLACKLIST_INDEX, parentSlot.getFilterIndex());
        tagCompound.putBoolean(RootFilterMenu.KEY_BLACKLIST, isBlacklist);
        RefinedRelocationAPI.sendContainerMessageToServer(RootFilterMenu.KEY_BLACKLIST, tagCompound);
        parentGui.getMenu().getRootFilter().setIsBlacklist(parentSlot.getFilterIndex(), isBlacklist);
    }

    @Override
    public void addTooltip(List<Component> list) {
        boolean nowBlacklist = parentGui.getMenu().getRootFilter().isBlacklist(parentSlot.getFilterIndex());
        list.add(formattedTranslation(ChatFormatting.WHITE, nowBlacklist ? "gui.refinedrelocation:root_filter.blacklist" : "gui.refinedrelocation:root_filter.whitelist"));
        list.add(formattedTranslation(ChatFormatting.YELLOW, "gui.refinedrelocation:root_filter.click_to_toggle"));
    }

}
