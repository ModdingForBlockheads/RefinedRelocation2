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

public interface InternalMethods {

    void registerFilter(Class<? extends IFilter> filterClass);

    void addToSortingGrid(ISortingGridMember member);

    void removeFromSortingGrid(ISortingGridMember member);

    void insertIntoSortingGrid(ISortingInventory sortingInventory, int fromSlotIndex, ItemStack itemStack);

    void sendContainerMessageToServer(String key, String value);

    void sendContainerMessageToServer(String key, CompoundTag value);

    void sendContainerMessageToServer(String key, int value);

    void sendContainerMessageToServer(String key, int value, int secondaryValue);

    void syncContainerValue(String key, String value, Iterable<ContainerListener> listeners);

    void syncContainerValue(String key, int value, Iterable<ContainerListener> listeners);

    void syncContainerValue(String key, byte[] value, Iterable<ContainerListener> listeners);

    void syncContainerValue(String key, CompoundTag value, Iterable<ContainerListener> listeners);

    void openRootFilterGui(Player player, BlockEntity blockEntity, int rootFilterIndex);

    void updateFilterPreview(Player player, BlockEntity blockEntity, ISimpleFilter filter);

    void returnToParentContainer();
}
