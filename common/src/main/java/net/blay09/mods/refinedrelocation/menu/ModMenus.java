package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.filter.NameFilter;
import net.blay09.mods.refinedrelocation.block.entity.FastHopperBlockEntity;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ModMenus {
    public static DeferredObject<MenuType<FastHopperMenu>> fastHopper;
    public static DeferredObject<MenuType<SortingChestMenu>> sortingChest;
    public static DeferredObject<MenuType<AddFilterMenu>> addFilter;
    public static DeferredObject<MenuType<RootFilterMenu>> rootFilter;
    public static DeferredObject<MenuType<NameFilterMenu>> nameFilter;
    public static DeferredObject<MenuType<ChecklistFilterMenu>> checklistFilter;

    public static void initialize(BalmMenus menus) {
        addFilter = menus.registerMenu(id("add_filter"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int rootFilterIndex = 0;
            if (data.readableBytes() > 0) {
                rootFilterIndex = data.readByte();
            }

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity != null) {
                return new AddFilterMenu(windowId, inv, blockEntity, rootFilterIndex);
            }

            throw new RuntimeException("Could not open container screen");
        });

        sortingChest = menus.registerMenu(id("sorting_chest"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof SortingChestBlockEntity sortingChest) {
                return new SortingChestMenu(windowId, inv, sortingChest);
            }

            throw new RuntimeException("Could not open container screen");
        });

        fastHopper = menus.registerMenu(id("fast_hopper"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity instanceof FastHopperBlockEntity fastHopper) {
                return new FastHopperMenu(windowId, inv, fastHopper);
            }

            throw new RuntimeException("Could not open container screen");
        });

        rootFilter = menus.registerMenu(id("root_filter"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int rootFilterIndex = 0;
            if (data.readableBytes() > 0) {
                rootFilterIndex = data.readByte();
            }

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity != null) {
                return new RootFilterMenu(windowId, inv, blockEntity, rootFilterIndex);
            }

            throw new RuntimeException("Could not open container screen");
        });

        nameFilter = menus.registerMenu(id("name_filter"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int rootFilterIndex = data.readByte();
            int filterIndex = data.readByte();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity != null) {
                if (inv.player.containerMenu instanceof IRootFilterMenu rootFilterMenu) {
                    IFilter filter = rootFilterMenu.getRootFilter().getFilter(filterIndex);
                    if (filter != null) {
                        return new NameFilterMenu(windowId, inv, blockEntity, rootFilterIndex, (NameFilter) filter);
                    }
                }
            }

            throw new RuntimeException("Could not open container screen");
        });

        checklistFilter = menus.registerMenu(id("checklist_filter"), (windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            int rootFilterIndex = data.readByte();
            int filterIndex = data.readByte();

            BlockEntity blockEntity = inv.player.level().getBlockEntity(pos);
            if (blockEntity != null) {
                if (inv.player.containerMenu instanceof IRootFilterMenu rootFilterMenu) {
                    IFilter filter = rootFilterMenu.getRootFilter().getFilter(filterIndex);
                    if (filter != null) {
                        return new ChecklistFilterMenu(windowId, inv, blockEntity, rootFilterIndex, (IChecklistFilter) filter);
                    }
                }
            }

            throw new RuntimeException("Could not open container screen");
        });
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, path);
    }
}
