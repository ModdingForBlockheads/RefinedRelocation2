package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.fabric.client.RefinedRelocationClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = RefinedRelocation.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeRefinedRelocationClient {

    public NeoForgeRefinedRelocationClient(IEventBus modEventBus) {
        final var context = new NeoForgeLoadContext(modEventBus);
        BalmClient.initialize(RefinedRelocation.MOD_ID, context, RefinedRelocationClient::initialize);
    }
}
