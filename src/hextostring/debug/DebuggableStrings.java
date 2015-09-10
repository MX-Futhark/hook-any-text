package hextostring.debug;

/**
 * Wraps a hexadecimal input and the results of its conversion into an object
 * containing all the necessary information to print debugging messages.
 *
 * @author Maxime PIA
 */
public interface DebuggableStrings {

	/**
	 * Formats the lines depending on the debugging flags.
	 *
	 * @param debuggingFlags
	 * 			The debugging flags used to format these lines.
	 * @param converterStrictness
	 * 			The validity value below which a converted string is eliminated.
	 * @return A string representing these lines, with or without debug traces.
	 */
	String toString(long debuggingFlags, int converterStrictness);

	/**
	 * Provides a list of lines successfully decoded and formatted.
	 *
	 * @return A list of lines successfully decoded and formatted.
	 */
	DebuggableLineList getValidLineList();

}
