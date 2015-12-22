package hextostring.replacement;

/**
 * Strategy pattern to apply string replacements.
 *
 * @author Maxime PIA
 */
public abstract class ReplacementStrategy {

	/**
	 * Replaces a pattern in a string.
	 *
	 * @param s
	 * 			The string to which replacements are applied.
	 * @param pattern
	 * 			The pattern.
	 * @param replacement
	 * 			The replacement.
	 * @return The string to which replacements were applied.
	 */
	public abstract String replacePattern(String s, String pattern,
		String replacement);

	/**
	 * Replaces a sequence in a string.
	 *
	 * @param s
	 * 			The string to which replacements are applied.
	 * @param sequence
	 * 			The sequence.
	 * @param replacement
	 * 			The replacement.
	 * @return The string to which replacements were applied.
	 */
	public abstract String replaceSequence(String s, String sequence,
		String replacement);

}
