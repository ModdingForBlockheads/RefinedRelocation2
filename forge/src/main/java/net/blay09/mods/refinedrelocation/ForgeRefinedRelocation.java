package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.refinedrelocation.api.ISortingUpgradable;
import net.blay09.mods.refinedrelocation.api.filter.MultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.SortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.SortingInventory;
import net.blay09.mods.refinedrelocation.fabric.client.RefinedRelocationClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefinedRelocation.MOD_ID)
public class ForgeRefinedRelocation {

    public ForgeRefinedRelocation() {
        Balm.initialize(RefinedRelocation.MOD_ID, EmptyLoadContext.INSTANCE, RefinedRelocation::initialize);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> BalmClient.initialize(RefinedRelocation.MOD_ID, EmptyLoadContext.INSTANCE, RefinedRelocationClient::initialize));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeRefinedRelocation::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(RootFilter.class);
        event.register(MultiRootFilter.class);
        event.register(SimpleFilter.class);
        event.register(SortingGridMember.class);
        event.register(SortingInventory.class);
        event.register(ISortingUpgradable.class);
    }
}
