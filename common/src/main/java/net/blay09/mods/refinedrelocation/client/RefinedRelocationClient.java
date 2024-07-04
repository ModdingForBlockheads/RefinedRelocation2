package net.blay09.mods.refinedrelocation.client;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.client.BlockHighlightDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.refinedrelocation.api.client.RefinedRelocationClientAPI;

public class RefinedRelocationClient {

    public static void initialize() {
        RefinedRelocationClientAPI.__internal__setupAPI(new InternalClientMethodsImpl());

        ModScreens.register(BalmClient.getScreens());
        ModRenderers.initialize(BalmClient.getRenderers());

        Balm.getEvents().onEvent(BlockHighlightDrawEvent.class, BlockHighlightHandler::onBlockHighlight);
        Balm.getEvents().onEvent(ScreenDrawEvent.Post.class, FilterPreviewHandler::onDrawScreen);
    }
}
