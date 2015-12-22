package main.options.domain;

/**
 * A set of values.
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the values.
 */
public interface Domain<T> {

	/**
	 * Determines whether a value is in the domain or not.
	 *
	 * @param value
	 * 			The value to check.
	 * @return True if the value is in the domain.
	 */
	boolean inDomain(T value);

}
