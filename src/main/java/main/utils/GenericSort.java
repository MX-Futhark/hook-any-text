package main.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Utility methods for sorting collections.
 *
 * @author Maxime PIA
 */
public class GenericSort {

	/**
	 * Alphabetically sorts a generic collection.
	 * Warning: duplicates are deleted.
	 *
	 * @param c
	 * 			The collection to sort
	 * @param getString
	 * 			The method used to sort the collection.
	 * @return The sorted collection.
	 */
	public static <T> Collection<T> apply(Collection<T> c, Method getString) {
		TreeMap<String, T> sorter = new TreeMap<>();
		for (T elt : c) {
			if (getString == null) {
				sorter.put(elt.toString(), elt);
			} else {
				try {
					sorter.put((String) getString.invoke(elt), elt);
				} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {

					sorter.put(elt.toString(), elt);
				}
			}
		}
		return sorter.values();
	}

}
