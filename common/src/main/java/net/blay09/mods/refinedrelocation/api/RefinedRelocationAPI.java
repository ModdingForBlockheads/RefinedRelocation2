package net.blay09.mods.refinedrelocation.api;

import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RefinedRelocationAPI {

    private static InternalMethods internalMethods;

    public static void __internal__setupAPI(InternalMethods internalMethods) {
        RefinedRelocationAPI.internalMethods = internalMethods;
    }

    public static void addToSortingGrid(ISortingGridMember member) {
        internalMethods.addToSortingGrid(member);
    }

    public static void removeFromSortingGrid(ISortingGridMember member) {
        internalMethods.removeFromSortingGrid(member);
    }

    public static void registerFilter(Class<? extends IFilter> filterClass) {
        internalMethods.registerFilter(filterClass);
    }

    public static void insertIntoSortingGrid(ISortingInventory sortingInventory, int fromSlotIndex, ItemStack itemStack) {
        internalMethods.insertIntoSortingGrid(sortingInventory, fromSlotIndex, itemStack);
    }

    public static void sendContainerMessageToServer(String key, String value) {
        internalMethods.sendContainerMessageToServer(key, value);
    }

    public static void sendContainerMessageToServer(String key, CompoundTag value) {
        internalMethods.sendContainerMessageToServer(key, value);
    }

    public static void sendContainerMessageToServer(String key, int value) {
        internalMethods.sendContainerMessageToServer(key, value);
    }

    public static void sendContainerMessageToServer(String key, int value, int value2) {
        internalMethods.sendContainerMessageToServer(key, value, value2);
    }

    public static void syncContainerValue(String key, String value, Iterable<ContainerListener> listeners) {
        internalMethods.syncContainerValue(key, value, listeners);
    }

    public static void syncContainerValue(String key, int value, Iterable<ContainerListener> listeners) {
        internalMethods.syncContainerValue(key, value, listeners);
    }

    public static void syncContainerValue(String key, byte[] value, Iterable<ContainerListener> listeners) {
        internalMethods.syncContainerValue(key, value, listeners);
    }

    public static void syncContainerValue(String key, CompoundTag value, Iterable<ContainerListener> listeners) {
        internalMethods.syncContainerValue(key, value, listeners);
    }

    public static void updateFilterPreview(Player player, BlockEntity blockEntity, ISimpleFilter filter) {
        internalMethods.updateFilterPreview(player, blockEntity, filter);
    }

    public static void openRootFilterGui(Player player, BlockEntity blockEntity, int rootFilterIndex) {
        internalMethods.openRootFilterGui(player, blockEntity, rootFilterIndex);
    }

    public static void returnToParentContainer() {
        internalMethods.returnToParentContainer();
    }
}
