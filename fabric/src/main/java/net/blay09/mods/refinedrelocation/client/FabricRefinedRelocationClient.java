package net.blay09.mods.refinedrelocation.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.fabricmc.api.ClientModInitializer;

public class FabricRefinedRelocationClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(RefinedRelocation.MOD_ID, RefinedRelocationClient::initialize);
    }
}
