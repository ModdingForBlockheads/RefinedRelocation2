package net.blay09.mods.refinedrelocation.api.filter;

import org.jetbrains.annotations.Nullable;

public interface IRootFilter extends ISimpleFilter {
    int getFilterCount();

    @Nullable
    IFilter getFilter(int index);

    void addFilter(IFilter filter);

    void removeFilter(int index);

    boolean isBlacklist(int index);

    void setIsBlacklist(int index, boolean isBlacklist);
}
