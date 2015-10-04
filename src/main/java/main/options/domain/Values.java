package main.options.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * An enumerated set of values.
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the values.
 */
public class Values<T> implements Domain<T> {

	private T[] values;

	@SuppressWarnings("unchecked")
	public Values(T[] values) {
		Set<T> noDuplicates = new HashSet<>();
		for (T value : values){
			noDuplicates.add(value);
		}
		this.values = (T[]) noDuplicates.toArray();
	}

	/**
	 * Getter on the possible values.
	 *
	 * @return The possible values.
	 */
	public T[] getValues() {
		return values;
	}

	@Override
	public boolean inDomain(T value) {
		for (T acceptedValue : values) {
			if (value.equals(acceptedValue)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("{");
		int counter = 0;
		for (T acceptedValue : values) {
			res.append(acceptedValue);
			if (counter < values.length - 1) {
				res.append(", ");
			}
			++counter;
		}
		res.append("}");
		return res.toString();
	}

}
