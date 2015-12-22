package hextostring.debug;

import hextostring.evaluate.EvaluationResult;
import main.utils.StringUtils;


/**
 * Wraps all the necessary information to debug a line:
 *  - the original hex string for the line and its validity
 *  - the result of the conversion of said hex string and its validity
 *  - the same string with all native formatting information processed
 *
 * @author Maxime PIA
 */
public class DebuggableLine {

	private String hex;
	private String hexAfterHexReplacements;
	private String hexAfterStrReplacements;
	private String readableString;
	private String readableStringAfterReplacements;

	private EvaluationResult hexEvaluationResult;
	private EvaluationResult readableStringEvaluationResult;

	private String decorationBefore = "";
	private String decorationAfter = "";

	public DebuggableLine(String hex) {
		setHex(hex);
	}

	/**
	 * Getter on the hex chunk from which this line originates.
	 *
	 * @return The hex chunk from which this line originates.
	 */
	public String getHex() {
		return hex;
	}

	/**
	 * Setter on the hex chunk from which this line originates.
	 *
	 * @param hex
	 * 			The hex chunk from which this line originates.
	 */
	public void setHex(String hex) {
		this.hex = hex;
		setHexAfterHexReplacements(hex);
	}

	/**
	 * Getter on the hex chunk from which this line originates to which HEX2HEX
	 * replacements were applied.
	 *
	 * @return The hex chunk from which this line originates to which HEX2HEX
	 * 		replacements were applied.
	 */
	public String getHexAfterHexReplacements() {
		return hexAfterHexReplacements;
	}

	/**
	 * Setter on the hex chunk from which this line originates to which HEX2HEX
	 * replacements were applied.
	 *
	 * @param hexAfterHexReplacements
	 * 			The hex chunk from which this line originates to which HEX2HEX
	 * 			replacements were applied.
	 */
	public void setHexAfterHexReplacements(String hexAfterHexReplacements) {
		this.hexAfterHexReplacements = hexAfterHexReplacements;
		setHexAfterStrReplacements(hexAfterHexReplacements);
	}

	/**
	 * Getter on the hex chunk from which this line originates to which HEX2HEX
	 * & HEX2STR replacements were applied.
	 *
	 * @return The hex chunk from which this line originates to which HEX2HEX
	 * 		& HEX2STR replacements were applied.
	 */
	public String getHexAfterStrReplacements() {
		return hexAfterStrReplacements;
	}

	/**
	 * Setter on the hex chunk from which this line originates to which HEX2HEX
	 * & HEX2STR replacements were applied.
	 *
	 * @param hexAfterStrReplacements
	 * 			The hex chunk from which this line originates to which HEX2HEX
	 * 			& HEX2STR replacements were applied.
	 */
	public void setHexAfterStrReplacements(String hexAfterStrReplacements) {
		this.hexAfterStrReplacements = hexAfterStrReplacements;
	}

	/**
	 * Getter on the non-formatted string.
	 *
	 * @return The non-formatted string.
	 */
	public String getReadableString() {
		return readableString;
	}

	/**
	 * Setter on the non-formatted string.
	 *
	 * @param readableString
	 * 			The new non-formatted string.
	 */
	public void setReadableString(String readableString) {
		this.readableString = readableString;
		setReadableStringAfterReplacements(readableString);
	}

	/**
	 * Getter on the non-formatted string to which STR2STR replacements were
	 * applied.
	 *
	 * @return The non-formatted string to which STR2STR replacements were
	 * 		applied.
	 */
	public String getReadableStringAfterReplacements() {
		return readableStringAfterReplacements;
	}

	/**
	 * Setter on the non-formatted string to which STR2STR replacements were
	 * applied.
	 *
	 * @param readableStringAfterReplacements
	 * 			The non-formatted string to which STR2STR replacements were
	 * 			applied.
	 */
	public void setReadableStringAfterReplacements(
		String readableStringAfterReplacements) {

		this.readableStringAfterReplacements = readableStringAfterReplacements;
	}

	/**
	 * Getter on the evaluation result of the hex chunk from which this line
	 * originates.
	 *
	 * @return The evaluation result of the hex chunk from which this line
	 * originates.
	 */
	public EvaluationResult getHexEvaluationResult() {
		return hexEvaluationResult;
	}

