package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.filter.NameFilter;
import net.blay09.mods.refinedrelocation.block.entity.FastHopperBlockEntity;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
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
        addFilter = menus.registerMenu(id("add_filter"), new BalmMenuFactory<AddFilterMenu, AddFilterMenu.Data>() {
            @Override
            public AddFilterMenu create(int windowId, Inventory inventory, AddFilterMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                return new AddFilterMenu(windowId, inventory, blockEntity, data.rootFilterIndex());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, AddFilterMenu.Data> getStreamCodec() {
                return AddFilterMenu.Data.STREAM_CODEC.cast();
            }
        });

        sortingChest = menus.registerMenu(id("sorting_chest"), new BalmMenuFactory<SortingChestMenu, BlockPos>() {
            @Override
            public SortingChestMenu create(int windowId, Inventory inventory, BlockPos pos) {
                final var blockEntity = inventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof SortingChestBlockEntity sortingChest) {
                    return new SortingChestMenu(windowId, inventory, sortingChest);
                } else {
                    throw new IllegalStateException("Block entity is not a SortingChestBlockEntity");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getStreamCodec() {
                return BlockPos.STREAM_CODEC.cast();
            }
        });

        fastHopper = menus.registerMenu(id("fast_hopper"),  new BalmMenuFactory<FastHopperMenu, BlockPos>() {
            @Override
            public FastHopperMenu create(int windowId, Inventory inventory, BlockPos pos) {
                final var blockEntity = inventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof FastHopperBlockEntity fastHopper) {
                    return new FastHopperMenu(windowId, inventory, fastHopper);
                } else {
                    throw new IllegalStateException("Block entity is not a FastHopperBlockEntity");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getStreamCodec() {
                return BlockPos.STREAM_CODEC.cast();
            }
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
