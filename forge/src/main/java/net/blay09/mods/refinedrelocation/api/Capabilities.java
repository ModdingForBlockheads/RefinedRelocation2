package net.blay09.mods.refinedrelocation.api;

import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.SortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.SortingInventory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class Capabilities {

    public static Capability<RootFilter> ROOT_FILTER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Capability<IMultiRootFilter> MULTI_ROOT_FILTER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Capability<SimpleFilter> SIMPLE_FILTER = CapabilityManager.get(new CapabilityToken<>() {
    });

    /**
     * Note that instances of this capability are being cached without validity checks.
     */
    public static Capability<SortingGridMember> SORTING_GRID_MEMBER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Capability<SortingInventory> SORTING_INVENTORY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static Capability<ISortingUpgradable> SORTING_UPGRADABLE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isSortingGridCapability(Capability<?> capability) {
        return capability == SORTING_GRID_MEMBER || capability == SORTING_INVENTORY;
    }

    public static boolean isFilterCapability(Capability<?> capability) {
        return capability == ROOT_FILTER || capability == SIMPLE_FILTER;
    }

}
