package hextostring.replacement;

/**
 * Replacement strategy for HEX2HEX & STR2STR replacements.
 *
 * @author Maxime PIA
 */
public class SameTypeStrategy extends ReplacementStrategy {

	@Override
	public String replacePattern(String s, String pattern, String replacement) {
		return s.replaceAll(pattern, replacement);
	}

	@Override
	public String replaceSequence(String s, String sequence,
		String replacement) {

		return s.replace(sequence, replacement);
	}

}
