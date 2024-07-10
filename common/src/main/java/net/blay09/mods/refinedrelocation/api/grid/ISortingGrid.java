package net.blay09.mods.refinedrelocation.api.grid;

import java.util.Collection;

public interface ISortingGrid {
	Collection<SortingGridMember> getMembers();
	void addMember(SortingGridMember member);
	void removeMember(SortingGridMember member);

	boolean isSortingActive();
	void setSortingActive(boolean isSortingActive);
}
