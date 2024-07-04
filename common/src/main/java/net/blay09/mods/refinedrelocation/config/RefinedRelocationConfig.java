package net.blay09.mods.refinedrelocation.config;

import net.blay09.mods.balm.api.Balm;

public class RefinedRelocationConfig {

	public static void initialize() {
		Balm.getConfig().initializeBackingConfig(RefinedRelocationConfigData.class);
	}

	public static RefinedRelocationConfigData getActive() {
		return Balm.getConfig().getActive(RefinedRelocationConfigData.class);
	}

}
