package net.blay09.mods.refinedrelocation.filter;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.MutableClassToInstanceMap;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;

import org.jetbrains.annotations.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterRegistry {

	private static final Map<String, Class<? extends IFilter>> filterMap = Maps.newHashMap();
	private static final List<Class<? extends IFilter>> filterList = Lists.newArrayList();
	private static final ClassToInstanceMap<IFilter> defaultInstances = MutableClassToInstanceMap.create();

	public static void registerFilter(Class<? extends IFilter> filterClass) {
		try {
			IFilter filter = filterClass.getConstructor().newInstance();
			if(filterMap.containsKey(filter.getIdentifier())) {
				throw new IllegalArgumentException("Filter with id '" + filter.getIdentifier() + "' is already registered.");
			}
			filterMap.put(filter.getIdentifier(), filterClass);
			filterList.add(filterClass);
			defaultInstances.put(filterClass, filter);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Could not instantiate new filter of class '" + filterClass.getSimpleName() + "': is a public no-arg constructor available?", e);
		}
	}

	@Nullable
	public static IFilter createFilter(String id) {
		Class<? extends IFilter> filterClass = filterMap.get(id);
		if(filterClass == null) {
			return null;
		}
		try {
			return filterClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException("Could not instantiate new filter of id '" + id + "': is a public no-arg constructor available?", e);
		}
	}

	@Deprecated
	@Nullable
	public static IFilter getFilterInstance(String id) {
		Class<? extends IFilter> filterClass = filterMap.get(id);
		if(filterClass == null) {
			return null;
		}
		return defaultInstances.get(filterClass);
	}

	public static List<IFilter> getApplicableFilters(Predicate<IFilter> predicate) {
		return defaultInstances.values().stream().filter(predicate).sorted().collect(Collectors.toList());
	}

	public static int getFilterCount() {
		return filterMap.size();
	}

}