	/**
	 * Setter on the evaluation result of the hex chunk from which this line
	 * originates.
	 *
	 * @param hexEvaluationResult
	 * 			The new evaluation result of the hex chunk from which this line
	 * 			originates.
	 */
	public void setHexEvaluationResult(EvaluationResult hexEvaluationResult) {
		this.hexEvaluationResult = hexEvaluationResult;
	}

	/**
	 * Getter on the evaluation result of the non-formatted string.
	 *
	 * @return The evaluation result of the non-formatted string.
	 */
	public EvaluationResult getReadableStringEvaluationResult() {
		return readableStringEvaluationResult;
	}

	/**
	 * Setter on the evaluation result of the non-formatted string.
	 *
	 * @param readableStringEvaluationResult
	 * 			The new evaluation result of the non-formatted string.
	 */
	public void setReadableStringEvaluationResult(
		EvaluationResult readableStringEvaluationResult) {
		this.readableStringEvaluationResult = readableStringEvaluationResult;
	}

	/**
	 * Convenience getter on the total validity of this line.
	 *
	 * @return The total validity of this line.
	 */
	public int getValidity() {
		return this.hexEvaluationResult.getMark()
			+ this.readableStringEvaluationResult.getMark();
	}

	/**
	 * Setter on the string put before this line in the toString method.
	 *
	 * @param decorationBefore
	 * 			The new string put before this line in the toString method.
	 */
	public void setDecorationBefore(String decorationBefore) {
		this.decorationBefore = decorationBefore;
	}

	/**
	 * Setter on the string put after this line in the toString method.
	 *
	 * @param decorationAfter
	 * 			The new string put after this line in the toString method.
	 */
	public void setDecorationAfter(String decorationAfter) {
		this.decorationAfter = decorationAfter;
	}

	/**
	 * Formats this line depending on the debugging flags.
	 *
	 * @param debuggingFlags
	 * 			The debugging flags used to format this line.
	 * @return a string representing this line, with or without debug traces.
	 */
	public String toString(long debuggingFlags) {
		StringBuilder sb = new StringBuilder();

		if ((debuggingFlags & DebuggingFlags.LINE_HEX_INPUT) > 0) {
			sb.append("hex: \n0x" + hex + "\n");
		}
		if ((debuggingFlags & DebuggingFlags.LINE_HEX_AFTER_HEX_REPL_INPUT)
			> 0) {

			sb.append("hex after hex replacements: \n0x"
				+ hexAfterHexReplacements + "\n");
		}
		if ((debuggingFlags & DebuggingFlags.LINE_HEX_AFTER_STR_REPL_INPUT)
			> 0) {

			sb.append("hex after str replacements: \n0x"
				+ hexAfterStrReplacements + "\n");
		}

		if ((debuggingFlags & DebuggingFlags.LINE_HEX_VALIDITY) > 0) {
			sb.append("hex validity: " + hexEvaluationResult.getMark() + "\n");
			if ((debuggingFlags
				& DebuggingFlags.LINE_HEX_VALIDITY_DETAILS)
				== DebuggingFlags.LINE_HEX_VALIDITY_DETAILS) {
				sb.append("details: \n");
				sb.append(StringUtils.indent(
					hexEvaluationResult.getDetails(),
					"\t",
					1
				) + "\n");
			}
		}

		if ((debuggingFlags & DebuggingFlags.LINE_STRING_VALIDITY) > 0) {
			sb.append("string validity: ");
			sb.append(readableStringEvaluationResult.getMark() + "\n");
			if ((debuggingFlags
				& DebuggingFlags.LINE_STRING_VALIDITY_DETAILS)
				== DebuggingFlags.LINE_STRING_VALIDITY_DETAILS) {
				sb.append("details: \n");
				sb.append(StringUtils.indent(
					readableStringEvaluationResult.getDetails(),
					"\t",
					1
				) + "\n");
			}
		}

		if ((debuggingFlags & DebuggingFlags.LINE_NON_FORMATTED) > 0) {
			sb.append("non formatted: \n" + readableString + "\n");
		}
		if ((debuggingFlags & DebuggingFlags.LINE_NON_FORMATTED_AFTER_STR_REPL)
			> 0) {

			sb.append("non formatted after replacements: \n"
				+ readableStringAfterReplacements + "\n");
		}

		if (debuggingFlags > 0) {
			sb.append("result: \n");
		}
		sb.append(decorationBefore + readableStringAfterReplacements
			+ decorationAfter);

		return sb.toString();
	}

}
