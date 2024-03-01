package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.balm.fabric.provider.FabricBalmProviders;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.block.entity.ModBlockEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricRefinedRelocation implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initialize(RefinedRelocation.MOD_ID, RefinedRelocation::initialize);

        registerProvider("sorting_grid_member", ISortingGridMember.class, ModBlockEntities.sortingChests.stream().map(DeferredObject::get).toArray(BlockEntityType[]::new));
        registerProvider("root_filter", IRootFilter.class, ModBlockEntities.sortingChests.stream().map(DeferredObject::get).toArray(BlockEntityType[]::new));
        registerProvider("multi_root_filter", IMultiRootFilter.class, ModBlockEntities.sortingChests.stream().map(DeferredObject::get).toArray(BlockEntityType[]::new));
    }

    private <T> void registerProvider(String name, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        var providers = ((FabricBalmProviders) Balm.getProviders());
        ResourceLocation identifier = new ResourceLocation(RefinedRelocation.MOD_ID, name);
        providers.registerProvider(identifier, clazz);
        registerLookup(identifier, clazz, blockEntities);
    }

    private <T> void registerLookup(ResourceLocation identifier, Class<T> clazz, BlockEntityType<?>... blockEntities) {
        var lookup = BlockApiLookup.get(identifier, clazz, Void.class);
        lookup.registerForBlockEntities((blockEntity, context) -> ((BalmBlockEntity) blockEntity).getProvider(clazz), blockEntities);
    }
}
