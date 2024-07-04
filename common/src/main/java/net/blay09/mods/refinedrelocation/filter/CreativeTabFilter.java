package net.blay09.mods.refinedrelocation.filter;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.menu.ChecklistFilterMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreativeTabFilter implements IChecklistFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":creative_tab_filter";

    private final List<CreativeModeTab> allTabs = CreativeModeTabs.allTabs();
    public Set<ResourceLocation> enabledTabs = new HashSet<>();

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean isFilterUsable(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean passes(BlockEntity blockEntity, ItemStack itemStack, ItemStack originalStack) {
        for (ResourceLocation creativeTabId : enabledTabs) {
            final var creativeTab = BuiltInRegistries.CREATIVE_MODE_TAB.get(creativeTabId);
            if (creativeTab.contains(itemStack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        final var tag = new CompoundTag();
        final var list = new ListTag();
        for (ResourceLocation tab : enabledTabs) {
            list.add(StringTag.valueOf(tab.toString()));
        }
        tag.put("Tabs", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        final var list = tag.getList("Tabs", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            enabledTabs.add(ResourceLocation.parse(list.getString(i)));
        }
    }

    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:creative_tab_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:creative_tab_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.CREATIVE_TAB_FILTER_ICON;
    }

    @Override
    public String getOptionLangKey(int option) {
        final var creativeTabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(allTabs.get(option));
        return "itemGroup." + creativeTabId.toString().replace(':', '.');
    }

    @Override
    public void setOptionChecked(int option, boolean checked) {
        final var creativeTabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(allTabs.get(option));
        if (checked) {
            enabledTabs.add(creativeTabId);
        } else {
            enabledTabs.remove(creativeTabId);
        }
    }

    @Override
    public boolean isOptionChecked(int option) {
        final var creativeTabId = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(allTabs.get(option));
        return enabledTabs.contains(creativeTabId);
    }

    @Override
    public int getOptionCount() {
        return allTabs.size();
    }

    @Override
    public int getVisualOrder() {
        return 700;
    }

    @Nullable
    @Override
    public MenuProvider getConfiguration(Player player, BlockEntity blockEntity, int rootFilterIndex, int filterIndex) {
        return new BalmMenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new ChecklistFilterMenu(i, playerInventory, blockEntity, rootFilterIndex, CreativeTabFilter.this);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable("container.refinedrelocation.creative_tab_filter");
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(blockEntity.getBlockPos());
                buf.writeByte(rootFilterIndex);
                buf.writeByte(filterIndex);
            }
        };
    }

    @Override
    public boolean hasConfiguration() {
        return true;
    }
}
