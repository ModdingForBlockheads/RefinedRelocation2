package net.blay09.mods.refinedrelocation.client;

import net.blay09.mods.balm.api.client.screen.BalmScreens;
import net.blay09.mods.refinedrelocation.client.gui.*;
import net.blay09.mods.refinedrelocation.menu.ModMenus;

public class ModScreens {
    public static void register(BalmScreens screens) {
        screens.registerScreen(ModMenus.fastHopper::get, FastHopperScreen::new);
        screens.registerScreen(ModMenus.sortingChest::get, SortingChestScreen::new);
        screens.registerScreen(ModMenus.nameFilter::get, NameFilterScreen::new);
        screens.registerScreen(ModMenus.checklistFilter::get, ChecklistFilterScreen::new);
        screens.registerScreen(ModMenus.rootFilter::get, RootFilterScreen::new);
        screens.registerScreen(ModMenus.addFilter::get, AddFilterScreen::new);
    }
}
