package org.mx;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionUtils {
	private CollectionUtils() {
		super();
	}

	public static <T> Set<T> add(Set<T> set, T value) {
		if (set == null) {
			set = new HashSet<T>();
		}
		if (value != null && !contain(set, value)) {
			set.add(value);
		}
		return set;
	}

	public static <T> boolean remove(List<T> list, T value) {
		if (list == null || value == null) {
			return false;
		}
		for (T t : list) {
			if (t.hashCode() == value.hashCode()) {
				list.remove(t);
				return true;
			}
		}
		return false;
	}

	public static <T> boolean remove(Set<T> set, T value) {
		if (set == null || value == null) {
			return false;
		}
		for (T t : set) {
			if (t.hashCode() == value.hashCode()) {
				set.remove(t);
				return true;
			}
		}
		return false;
	}

	public static <T> boolean contain(List<T> list, T value) {
		if (list == null || value == null) {
			return false;
		}
		for (T t : list) {
			if (t.hashCode() == value.hashCode()) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean contain(Set<T> set, T value) {
		if (set == null || value == null) {
			return false;
		}
		for (T t : set) {
			if (t.hashCode() == value.hashCode()) {
				return true;
			}
		}
		return false;
	}
}
