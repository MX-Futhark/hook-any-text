package main.options;

import java.util.Set;

/**
 * Indicates that an class is a top-level non-abstract option class
 *
 * @author Maxime PIA
 */
public interface EncompassingOptions {

	/**
	 * Getter on the lower-level option members of the class.
	 *
	 * @return The lower-level option members of the class.
	 */
	Set<Options> getSubOptions();

}
