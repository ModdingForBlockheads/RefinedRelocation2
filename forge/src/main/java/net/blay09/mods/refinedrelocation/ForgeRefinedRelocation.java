package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.refinedrelocation.api.ISortingUpgradable;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
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
        event.register(IRootFilter.class);
        event.register(IMultiRootFilter.class);
        event.register(ISimpleFilter.class);
        event.register(ISortingGridMember.class);
        event.register(ISortingInventory.class);
        event.register(ISortingUpgradable.class);
    }
}
