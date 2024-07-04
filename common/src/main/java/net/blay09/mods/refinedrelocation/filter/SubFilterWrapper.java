package net.blay09.mods.refinedrelocation.filter;

import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

public class SubFilterWrapper {

	private final IFilter filter;
	private boolean isBlacklist;

	public SubFilterWrapper(IFilter filter) {
		this.filter = filter;
	}

	public boolean isBlacklist() {
		return isBlacklist;
	}

	public void setBlacklist(boolean blacklist) {
		isBlacklist = blacklist;
	}

	public IFilter getFilter() {
		return filter;
	}

	public CompoundTag writeNBT(CompoundTag compound) {
		compound.putString("Type", filter.getIdentifier());
		compound.put("Data", filter.serializeNBT());
		compound.putBoolean("IsBlacklist", isBlacklist);
		return compound;
	}

	@Nullable
	public static SubFilterWrapper loadFromNBT(CompoundTag tagCompound) {
		IFilter filter = FilterRegistry.createFilter(tagCompound.getString("Type"));
		if(filter != null) {
			filter.deserializeNBT(tagCompound.getCompound("Data"));
			SubFilterWrapper wrapper = new SubFilterWrapper(filter);
			wrapper.isBlacklist = tagCompound.getBoolean("IsBlacklist");
			return wrapper;
		}
		return null;
	}
}
