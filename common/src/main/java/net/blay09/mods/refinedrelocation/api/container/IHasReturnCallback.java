package net.blay09.mods.refinedrelocation.api.container;

import org.jetbrains.annotations.Nullable;

public interface IHasReturnCallback {
	void setReturnCallback(@Nullable ReturnCallback callback);
	@Nullable ReturnCallback getReturnCallback();
}
