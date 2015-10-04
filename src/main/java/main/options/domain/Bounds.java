package main.options.domain;

/**
 * An interval of values.
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the values.
 */
public class Bounds<T extends Comparable<T>> implements Domain<T> {

	private T min;
	private T max;

	public Bounds(T min, T max) {
		this.min = min;
		this.max = max;
	}

	/**
	 * Getter on the minimum (inclusive) bound of the domain.
	 *
	 * @return The minimum (inclusive) bound of the domain.
	 */
	public T getMin() {
		return min;
	}

	/**
	 * Getter on the maximum (inclusive) bound of the domain.
	 *
	 * @return The maximum (inclusive) bound of the domain.
	 */
	public T getMax() {
		return max;
	}

	@Override
	public boolean inDomain(T value) {
		return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
	}

	@Override
	public String toString() {
		return "[" + min + ", " + max + "]";
	}

}
