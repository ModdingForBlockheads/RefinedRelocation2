package net.blay09.mods.refinedrelocation.filter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.menu.ChecklistFilterMenu;
import net.minecraft.core.Registry;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ModFilter implements IChecklistFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":mod_filter";

    private static class ModWithName {
        private final int index;
        private final String id;
        private final String name;

        public ModWithName(int index, String id, String name) {
            this.index = index;
            this.id = id;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static final Map<String, ModWithName> modList = Maps.newHashMap();
    public static String[] modIds = new String[0];

    public static void gatherMods() {
        Set<String> modSet = Sets.newHashSet();
        for (ResourceLocation registryName : Balm.getRegistries().getItemKeys()) {
            modSet.add(registryName.getNamespace());
        }
        String[] unsorted = modSet.toArray(new String[0]);
        Arrays.sort(unsorted, (o1, o2) -> {
            if (o1.equals("minecraft")) {
                return -1;
            } else if (o2.equals("minecraft")) {
                return 1;
            }
            return o2.compareTo(o1);
        });
        setModList(unsorted);
    }

    public static void setModList(String[] modIDs) {
        ModFilter.modIds = modIDs;
        modList.clear();
        for (int i = 0; i < modIDs.length; i++) {
            if (modIDs[i].equals("minecraft")) {
                modList.put(modIDs[i], new ModWithName(i, modIDs[i], "Minecraft"));
            } else {
                String modName = Balm.getModName(modIDs[i]);
                modList.put(modIDs[i], new ModWithName(i, modIDs[i], modName != null ? modName : modIDs[i]));
            }
        }
    }

    private final boolean[] modStates = new boolean[modIds.length];

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
        ResourceLocation resourceLocation = Balm.getRegistries().getKey(itemStack.getItem());
        if (resourceLocation != null) {
            ModWithName modWithName = modList.get(resourceLocation.getNamespace());
            return modWithName != null && modStates[modWithName.getIndex()];
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < modStates.length; i++) {
            if (modStates[i]) {
                list.add(StringTag.valueOf(modIds[i]));
            }
        }
        tag.put("Mods", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag list = tag.getList("Mods", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            String modId = list.getString(i);
            for (int j = 0; j < modIds.length; j++) {
                if (modIds[j].equals(modId)) {
                    modStates[j] = true;
                }
            }
        }
    }

    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:mod_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:mod_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.MOD_FILTER_ICON;
    }

    @Override
    public String getOptionLangKey(int option) {
        return modList.get(modIds[option]).getName();
    }

    @Override
    public void setOptionChecked(int option, boolean checked) {
        modStates[option] = checked;
    }

    @Override
    public boolean isOptionChecked(int option) {
        return modStates[option];
    }

    @Override
    public int getOptionCount() {
        return modIds.length;
    }

    @Override
    public int getVisualOrder() {
        return 800;
    }

    @Nullable
    @Override
    public MenuProvider getConfiguration(Player player, BlockEntity blockEntity, int rootFilterIndex, int filterIndex) {
        return new BalmMenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new ChecklistFilterMenu(i, playerInventory, blockEntity, rootFilterIndex, ModFilter.this);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable(ModFilter.this.getLangKey());
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
