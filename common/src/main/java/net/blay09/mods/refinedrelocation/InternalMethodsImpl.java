package net.blay09.mods.refinedrelocation;

import com.google.common.collect.Lists;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.blay09.mods.refinedrelocation.api.InternalMethods;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.filter.SimpleFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.SortingGridMember;
import net.blay09.mods.refinedrelocation.api.grid.SortingInventory;
import net.blay09.mods.refinedrelocation.filter.FilterRegistry;
import net.blay09.mods.refinedrelocation.grid.SortingGrid;
import net.blay09.mods.refinedrelocation.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Iterator;
import java.util.List;

public class InternalMethodsImpl implements InternalMethods {

    @Override
    public void registerFilter(Class<? extends IFilter> filterClass) {
        FilterRegistry.registerFilter(filterClass);
    }

    @Override
    public void addToSortingGrid(SortingGridMember member) {
        ISortingGrid sortingGrid = member.getSortingGrid();
        if (sortingGrid != null) {
            return;
        }

        Level level = member.getBlockEntity().getLevel();
        if (level == null) {
            return;
        }

        BlockPos pos = member.getBlockEntity().getBlockPos();
        for (Direction facing : Direction.values()) {
            BlockPos facingPos = pos.relative(facing);
            if (level.hasChunkAt(facingPos)) {
                BlockEntity blockEntity = level.getChunk(facingPos).getBlockEntity(facingPos);
                if (blockEntity != null) {
                    SortingGridMember otherMember = Balm.getProviders().getProvider(blockEntity, SortingGridMember.class);
                    if (otherMember != null && otherMember.getSortingGrid() != null) {
                        if (sortingGrid != null) {
                            ((SortingGrid) sortingGrid).mergeWith(otherMember.getSortingGrid());
                        } else {
                            sortingGrid = otherMember.getSortingGrid();
                        }
                    }
                }
            }
        }

        if (sortingGrid == null) {
            sortingGrid = new SortingGrid();
        }

        sortingGrid.addMember(member);
    }

    @Override
    public void removeFromSortingGrid(SortingGridMember member) {
        ISortingGrid sortingGrid = member.getSortingGrid();
        if (sortingGrid == null) {
            return;
        }
        sortingGrid.removeMember(member);
        // First, reset all sorting grid members
        for (SortingGridMember otherMember : sortingGrid.getMembers()) {
            otherMember.setSortingGrid(null);
        }
        // Then, re-add them to the grid
        for (SortingGridMember otherMember : sortingGrid.getMembers()) {
            RefinedRelocationAPI.addToSortingGrid(otherMember);
        }
    }

    @Override
    public void insertIntoSortingGrid(SortingInventory sortingInventory, int fromSlotIndex, ItemStack itemStack) {
        List<SortingInventory> passingList = Lists.newArrayList();
        Container container = sortingInventory.getContainer();
        if (container == null) {
            return;
        }

        ItemStack restStack = ContainerUtils.extractItem(container, fromSlotIndex, 64, true);
        if (restStack.isEmpty()) {
            return;
        }

        ISortingGrid sortingGrid = sortingInventory.getSortingGrid();
        if (sortingGrid != null) {
            for (SortingGridMember member : sortingGrid.getMembers()) {
                if (member instanceof SortingInventory memberInventory) {
                    SimpleFilter filter = memberInventory.getFilter();
                    boolean passes = filter.passes(memberInventory.getBlockEntity(), restStack, itemStack);
                    if (passes) {
                        passingList.add(memberInventory);
                    }
                }
            }
        }

        // No point trying if there's no matching inventories.
        if (!passingList.isEmpty()) {
            SortingInventory targetInventory = getBestTargetInventory(passingList, null);
            if (targetInventory != sortingInventory) {
                // Only move the item if it's not already in the correct inventory
                while (!restStack.isEmpty() && !passingList.isEmpty() && targetInventory != null) {
                    // Insert stack into passing inventories
                    int insertCount = restStack.getCount();
                    Container targetContainer = targetInventory.getContainer();
                    if (targetContainer != null) {
                        restStack = ContainerUtils.insertItemStacked(targetContainer, restStack, false);
                    }

                    int actuallyInserted = insertCount - restStack.getCount();
                    if (actuallyInserted > 0) {
                        ItemStack movedStack = ContainerUtils.extractItem(container, fromSlotIndex, actuallyInserted, false);
                        if (movedStack.getCount() != actuallyInserted) {
                            // This would mean we just duped an item. This should only be possible if someone implements IItemHandler incorrectly, so crash and make it more likely to be reported.
                            throw new RuntimeException("Refined Relocation ran into a major problem with the connected inventory " + sortingInventory + ". Please report this at https://github.com/blay09/RefinedRelocation2/issues.");
                        }
                    }

                    if (!restStack.isEmpty()) {
                        targetInventory = getBestTargetInventory(passingList, targetInventory);
                    }
                }
            }
        }
    }

