package net.blay09.mods.refinedrelocation.filter;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiTextures;
import net.blay09.mods.refinedrelocation.menu.NameFilterMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class NameFilter implements IFilter {

    public static final String ID = RefinedRelocation.MOD_ID + ":name_filter";

    private static final Pattern WILDCARD_PATTERN = Pattern.compile("[^*]+|(\\*)");
    private static final Matcher WILDCARD_MATCHER = WILDCARD_PATTERN.matcher("");

    public enum NameFilterType {
        NAME,
        TAG,
        MOD
    }

    private String value = "";
    private Pattern[] cachedPatterns;
    private NameFilterType[] cachedNameFilterType;

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
        String itemName = null;
        Set<TagKey<Item>> tags = null;
        Pattern[] patterns = getPatterns();
        for (int i = 0; i < patterns.length; i++) {
            Pattern pattern = patterns[i];
            if (pattern != null) {
                if (cachedNameFilterType[i] == NameFilterType.TAG) {
                    if (tags == null) {
                        tags = itemStack.getTags().collect(Collectors.toSet());
                    }
                    Matcher matcher = pattern.matcher("");
                    for (TagKey<Item> tag : tags) {
                        matcher.reset(tag.toString());
                        if (matcher.matches()) {
                            return true;
                        }

                        matcher.reset(tag.location().getPath());
                        if (matcher.matches()) {
                            return true;
                        }
                    }
                } else if (cachedNameFilterType[i] == NameFilterType.MOD) {
                    ResourceLocation registryName = Balm.getRegistries().getKey(itemStack.getItem());
                    if (registryName != null) {
                        Matcher matcher = pattern.matcher(registryName.getNamespace());
                        if (matcher.matches()) {
                            return true;
                        }
                    }
                } else {
                    if (itemName == null) {
                        itemName = itemStack.getDisplayName().getString();
                    }
                    if (pattern.matcher(itemName).matches()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setValue(String value) {
        this.value = value;
        this.cachedPatterns = null;
    }

    public String getValue() {
        return value;
    }

    public Pattern[] getPatterns() {
        if (cachedPatterns == null) {
            String[] patternsSplit = value.split("[\n,]");
            cachedPatterns = new Pattern[patternsSplit.length];
            cachedNameFilterType = new NameFilterType[patternsSplit.length];
            for (int i = 0; i < patternsSplit.length; i++) {
                if ((patternsSplit[i].startsWith("tag:") || patternsSplit[i].startsWith("ore:")) && patternsSplit[i].length() > 4) {
                    patternsSplit[i] = patternsSplit[i].substring(4);
                    cachedNameFilterType[i] = NameFilterType.TAG;
                } else if (patternsSplit[i].startsWith("mod:") && patternsSplit[i].length() > 4) {
                    patternsSplit[i] = patternsSplit[i].substring(4).toLowerCase(Locale.ENGLISH).replace(' ', '*');
                    cachedNameFilterType[i] = NameFilterType.MOD;
                } else if (patternsSplit[i].isEmpty()) {
                    continue;
                }
                WILDCARD_MATCHER.reset(patternsSplit[i]);
                StringBuffer sb = new StringBuffer();
                while (WILDCARD_MATCHER.find()) {
                    if (WILDCARD_MATCHER.group(1) != null) {
                        WILDCARD_MATCHER.appendReplacement(sb, ".*");
                    } else {
                        WILDCARD_MATCHER.appendReplacement(sb, "\\\\Q" + WILDCARD_MATCHER.group(0) + "\\\\E");
                    }
                }
                WILDCARD_MATCHER.appendTail(sb);
                try {
                    cachedPatterns[i] = Pattern.compile(sb.toString());
                } catch (PatternSyntaxException e) {
                    RefinedRelocation.logger.error("Caught an exception in the pattern compilation for the Name Filter. This should never happen, please report: {} => {}", patternsSplit[i], sb.toString());
                }
            }
        }
        return cachedPatterns;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Patterns", value);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        value = tag.getString("Patterns");
    }

    @Override
    public String getLangKey() {
        return "filter.refinedrelocation:name_filter";
    }

    @Override
    public String getDescriptionLangKey() {
        return "filter.refinedrelocation:name_filter.description";
    }

    @Override
    public IDrawable getFilterIcon() {
        return GuiTextures.NAME_FILTER_ICON;
    }

    @Override
    public int getVisualOrder() {
        return 900;
    }

    @Nullable
    @Override
    public MenuProvider getConfiguration(Player player, BlockEntity blockEntity, int rootFilterIndex, int filterIndex) {
        return new BalmMenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
                return new NameFilterMenu(i, playerInventory, blockEntity, rootFilterIndex, NameFilter.this);
            }

            @Override
            public Component getDisplayName() {
                return Component.translatable(NameFilter.this.getLangKey());
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
