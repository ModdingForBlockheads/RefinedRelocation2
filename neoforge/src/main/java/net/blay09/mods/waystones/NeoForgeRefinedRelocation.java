package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(RefinedRelocation.MOD_ID)
public class NeoForgeRefinedRelocation {

    public NeoForgeRefinedRelocation(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        Balm.initialize(RefinedRelocation.MOD_ID, context, RefinedRelocation::initialize);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // TODO event.register(IRootFilter.class);
        // TODO event.register(IMultiRootFilter.class);
        // TODO event.register(ISimpleFilter.class);
        // TODO event.register(ISortingGridMember.class);
        // TODO event.register(ISortingInventory.class);
        // TODO event.register(ISortingUpgradable.class);
    }
}
