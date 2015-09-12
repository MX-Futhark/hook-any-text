package hextostring.evaluate;

import hextostring.evaluate.encoding.EncodingEvaluator;
import hextostring.evaluate.hex.HexStringEvaluator;
import hextostring.evaluate.hex.SJISHexStringEvaluator;
import hextostring.evaluate.hex.UTF16HexStringEvaluator;
import hextostring.evaluate.hex.UTF8HexStringEvaluator;
import hextostring.evaluate.string.JapaneseStringEvaluator;
import hextostring.evaluate.string.ReadableStringEvaluator;
import hextostring.utils.Charsets;

import java.nio.charset.Charset;

/**
 * Determines which evaluator to provide.
 *
 * @author Maxime PIA
 */
public class EvaluatorFactory {

	private static HexStringEvaluator sjisHexStringEvaluatorInstance =
		new SJISHexStringEvaluator();
	private static HexStringEvaluator utf16HexStringEvaluatorInstance =
		new UTF16HexStringEvaluator();
	private static HexStringEvaluator utf8HexStringEvaluatorInstance =
		new UTF8HexStringEvaluator();

	private static ReadableStringEvaluator readableStringEvaluatorInstance =
		new JapaneseStringEvaluator();

	private static EncodingEvaluator encodingEvaluatorInstance =
		new EncodingEvaluator();

	/**
	 * Provides a hex evaluator for a given charset.
	 *
	 * @param cs
	 * 			The charset encoding the strings to evaluate.
	 * @return A fitting evaluator.
	 */
	public static HexStringEvaluator getHexStringEvaluatorInstance(Charset cs) {
		if (cs == Charsets.DETECT) {
			return null;
		} else if (cs == Charsets.SHIFT_JIS) {
			return sjisHexStringEvaluatorInstance;
		} else if (cs == Charsets.UTF16_BE || cs == Charsets.UTF16_LE) {
			return utf16HexStringEvaluatorInstance;
		} else if (cs == Charsets.UTF8) {
			return utf8HexStringEvaluatorInstance;
		}
		throw new IllegalArgumentException("Invalid charset: " + cs.name());
	}

	public static ReadableStringEvaluator getReadableStringEvaluatorInstance() {
		return readableStringEvaluatorInstance;
	}

	public static EncodingEvaluator getEncodingEvaluatorInstance() {
		return encodingEvaluatorInstance;
	}

}
