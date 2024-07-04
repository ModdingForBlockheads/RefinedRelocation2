package net.blay09.mods.refinedrelocation;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.api.filter.IMultiRootFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.block.entity.IDroppableItemHandler;
import net.blay09.mods.refinedrelocation.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class RefinedRelocationUtils {

    public static Optional<IRootFilter> getRootFilter(BlockEntity tileEntity, int rootFilterIndex) {
        IMultiRootFilter multiRootFilter = Balm.getProviders().getProvider(tileEntity, IMultiRootFilter.class);
        if (multiRootFilter != null) {
            IRootFilter foundRootFilter = multiRootFilter.getRootFilter(rootFilterIndex);
            if (foundRootFilter != null) {
                return Optional.of(foundRootFilter);
            }
        }

        return rootFilterIndex == 0 ? Optional.of(Balm.getProviders().getProvider(tileEntity, IRootFilter.class)) : Optional.empty();
    }

    public static void dropItemHandler(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            if (blockEntity instanceof IDroppableItemHandler) {
                ((IDroppableItemHandler) blockEntity).getDroppedItemHandlers().forEach(itemHandler -> ItemUtils.dropItemHandlerItems(level, pos, itemHandler));
            } else {
                Container container = Balm.getProviders().getProvider(blockEntity, Container.class);
                if (container != null) {
                    ItemUtils.dropItemHandlerItems(level, pos, container);
                }
            }
        }
    }

    public static int getComparatorInputOverride(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            Container container = Balm.getProviders().getProvider(blockEntity, Container.class);
            if(container != null) {
                return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
            }
            return 0;
        }

        return 0;
    }
}
