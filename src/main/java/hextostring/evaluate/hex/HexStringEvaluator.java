package hextostring.evaluate.hex;

import hextostring.evaluate.EvaluationResult;
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
	public EvaluationResult evaluate(String s) {
		StringBuilder details = new StringBuilder();

		int length = s.length();
		int mark = 0;

		details.append("length=" + length);
		if (length <= SMALL_LENGTH_THRESHOLD) {
			details.append(", <= " + SMALL_LENGTH_THRESHOLD);
			details.append("; applying malus of ");
			details.append(SMALL_LENGTH_MALUS);
			details.append(" once: ");
			mark = -SMALL_LENGTH_MALUS;
			details.append(mark);
		} else {
			details.append(", > " + SMALL_LENGTH_THRESHOLD);
			details.append("; applying bonus of ");
			details.append(HEX_LENGTH_VALIDITY_WEIGHT);
			details.append(" for every character: +");
			mark = length * HEX_LENGTH_VALIDITY_WEIGHT;
			details.append(mark);
		}
		details.append("\nTotal: " + mark);

		return new EvaluationResult(mark, details.toString());
	}

}
