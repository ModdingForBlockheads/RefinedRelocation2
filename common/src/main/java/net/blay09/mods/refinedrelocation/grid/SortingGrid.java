package net.blay09.mods.refinedrelocation.grid;

import com.google.common.collect.Lists;
import net.blay09.mods.refinedrelocation.api.grid.ISortingGrid;
import net.blay09.mods.refinedrelocation.api.grid.SortingGridMember;

import java.util.Collection;
import java.util.List;

public class SortingGrid implements ISortingGrid {

	private final List<SortingGridMember> memberList = Lists.newArrayList();
	private boolean isSortingActive;

	public void mergeWith(ISortingGrid otherGrid) {
		if(otherGrid == this) {
			return;
		}
		for(SortingGridMember member : otherGrid.getMembers()) {
			addMember(member);
		}
	}

	@Override
	public void addMember(SortingGridMember member) {
		member.setSortingGrid(this);
		memberList.add(member);
	}

	@Override
	public void removeMember(SortingGridMember member) {
		member.setSortingGrid(null);
		memberList.remove(member);
	}

	@Override
	public boolean isSortingActive() {
		return isSortingActive;
	}

	@Override
	public void setSortingActive(boolean isSortingActive) {
		this.isSortingActive = isSortingActive;
	}

	@Override
	public Collection<SortingGridMember> getMembers() {
		return memberList;
	}
}
