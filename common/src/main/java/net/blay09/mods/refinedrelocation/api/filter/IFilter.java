package net.blay09.mods.refinedrelocation.api.filter;

import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public interface IFilter extends ISimpleFilter, Comparable<IFilter> {
	String getIdentifier();
	String getLangKey();
	String getDescriptionLangKey();
	boolean isFilterUsable(BlockEntity blockEntity);
	@Nullable
	IDrawable getFilterIcon();

	default int getVisualOrder() {
		return 0;
	}

	@Override
	default int compareTo(IFilter other) {
		int o1 = getVisualOrder();
		int o2 = other.getVisualOrder();
		if(o1 == o2) {
			return getIdentifier().compareTo(other.getIdentifier());
		}
		return o2 - o1;
	}
}
