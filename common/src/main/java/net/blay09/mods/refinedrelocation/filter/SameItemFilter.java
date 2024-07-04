package net.blay09.mods.refinedrelocation.filter;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SameItemFilter implements IFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":same_item_filter";

    private boolean ignoreNBT = true;

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean isFilterUsable(BlockEntity blockEntity) {
        return Balm.getProviders().getProvider(blockEntity, ISortingInventory.class) != null;
    }

    @Override
    public boolean passes(BlockEntity blockEntity, ItemStack itemStack, ItemStack originalStack) {
        Container container = Balm.getProviders().getProvider(blockEntity, Container.class);
        if (container != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack otherStack = container.getItem(i);
                if (!otherStack.isEmpty()) {
                    if (otherStack == originalStack || itemStack.getItem() != otherStack.getItem()) {
                        continue;
                    }

                    if (!ignoreNBT && !ItemStack.isSameItemSameComponents(itemStack, otherStack)) {
                        continue;
                    }

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("IgnoreNBT", ignoreNBT);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ignoreNBT = tag.getBoolean("IgnoreNBT");
    }


    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:same_item_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:same_item_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.SAME_ITEM_FILTER_ICON;
    }

}
