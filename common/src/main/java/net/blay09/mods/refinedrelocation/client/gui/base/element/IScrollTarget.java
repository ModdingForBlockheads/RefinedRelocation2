package net.blay09.mods.refinedrelocation.client.gui.base.element;

public interface IScrollTarget {
	int getVisibleRows();
	int getRowCount();
	int getCurrentOffset();
	void setCurrentOffset(int offset);
}
