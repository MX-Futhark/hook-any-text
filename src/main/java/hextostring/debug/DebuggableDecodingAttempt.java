package hextostring.debug;

import hextostring.evaluate.EvaluationResult;
import hextostring.utils.StringUtils;

import java.nio.charset.Charset;

/**
 * Wraps all the necessary information to debug an attempt at decoding a line
 * whose encoding is unknown.
 *
 * @author Maxime PIA
 */
public class DebuggableDecodingAttempt {

	private DebuggableLineList attempt;
	private Charset encoding;
	private EvaluationResult encodingEvaluationResult;
	private boolean validEncoding = false;

	public DebuggableDecodingAttempt(DebuggableLineList attempt,
		Charset encoding) {

		this.attempt = attempt;
		this.encoding = encoding;
	}

	/**
	 * Getter on the list of list attempted to be decoded.
	 *
	 * @return The list of list attempted to be decoded.
	 */
	public DebuggableLineList getAttempt() {
		return attempt;
	}

	/**
	 * Getter on the encoding used to decode the list of lines.
	 *
	 * @return The encoding used to decode the list of lines.
	 */
	public Charset getEncoding() {
		return encoding;
	}

	/**
	 * Getter on the evaluation result of the encoding used to decode the
	 * list of lines.
	 *
	 * @return The evaluation result of the encoding used to decode the
	 * list of lines.
	 */
	public EvaluationResult getEncodingEvaluationResult() {
		return encodingEvaluationResult;
	}

	/**
	 * Setter on the evaluation result of the encoding used to decode the
	 * list of lines.
	 *
	 * @param encodingEvaluationResult
	 * 			The evaluation result of the encoding used to decode the
	 * 			list of lines.
	 */
	public void setEncodingEvaluationResult(
		EvaluationResult encodingEvaluationResult) {
		this.encodingEvaluationResult = encodingEvaluationResult;
	}

	/**
	 * Getter on whether or not the encoding was deemed valid for this
	 * list of lines.
	 *
	 * @return True is the encoding was deemed valid for this list of lines.
	 */
	public boolean isValidEncoding() {
		return validEncoding;
	}

	/**
	 * Setter on whether or not the encoding was deemed valid for this
	 * list of lines.
	 *
	 * @param validEncoding
	 * 			True is the encoding is deemed valid for this list of lines.
	 */
	public void setValidEncoding(boolean validEncoding) {
		this.validEncoding = validEncoding;
	}

	/**
	 * Formats the attempt depending on the debugging flags.
	 *
	 * @param debuggingFlags
	 * 			The debugging flags used to format these lines.
	 * @param converterStrictness
	 * 			The validity value below which a converted string is eliminated.
	 * @return A string representing these lines, with or without debug traces.
	 */
	public String toString(long debuggingFlags, int converterStrictness) {
		StringBuilder sb = new StringBuilder();

		if ((debuggingFlags & DebuggingFlags.LINE_LIST_ENCODING) > 0) {
			sb.append("Encoding: " + encoding.name() + "\n");
		}
		if ((debuggingFlags & DebuggingFlags.LINE_LIST_ENCODING_VALIDITY) > 0) {
			sb.append("Encoding validity: ");
			sb.append(encodingEvaluationResult.getMark() + "\n");

			if ((debuggingFlags
				& DebuggingFlags.LINE_LIST_ENCODING_VALIDITY_DETAILS)
				== DebuggingFlags.LINE_LIST_ENCODING_VALIDITY_DETAILS) {

				sb.append("details:\n");
				sb.append(StringUtils.indent(
					encodingEvaluationResult.getDetails(),
					"\t",
					1
				) + "\n");
			}
		}
		sb.append(attempt.toString(debuggingFlags, converterStrictness));

		return sb.toString();
	}

}
