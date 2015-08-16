package hextostring.evaluate.encoding;

import hextostring.debug.DebuggableLineList;
import hextostring.evaluate.Evaluator;

/**
 * Evaluator for a list of line created using a given encoding.
 *
 * @author Maxime PIA
 */
public class EncodingEvaluator implements Evaluator<DebuggableLineList> {

	// The threshold below which a line extracted from the hex data
	// is not considered in the encoding detection algorithm.
	public static int LINE_VALIDITY_THRESHOLD = 0;

	@Override
	public int evaluate(DebuggableLineList lines) {
		return lines.getTotalReadableStringValidity(LINE_VALIDITY_THRESHOLD);
	}

}
