package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.filter.ChecklistFilter;
import net.blay09.mods.refinedrelocation.filter.NameFilter;
import net.blay09.mods.refinedrelocation.block.entity.FastHopperBlockEntity;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

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
                if (blockEntity instanceof SortingChestBlockEntity sortingChestBlockEntity) {
                    return new SortingChestMenu(windowId, inventory, sortingChestBlockEntity);
                } else {
                    throw new IllegalStateException("Block entity is not a SortingChestBlockEntity");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getStreamCodec() {
                return BlockPos.STREAM_CODEC.cast();
            }
        });

        fastHopper = menus.registerMenu(id("fast_hopper"), new BalmMenuFactory<FastHopperMenu, BlockPos>() {
            @Override
            public FastHopperMenu create(int windowId, Inventory inventory, BlockPos pos) {
                final var blockEntity = inventory.player.level().getBlockEntity(pos);
                if (blockEntity instanceof FastHopperBlockEntity fastHopperBlockEntity) {
                    return new FastHopperMenu(windowId, inventory, fastHopperBlockEntity);
                } else {
                    throw new IllegalStateException("Block entity is not a FastHopperBlockEntity");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, BlockPos> getStreamCodec() {
                return BlockPos.STREAM_CODEC.cast();
            }
        });

        rootFilter = menus.registerMenu(id("root_filter"), new BalmMenuFactory<RootFilterMenu, RootFilterMenu.Data>() {
            @Override
            public RootFilterMenu create(int windowId, Inventory inventory, RootFilterMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                return new RootFilterMenu(windowId, inventory, blockEntity, data.rootFilterIndex());
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, RootFilterMenu.Data> getStreamCodec() {
                return RootFilterMenu.Data.STREAM_CODEC.cast();
            }
        });

        nameFilter = menus.registerMenu(id("name_filter"), new BalmMenuFactory<NameFilterMenu, NameFilterMenu.Data>() {
            @Override
            public NameFilterMenu create(int windowId, Inventory inventory, NameFilterMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                if (inventory.player.containerMenu instanceof IRootFilterMenu rootFilterMenu) {
                    final var filter = rootFilterMenu.getRootFilter().getFilter(data.filterIndex());
                    if (filter instanceof NameFilter nameFilterInstance) {
                        return new NameFilterMenu(windowId, inventory, blockEntity, data.rootFilterIndex(), nameFilterInstance);
                    } else {
                        throw new IllegalStateException("Filter is not a NameFilter");
                    }
                } else {
                    throw new IllegalStateException("Current container menu is not an IRootFilterMenu");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, NameFilterMenu.Data> getStreamCodec() {
                return NameFilterMenu.Data.STREAM_CODEC.cast();
            }
        });

        checklistFilter = menus.registerMenu(id("checklist_filter"), new BalmMenuFactory<ChecklistFilterMenu, ChecklistFilterMenu.Data>() {
            @Override
            public ChecklistFilterMenu create(int windowId, Inventory inventory, ChecklistFilterMenu.Data data) {
                final var blockEntity = inventory.player.level().getBlockEntity(data.pos());
                if (inventory.player.containerMenu instanceof IRootFilterMenu rootFilterMenu) {
                    final var filter = rootFilterMenu.getRootFilter().getFilter(data.filterIndex());
                    if (filter instanceof ChecklistFilter checklistFilterInstance) {
                        return new ChecklistFilterMenu(windowId, inventory, blockEntity, data.rootFilterIndex(), checklistFilterInstance);
                    } else {
                        throw new IllegalStateException("Filter is not a NameFilter");
                    }
                } else {
                    throw new IllegalStateException("Current container menu is not an IRootFilterMenu");
                }
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, ChecklistFilterMenu.Data> getStreamCodec() {
                return ChecklistFilterMenu.Data.STREAM_CODEC.cast();
            }
        });
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, path);
    }
}
