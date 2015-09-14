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
		int points = 0;
		int total = 0;

		details.append("length=" + length);
		if (length <= SMALL_LENGTH_THRESHOLD) {
			details.append(", <= " + SMALL_LENGTH_THRESHOLD);
			details.append("; applying malus of ");
			details.append(SMALL_LENGTH_MALUS);
			details.append(" once: ");
			points = -SMALL_LENGTH_MALUS;
			details.append(points);
			total += points;
		} else {
			details.append(", > " + SMALL_LENGTH_THRESHOLD);
			details.append("; applying bonus of ");
			details.append(HEX_LENGTH_VALIDITY_WEIGHT);
			details.append(" for every character: +");
			points = length * HEX_LENGTH_VALIDITY_WEIGHT;
			details.append(points);
			total += points;
		}
		details.append("\nTotal: " + total);

		return new EvaluationResult(total, details.toString());
	}

}
