package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.PlayerLoginEvent;
import net.blay09.mods.balm.api.event.server.ServerStartedEvent;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.block.ModBlocks;
import net.blay09.mods.refinedrelocation.config.RefinedRelocationConfig;
import net.blay09.mods.refinedrelocation.menu.ModMenus;
import net.blay09.mods.refinedrelocation.filter.*;
import net.blay09.mods.refinedrelocation.item.ModItems;
import net.blay09.mods.refinedrelocation.network.LoginSyncListMessage;
import net.blay09.mods.refinedrelocation.network.ModNetworking;
import net.blay09.mods.refinedrelocation.block.entity.ModBlockEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RefinedRelocation {

    public static final String MOD_ID = "refinedrelocation";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    public static void initialize() {
        RefinedRelocationAPI.__internal__setupAPI(new InternalMethodsImpl());

        RefinedRelocationConfig.initialize();

        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModItems.initialize(Balm.getItems());
        ModMenus.initialize(Balm.getMenus());
        ModNetworking.initialize(Balm.getNetworking());

        RefinedRelocationAPI.registerFilter(SameItemFilter.class);
        RefinedRelocationAPI.registerFilter(NameFilter.class);
        RefinedRelocationAPI.registerFilter(PresetFilter.class);
        RefinedRelocationAPI.registerFilter(CreativeTabFilter.class);
        RefinedRelocationAPI.registerFilter(ModFilter.class);
        RefinedRelocationAPI.registerFilter(SameModFilter.class);

        Balm.initializeIfLoaded("ironchest", "net.blay09.mods.refinedrelocation.compat.ironchest.IronChestAddon");

        Balm.getEvents().onEvent(ServerStartedEvent.class, event -> {
            ModFilter.gatherMods();
        });

        Balm.getEvents().onEvent(PlayerLoginEvent.class, event -> {
            Balm.getNetworking().sendTo(event.getPlayer(), new LoginSyncListMessage(LoginSyncListMessage.TYPE_MODS, ModFilter.modIds));
        });
    }

}
