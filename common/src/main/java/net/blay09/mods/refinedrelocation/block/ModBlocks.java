package net.blay09.mods.refinedrelocation.block;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static SortingChestBlock[] sortingChests;
    public static Block fastHopper;
    public static Block filteredHopper;
    public static Block sortingConnector;
    public static Block sortingInterface;

    public static void initialize(BalmBlocks blocks) {
        final var chestTypes = SortingChestType.values();
        sortingChests = new SortingChestBlock[chestTypes.length];
        for (final var type : chestTypes) {
            blocks.register(() -> sortingChests[type.ordinal()] = new SortingChestBlock(type, defaultProperties()), () -> itemBlock(sortingChests[type.ordinal()]), id(type.getRegistryName()));
        }

        blocks.register(() -> fastHopper = new FastHopperBlock(defaultProperties()), () -> itemBlock(fastHopper), id("fast_hopper"));
        blocks.register(() -> filteredHopper = new FilteredHopperBlock(defaultProperties()), () -> itemBlock(filteredHopper), id("filtered_hopper"));
        blocks.register(() -> sortingConnector = new SortingConnectorBlock(defaultProperties()), () -> itemBlock(sortingConnector), id("sorting_connector"));
        blocks.register(() -> sortingInterface = new SortingInterfaceBlock(defaultProperties()), () -> itemBlock(sortingInterface), id("sorting_interface"));
    }

    private static BlockItem itemBlock(Block block) {
        return new BlockItem(block, Balm.getItems().itemProperties());
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, path);
    }

    private static BlockBehaviour.Properties defaultProperties() {
        return Balm.getBlocks().blockProperties();
    }
}
