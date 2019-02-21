package net.blay09.mods.refinedrelocation.capability;

import net.blay09.mods.refinedrelocation.api.filter.ISimpleFilter;
import net.blay09.mods.refinedrelocation.filter.RootFilter;
import net.minecraft.nbt.INBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilitySimpleFilter {

	@CapabilityInject(ISimpleFilter.class)
	public static Capability<ISimpleFilter> CAPABILITY;

	public static void register() {
		CapabilityManager.INSTANCE.register(ISimpleFilter.class, new Capability.IStorage<ISimpleFilter>() {
			@Override
			public INBTBase writeNBT(Capability<ISimpleFilter> capability, ISimpleFilter instance, EnumFacing side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<ISimpleFilter> capability, ISimpleFilter instance, EnumFacing side, INBTBase nbt) {
				instance.deserializeNBT(nbt);
			}
		}, RootFilter::new);
	}

}
