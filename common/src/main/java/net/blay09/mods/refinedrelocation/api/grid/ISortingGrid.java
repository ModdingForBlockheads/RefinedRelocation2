package net.blay09.mods.refinedrelocation.api.grid;

import java.util.Collection;

public interface ISortingGrid {
	Collection<ISortingGridMember> getMembers();
	void addMember(ISortingGridMember member);
	void removeMember(ISortingGridMember member);

	boolean isSortingActive();
	void setSortingActive(boolean isSortingActive);
}
