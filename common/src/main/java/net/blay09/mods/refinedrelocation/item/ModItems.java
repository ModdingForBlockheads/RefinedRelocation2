package net.blay09.mods.refinedrelocation.item;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.block.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {

    public static DeferredObject<CreativeModeTab> creativeModeTab;

    public static Item sortingUpgrade;

    public static void initialize(BalmItems items) {
        items.registerItem(() -> sortingUpgrade = new SortingUpgradeItem(), id("sorting_upgrade"));

        creativeModeTab = items.registerCreativeModeTab(() -> new ItemStack(ModBlocks.sortingChests[SortingChestType.WOOD.ordinal()]),
                id(RefinedRelocation.MOD_ID));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, path);
    }
}
