package net.blay09.mods.refinedrelocation.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.api.tag.BalmItemTags;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.ChecklistFilter;
import net.blay09.mods.refinedrelocation.fabric.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.menu.ChecklistFilterMenu;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PresetFilter implements ChecklistFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":preset_filter";

    public abstract static class Preset {
        private final String id;

        public Preset(String id) {
            this.id = id;
        }

        public final String getId() {
            return id;
        }

        public abstract boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags);
    }

    private static final Map<String, Preset> presetMap = Maps.newHashMap();
    private static final List<Preset> presetList = Lists.newArrayList();

    public static final Preset ORES = new Preset("ores") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.is(BalmItemTags.ORES);
        }
    };

    public static final Preset INGOTS = new Preset("ingots") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.is(BalmItemTags.INGOTS);
        }
    };

    public static final Preset NUGGETS = new Preset("nuggets") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.is(BalmItemTags.NUGGETS);
        }
    };

    public static final Preset GEMS = new Preset("gems") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.is(BalmItemTags.GEMS);
        }
    };

    public static final Preset DYES = new Preset("dyes") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.is(BalmItemTags.DYES);
        }
    };

    public static final Preset FOOD = new Preset("food") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.has(DataComponents.FOOD);
        }
    };

    public static final Preset FUEL_ITEMS = new Preset("fuel") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return Balm.getHooks().getBurnTime(itemStack) > 0;
        }
    };

    public static final Preset BLOCKS = new Preset("blocks") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.getItem() instanceof BlockItem;
        }
    };

    public static final Preset UNSTACKABLE = new Preset("unstackable") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.getMaxStackSize() <= 1;
        }
    };

    public static final Preset REPAIRABLE = new Preset("repairable") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return Balm.getHooks().isRepairable(itemStack);
        }
    };

    public static final Preset WEAPONS = new Preset("weapons") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof BowItem;
        }
    };

    public static final Preset ARMORS = new Preset("armors") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.getItem() instanceof ArmorItem;
        }
    };

    public static final Preset TOOLS = new Preset("tools") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.getItem() == Items.CARROT_ON_A_STICK || itemStack.getItem() == Items.FLINT_AND_STEEL || itemStack.getItem() instanceof DiggerItem || itemStack.getItem() instanceof FishingRodItem || itemStack.getItem() instanceof ShearsItem;
        }
    };

    public static final Preset ENCHANTED = new Preset("enchanted") {
        @Override
        public boolean passes(ItemStack itemStack, Set<TagKey<Item>> tags) {
            return itemStack.isEnchanted();
        }
    };

    static {
        registerPreset(BLOCKS);
        registerPreset(ORES);
        registerPreset(INGOTS);
        registerPreset(NUGGETS);
        registerPreset(GEMS);
        registerPreset(FOOD);
        registerPreset(DYES);
        registerPreset(UNSTACKABLE);
        registerPreset(FUEL_ITEMS);
        registerPreset(REPAIRABLE);
        registerPreset(WEAPONS);
        registerPreset(ARMORS);
        registerPreset(TOOLS);
        registerPreset(ENCHANTED);

        presetList.addAll(presetMap.values());
    }

    private static void registerPreset(Preset preset) {
        presetMap.put(preset.getId(), preset);
        presetList.add(preset);
    }

    private final boolean[] presetStates = new boolean[presetList.size()];

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
        final var tags = itemStack.getTags().collect(Collectors.toSet());
        for (int i = 0; i < presetList.size(); i++) {
            if (presetStates[i] && presetList.get(i).passes(itemStack, tags)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for (int i = 0; i < presetStates.length; i++) {
            if (presetStates[i]) {
                list.add(StringTag.valueOf(presetList.get(i).getId()));
            }
        }
        tag.put("Presets", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag list = tag.getList("Presets", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            Preset preset = presetMap.get(list.getString(i));
            if (preset != null) {
                int index = presetList.indexOf(preset);
                if (index != -1) {
                    presetStates[index] = true;
                }
            }
        }
    }

    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:preset_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:preset_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.PRESET_FILTER_ICON;
    }

    @Override
    public String getOptionLangKey(int option) {
        return "filter.refinedrelocation:preset_filter.option_" + presetList.get(option).getId();
    }

    @Override
    public void setOptionChecked(int option, boolean checked) {
        presetStates[option] = checked;
    }

    @Override
    public boolean isOptionChecked(int option) {
        return presetStates[option];
    }

    @Override
    public int getOptionCount() {
        return presetMap.size();
    }

    @Override
    public int getVisualOrder() {
        return 1000;
    }

    @Nullable
    @Override
    public MenuProvider getConfiguration(Player player, BlockEntity blockEntity, int rootFilterIndex, int filterIndex) {
        return new BalmMenuProvider<ChecklistFilterMenu.Data>() {
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new ChecklistFilterMenu(i, playerInventory, blockEntity, rootFilterIndex, PresetFilter.this);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable(PresetFilter.this.getLangKey());
            }

            @Override
            public ChecklistFilterMenu.Data getScreenOpeningData(ServerPlayer serverPlayer) {
                return new ChecklistFilterMenu.Data(blockEntity.getBlockPos(), rootFilterIndex, filterIndex);
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ChecklistFilterMenu.Data> getScreenStreamCodec() {
                return ChecklistFilterMenu.Data.STREAM_CODEC;
            }
        };
    }

    @Override
    public boolean hasConfiguration() {
        return true;
    }
}
