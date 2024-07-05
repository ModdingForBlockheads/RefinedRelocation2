package net.blay09.mods.waystones;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.ISortingUpgradable;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RefinedRelocation.MOD_ID)
public class NeoForgeRefinedRelocation {

    private static final Logger logger = LoggerFactory.getLogger(NeoForgeRefinedRelocation.class);

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
