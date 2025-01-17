package net.blay09.mods.refinedrelocation.api.filter;

public interface ChecklistFilter extends IFilter {
	String getOptionLangKey(int option);
	void setOptionChecked(int option, boolean checked);
	boolean isOptionChecked(int option);
	int getOptionCount();
}
