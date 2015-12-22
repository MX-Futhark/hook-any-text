package hextostring.replacement;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a replacement in a string.
 *
 * @author Maxime PIA
 */
public class Replacement implements Serializable {

	/**
	 * Backward-compatible with 0.7.0
	 */
	private static final long serialVersionUID = 00000000007000000L;

	private String sequence;
	private String replacement;
	private String escapedSequence;
	private String escapedReplacement;

	private boolean escapeCharacters;
	private boolean interpretAsPattern;

	private ReplacementType type;

	public Replacement(ReplacementType type) {
		this("", "", true, false, type);
	}

	public Replacement(String sequence, String replacement,
		boolean escapeCharacters, boolean interpretAsPattern,
		ReplacementType type) {

		this.sequence = sequence;
		this.replacement = replacement;
		this.escapedSequence = escape(sequence);
		this.escapedReplacement = escape(replacement);
		this.escapeCharacters = escapeCharacters;
		this.interpretAsPattern = interpretAsPattern;
		this.type = type;
	}

	/**
	 * Getter on the raw sequence/pattern to be replaced.
	 *
	 * @return The raw sequence/pattern to be replaced.
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Getter on the sequence/pattern to be replaced, escaped if necessary.
	 *
	 * @return The sequence/pattern to be replaced, escaped if necessary.
	 */
	public String getProcessedSequence() {
		return escapeCharacters ? escapedSequence : sequence;
	}

	/**
	 * Setter on the sequence/pattern to be replaced.
	 *
	 * @param sequence
	 * 			The sequence/pattern to be replaced.
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
		this.escapedSequence = escape(sequence);
	}

	/**
	 * Getter on the raw replacement.
	 *
	 * @return The raw replacement.
	 */
	public String getReplacement() {
		return replacement;
	}

	/**
	 * Getter on the raw replacement, escaped if necessary.
	 *
	 * @return The raw replacement, escaped if necessary.
	 */
	public String getProcessedReplacement() {
		return escapeCharacters ? escapedReplacement : replacement;
	}

	/**
	 * Setter on the replacement.
	 *
	 * @param replacement
	 * 			The replacement.
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
		this.escapedReplacement = escape(replacement);
	}

	/**
	 * Getter on whether or not antislashes are interpreted as escaping
	 * characters.
	 *
	 * @return True if antislashes are interpreted as escaping characters.
	 */
	public boolean isEscapeCharacters() {
		return escapeCharacters;
	}

	/**
	 * Setter on whether or not antislashes are interpreted as escaping
	 * characters.
	 *
	 * @param escapeCharacters
	 * 			True if antislashes are interpreted as escaping characters.
	 */
	public void setEscapeCharacters(boolean escapeCharacters) {
		this.escapeCharacters = escapeCharacters;
	}

	/**
	 * Getter on whether or not the sequence represents a regex.
	 *
	 * @return True if the sequence represents a regex.
	 */
	public boolean isInterpretAsPattern() {
		return interpretAsPattern;
	}

	/**
	 * Setter on whether or not the sequence represents a regex.
	 *
	 * @param interpretAsPattern
	 * 			True if the sequence represents a regex.
	 */
	public void setInterpretAsPattern(boolean interpretAsPattern) {
		this.interpretAsPattern = interpretAsPattern;
	}

	/**
	 * Getter on the respective types of the sequence and its replacement.
	 *
	 * @return The respective types of the sequence and its replacement.
	 */
	public ReplacementType getType() {
		return type;
	}

	/**
	 * Setter on the respective types of the sequence and its replacement.
	 * This must not be called from somewhere else than Replacements
	 *
	 * @param type
	 * 			The respective types of the sequence and its replacement.
	 */
	void setType(ReplacementType type) {
		this.type = type;
	}

	/**
	 * Applies the replacement depending on the respective types of the sequence
	 * and its replacement.
	 *
	 * @param s
	 * 			The string to which the replacement is applied.
	 * @return The string to which the replacement was applied.
	 */
	public String apply(String s) {
		ReplacementStrategy strategy =
			ReplacementStrategyFactory.getStrategy(type);
		return interpretAsPattern
			? strategy.replacePattern(
				s,
				getProcessedSequence(),
				getProcessedReplacement()
			)
			: strategy.replaceSequence(
				s,
				getProcessedSequence(),
				getProcessedReplacement()
			);
	}

	/**
	 * Escapes escapable characters preceded by an antislash.
	 *
	 * @param s
	 * 			The string to escape.
	 * @return The escaped string.
	 */
	private String escape(String s) {
		String res = s
			.replace("\\b", "\b")
			.replace("\\t", "\t")
			.replace("\\n", "\n")
			.replace("\\f", "\f")
			.replace("\\r", "\r")
			.replace("\\\"", "\"")
			.replace("\\'", "'")
			.replace("\\\\", "\\");
		Matcher m = Pattern.compile("\\\\u[0-9a-fA-F]{4}").matcher(s);
		String unicodeReplacedRes = new String(res);
		while (m.find()) {
			String match = res.substring(m.start(), m.end());
			String replacement = new String(Character.toChars(
				Integer.parseInt(match.substring(2), 16)
			));
			unicodeReplacedRes = unicodeReplacedRes.replace(match, replacement);
		}
		return unicodeReplacedRes;
	}

}
