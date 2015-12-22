package hexcapture;

import main.options.ValueClass;
import main.options.annotations.CommandLineValue;

/**
 * Possible strategies used by the main lua script to capture the selection
 * in Cheat Engine.
 *
 * @author Maxime PIA
 */
public enum HexUpdateStrategies implements ValueClass {

	@CommandLineValue(
		value = "basic",
		description = "Converts the selection every time the delay between "
			+ "two peeks at it has ellapsed."
	)
	BASIC,

	@CommandLineValue(
		value = "stabilized",
		description = "Waits for the content of the selection to be stable "
			+ "enough, relative to the threshold of stabilization, to convert "
			+ "the selection. The whole history is used to compute the "
			+ "stabilization factor. The bigger the history, the longer the "
			+ "wait for converting the selection."
	)
	STABILIZED,

	@CommandLineValue(
		value = "recurring",
		description = "Does not convert the selection directly, but an array "
			+ "of bytes constructed from the most common bytes at every "
			+ "position of the elements in the history."
	)
	RECURRING,

	@CommandLineValue(
		value = "combined",
		description = "Combines the two above."
	)
	COMBINED;

	@Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
