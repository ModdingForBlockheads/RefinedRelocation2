package net.blay09.mods.refinedrelocation.config;

import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.Config;
import net.blay09.mods.refinedrelocation.RefinedRelocation;

@Config(RefinedRelocation.MOD_ID)
public class RefinedRelocationConfigData implements BalmConfigData {

	@Comment("The amount of slots each sorting interface should check for changes per tick. Setting this too high while having many interfaces on large inventories can cause performance issues.")
	public int sortingInterfaceSlotsPerTick = 9;

}
