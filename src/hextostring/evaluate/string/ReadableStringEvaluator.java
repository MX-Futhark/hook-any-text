package hextostring.evaluate.string;

import hextostring.evaluate.Evaluator;

/**
 * Abstract evaluator for non hexadecimal strings.
 *
 * @author Maxime PIA
 */
public abstract class ReadableStringEvaluator implements Evaluator {

	// an invalid character reduces the validity of the string
	public static final int INVALID_CHARS_MALUS = 4;

}
