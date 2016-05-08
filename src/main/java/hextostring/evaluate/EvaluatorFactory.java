package hextostring.evaluate;

import hextostring.evaluate.encoding.EncodingEvaluator;
import hextostring.evaluate.string.JapaneseStringEvaluator;
import hextostring.evaluate.string.StringEvaluator;

/**
 * Determines which evaluator to provide.
 *
 * @author Maxime PIA
 */
public class EvaluatorFactory {

	private static StringEvaluator stringEvaluatorInstance =
		new JapaneseStringEvaluator();

	private static EncodingEvaluator encodingEvaluatorInstance =
		new EncodingEvaluator();

	public static StringEvaluator getStringEvaluatorInstance() {
		return stringEvaluatorInstance;
	}

	public static EncodingEvaluator getEncodingEvaluatorInstance() {
		return encodingEvaluatorInstance;
	}

}
