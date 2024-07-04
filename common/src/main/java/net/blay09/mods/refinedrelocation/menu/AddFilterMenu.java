package net.blay09.mods.refinedrelocation.menu;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.refinedrelocation.RefinedRelocationUtils;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.filter.FilterRegistry;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AddFilterMenu extends AbstractBaseMenu implements IRootFilterMenu {

    public static final String KEY_ADD_FILTER = "AddFilter";
    public static final String KEY_ROOT_FILTER = "RootFilter";

    private final BlockEntity tileEntity;
    private final Player player;
    private final IRootFilter rootFilter;
    private final int rootFilterIndex;

    public AddFilterMenu(int windowId, Inventory playerInventory, BlockEntity blockEntity, int rootFilterIndex) {
        super(ModMenus.addFilter.get(), windowId);
        this.tileEntity = blockEntity;

        player = playerInventory.player;

        addPlayerInventory(playerInventory, 8, 128);

        this.rootFilterIndex = rootFilterIndex;
        this.rootFilter = RefinedRelocationUtils.getRootFilter(blockEntity, rootFilterIndex).orElseGet(RootFilter::new);
    }

    @Override
    public void receivedMessageServer(IMenuMessage message) {
        if (KEY_ADD_FILTER.equals(message.getKey())) {
            String typeId = message.getStringValue();
            IFilter filter = FilterRegistry.createFilter(typeId);
            if (filter == null) {
                // Client tried to create a filter that doesn't exist. Bad client!
                return;
            }

            if (rootFilter.getFilterCount() >= 3) {
                // Client tried to create more than three filters. Bad client!
                return;
            }

            rootFilter.addFilter(filter);
            tileEntity.setChanged();
            syncFilterList();
            RefinedRelocationAPI.updateFilterPreview(player, tileEntity, rootFilter);
            MenuProvider filterConfig = filter.getConfiguration(player, tileEntity, rootFilterIndex, rootFilter.getFilterCount() - 1);
            if (filterConfig != null) {
                Balm.getNetworking().openGui(player, filterConfig);
            } else {
                RefinedRelocationAPI.openRootFilterGui(player, tileEntity, rootFilterIndex);
            }
        }
    }

    private void syncFilterList() {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.put(KEY_ROOT_FILTER, rootFilter.serializeNBT());
        RefinedRelocationAPI.syncContainerValue(KEY_ROOT_FILTER, tagCompound, containerListeners());
    }

    @Override
    public void receivedMessageClient(IMenuMessage message) {
        if (KEY_ROOT_FILTER.equals(message.getKey())) {
            rootFilter.deserializeNBT(message.getNBTValue().getCompound(KEY_ROOT_FILTER));
        }
    }

    public BlockEntity getTileEntity() {
        return tileEntity;
    }

    @Override
    public IRootFilter getRootFilter() {
        return rootFilter;
    }
}
