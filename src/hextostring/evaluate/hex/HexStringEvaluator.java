package hextostring.evaluate.hex;

import hextostring.evaluate.Evaluator;

/**
 * Abstract evaluator for hexadecimal strings.
 *
 * @author Maxime PIA
 */
public abstract class HexStringEvaluator implements Evaluator<String> {

	// the longer the hex string, the more likely for it to be a valid one
	public static final int HEX_LENGTH_VALIDITY_WEIGHT = 1;

	public static final int SMALL_LENGTH_THRESHOLD = 6;

	// garbage hex chunks tend to be short
	public static final int SMALL_LENGTH_MALUS = 24;

	@Override
	public int evaluate(String s) {
		int length = s.length();
		return length <= SMALL_LENGTH_THRESHOLD
			? -SMALL_LENGTH_MALUS
			: length * HEX_LENGTH_VALIDITY_WEIGHT;
	}

}
