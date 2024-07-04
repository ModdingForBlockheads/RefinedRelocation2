package net.blay09.mods.refinedrelocation.api;

public final class Priority {

	public static final int LOWEST = -999;
	public static final int LOW = -500;
	public static final int NORMAL = 0;
	public static final int HIGH = 500;
	public static final int HIGHEST = 999;

	public enum Enum {
		LOWEST(Priority.LOWEST, "gui.refinedrelocation:root_filter.priority_lowest", "--"),
		LOW(Priority.LOW, "gui.refinedrelocation:root_filter.priority_low", "-"),
		NORMAL(Priority.NORMAL, "gui.refinedrelocation:root_filter.priority_normal", "0"),
		HIGH(Priority.HIGH, "gui.refinedrelocation:root_filter.priority_high", "+"),
		HIGHEST(Priority.HIGHEST, "gui.refinedrelocation:root_filter.priority_highest", "++");

		private final int priority;
		private final String langKey;
		private final String symbol;

		Enum(int priority, String langKey, String symbol) {
			this.priority = priority;
			this.langKey = langKey;
			this.symbol = symbol;
		}

		public int getPriority() {
			return priority;
		}

		public String getLangKey() {
			return langKey;
		}

		public String getSymbol() {
			return symbol;
		}
	}
}
