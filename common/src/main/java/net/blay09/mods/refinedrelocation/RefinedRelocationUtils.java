package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.RootFilter;
import net.blay09.mods.refinedrelocation.block.entity.IDroppableContainer;
import net.blay09.mods.refinedrelocation.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class RefinedRelocationUtils {

    public static Optional<RootFilter> getRootFilter(BlockEntity tileEntity, int rootFilterIndex) {
        final var multiRootFilter = Balm.getProviders().getProvider(tileEntity, IMultiRootFilter.class);
        if (multiRootFilter != null) {
            final var foundRootFilter = multiRootFilter.getRootFilter(rootFilterIndex);
            if (foundRootFilter != null) {
                return Optional.of(foundRootFilter);
            }
        }

        return rootFilterIndex == 0 ? Optional.of(Balm.getProviders().getProvider(tileEntity, RootFilter.class)) : Optional.empty();
    }

    public static void dropItemHandler(Level level, BlockPos pos) {
        final var blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            if (blockEntity instanceof IDroppableContainer droppableContainer) {
                droppableContainer.getDroppedContainers().forEach(container -> ItemUtils.dropContainerItems(level, pos, container));
            } else {
                final var container = Balm.getProviders().getProvider(blockEntity, Container.class);
                if (container != null) {
                    ItemUtils.dropContainerItems(level, pos, container);
                }
            }
        }
    }

    public static int getComparatorInputOverride(BlockState state, Level level, BlockPos pos) {
        final var blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            final var container = Balm.getProviders().getProvider(blockEntity, Container.class);
            if (container != null) {
                return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
            }
            return 0;
        }

        return 0;
    }
}
