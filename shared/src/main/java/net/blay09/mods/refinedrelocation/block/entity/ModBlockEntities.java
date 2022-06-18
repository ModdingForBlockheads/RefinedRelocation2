package net.blay09.mods.refinedrelocation.block.entity;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.block.ModBlocks;
import net.blay09.mods.refinedrelocation.block.SortingChestBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;

public class ModBlockEntities {

    public static List<DeferredObject<BlockEntityType<SortingChestBlockEntity>>> sortingChests;
    public static DeferredObject<BlockEntityType<BlockExtenderBlockEntity>> blockExtender;
    public static DeferredObject<BlockEntityType<FastHopperBlockEntity>> fastHopper;
    public static DeferredObject<BlockEntityType<FilteredHopperBlockEntity>> filteredHopper;
    public static DeferredObject<BlockEntityType<SortingConnectorBlockEntity>> sortingConnector;
    public static DeferredObject<BlockEntityType<SortingInterfaceBlockEntity>> sortingInterface;

    public static void initialize(BalmBlockEntities blockEntities) {
        sortingChests = new ArrayList<>();
        for (SortingChestBlock sortingChest : ModBlocks.sortingChests) {
            SortingChestType chestType = sortingChest.getChestType();
            DeferredObject<BlockEntityType<SortingChestBlockEntity>> deferredObject = blockEntities.registerBlockEntity(id(chestType.getRegistryName()), (pos, state) -> new SortingChestBlockEntity(sortingChest.getChestType(), pos, state), () -> new Block[]{sortingChest});
            sortingChests.add(deferredObject);
        }

        blockExtender = blockEntities.registerBlockEntity(id("block_extender"), BlockExtenderBlockEntity::new, () -> new Block[]{ModBlocks.blockExtender});
        fastHopper = blockEntities.registerBlockEntity(id("fast_hopper"), FastHopperBlockEntity::new, () -> new Block[]{ModBlocks.fastHopper});
        filteredHopper = blockEntities.registerBlockEntity(id("filtered_hopper"), FilteredHopperBlockEntity::new, () -> new Block[]{ModBlocks.filteredHopper});
        sortingConnector = blockEntities.registerBlockEntity(id("sorting_connector"), SortingConnectorBlockEntity::new, () -> new Block[]{ModBlocks.sortingConnector});
        sortingInterface = blockEntities.registerBlockEntity(id("sorting_interface"), SortingInterfaceBlockEntity::new, () -> new Block[]{ModBlocks.sortingInterface});
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(RefinedRelocation.MOD_ID, path);
    }

}
