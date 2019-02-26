package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.container.IContainerReturnable;
import net.blay09.mods.refinedrelocation.api.filter.IChecklistFilter;
import net.blay09.mods.refinedrelocation.api.filter.IConfigurableFilter;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.capability.CapabilityRootFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiBlockExtender;
import net.blay09.mods.refinedrelocation.client.gui.GuiChecklistFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiFastHopper;
import net.blay09.mods.refinedrelocation.client.gui.GuiRootFilter;
import net.blay09.mods.refinedrelocation.client.gui.GuiSortingChest;
import net.blay09.mods.refinedrelocation.container.ContainerBlockExtender;
import net.blay09.mods.refinedrelocation.container.ContainerChecklistFilter;
import net.blay09.mods.refinedrelocation.container.ContainerFastHopper;
import net.blay09.mods.refinedrelocation.container.ContainerRootFilter;
import net.blay09.mods.refinedrelocation.container.ContainerSortingChest;
import net.blay09.mods.refinedrelocation.tile.TileBlockExtender;
import net.blay09.mods.refinedrelocation.tile.TileFastHopper;
import net.blay09.mods.refinedrelocation.tile.TileSortingChest;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class GuiHandler {

	public static final int GUI_ROOT_FILTER = 2;
	public static final int GUI_ANY_FILTER = 3;
	public static final int GUI_BLOCK_EXTENDER_ROOT_FILTER = 6;

	@Nullable
	public static Container getContainer(int id, EntityPlayer player, MessageOpenGui message) {
		TileEntity tileEntity = message.hasPosition() ? player.world.getTileEntity(message.getPos()) : null;
		switch(id) {
			case GUI_ROOT_FILTER:
				return tileEntity != null ? (tileEntity.hasCapability(CapabilityRootFilter.CAPABILITY, null) ?
						new ContainerRootFilter(player, tileEntity).setReturnCallback(() -> RefinedRelocation.proxy.openGui(player, message)) : null) : null;
			case GUI_ANY_FILTER:
				if(tileEntity != null) {
					Container container = player.openContainer;
					if (container instanceof ContainerRootFilter) {
						IFilter filter = ((ContainerRootFilter) container).getRootFilter().getFilter(message.getIntValue());
						Container filterContainer = createFilterContainer(player, tileEntity, filter);
						if(filterContainer instanceof IContainerReturnable) {
							((IContainerReturnable) filterContainer).setReturnCallback(((ContainerRootFilter) container).getReturnCallback());
						}
						return filterContainer;
					}
				}
				break;
			case GUI_BLOCK_EXTENDER_ROOT_FILTER:
				return tileEntity instanceof TileBlockExtender ?
						new ContainerRootFilter(player, tileEntity,
								message.getIntValue() == 1 ? ((TileBlockExtender) tileEntity).getOutputFilter() : ((TileBlockExtender) tileEntity).getInputFilter())
								.setReturnCallback(() -> RefinedRelocation.proxy.openGui(player, message)) : null;
		}
		return null;
	}

	@Nullable
	private static Container createFilterContainer(EntityPlayer player, TileEntity tileEntity, @Nullable IFilter filter) {
		if(filter instanceof IConfigurableFilter) {
			return ((IConfigurableFilter) filter).createContainer(player, tileEntity);
		} else if(filter instanceof IChecklistFilter) {
			return new ContainerChecklistFilter(player, tileEntity, (IChecklistFilter) filter);
		}
		return null;
	}

}