    @Nullable
    private static SortingInventory getBestTargetInventory(List<SortingInventory> passingList, @Nullable SortingInventory lastInventory) {
        SortingInventory targetInventory = null;
        int highestPriority = Integer.MIN_VALUE;
        Iterator<SortingInventory> it = passingList.iterator();
        while (it.hasNext()) {
            SortingInventory sortingInventory = it.next();
            if (sortingInventory == lastInventory) {
                it.remove();
            } else if (sortingInventory.getPriority() > highestPriority) {
                targetInventory = sortingInventory;
                highestPriority = sortingInventory.getPriority();
            }
        }
        return targetInventory;
    }

    @Override
    public void sendContainerMessageToServer(String key, String value) {
        Balm.getNetworking().sendToServer(new StringMenuMessage(key, value));
    }

    @Override
    public void sendContainerMessageToServer(String key, CompoundTag value) {
        Balm.getNetworking().sendToServer(new NBTMenuMessage(key, value));
    }


    @Override
    public void sendContainerMessageToServer(String key, int value) {
        Balm.getNetworking().sendToServer(new IntMenuMessage(key, value));
    }

    @Override
    public void sendContainerMessageToServer(String key, int value, int secondaryValue) {
        Balm.getNetworking().sendToServer(new IndexedIntMenuMessage(key, value, secondaryValue));
    }

    @Override
    public void syncContainerValue(String key, String value, Iterable<ContainerListener> listeners) {
        syncContainerValue(new StringMenuMessage(key, value), listeners);
    }

    @Override
    public void syncContainerValue(String key, int value, Iterable<ContainerListener> listeners) {
        syncContainerValue(new IntMenuMessage(key, value), listeners);
    }

    @Override
    public void syncContainerValue(String key, byte[] value, Iterable<ContainerListener> listeners) {
        syncContainerValue(new ByteArrayMenuMessage(key, value), listeners);
    }

    @Override
    public void syncContainerValue(String key, CompoundTag value, Iterable<ContainerListener> listeners) {
        syncContainerValue(new NBTMenuMessage(key, value), listeners);
    }

    private void syncContainerValue(MenuMessage message, Iterable<ContainerListener> listeners) {
        for (ContainerListener listener : listeners) {
            if (listener instanceof ServerPlayer player) {
                Balm.getNetworking().sendTo(player, message);
            }
        }
    }

    @Override
    public void openRootFilterGui(Player player, BlockEntity blockEntity, int rootFilterIndex) {
        if (player.level().isClientSide) {
            Balm.getNetworking().sendToServer(new RequestFilterScreenMessage(blockEntity.getBlockPos(), rootFilterIndex));
        } else {
            RefinedRelocationUtils.getRootFilter(blockEntity, rootFilterIndex).ifPresent(rootFilter -> {
                final var filterConfig = rootFilter.getConfiguration(player, blockEntity, rootFilterIndex, 0);
                Balm.getNetworking().openGui(player, filterConfig);
            });
        }
    }

    @Override
    public void updateFilterPreview(Player player, BlockEntity blockEntity, SimpleFilter filter) {
        if (!player.level().isClientSide) {
            byte[] slotStates = new byte[FilterPreviewMessage.INVENTORY_SLOT_COUNT];
            for (int i = 0; i < slotStates.length; i++) {
                ItemStack itemStack = player.getInventory().getItem(i);
                if (!itemStack.isEmpty()) {
                    slotStates[i] = (byte) (filter.passes(blockEntity, itemStack, itemStack) ? FilterPreviewMessage.STATE_SUCCESS : FilterPreviewMessage.STATE_FAILURE);
                }
            }

            Balm.getNetworking().sendTo(player, new FilterPreviewMessage(slotStates));
        }
    }

    @Override
    public void returnToParentContainer() {
        Balm.getNetworking().sendToServer(new ReturnToParentScreenMessage());
    }

}
