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

public class SameModFilter implements IFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":same_mod_filter";

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
        if(container != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack otherStack = container.getItem(i);
                if (!otherStack.isEmpty()) {
                    if (otherStack == originalStack || itemStack.getItem() == otherStack.getItem()) {
                        return true;
                    }

                    String modId = Balm.getRegistries().getKey(itemStack.getItem()).getNamespace();
                    String otherModId = Balm.getRegistries().getKey(otherStack.getItem()).getNamespace();
                    if (modId.equals(otherModId)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
    }

    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:same_mod_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:same_mod_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.SAME_MOD_FILTER_ICON;
    }

}
