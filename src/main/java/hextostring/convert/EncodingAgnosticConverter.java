package hextostring.convert;

import hextostring.debug.DebuggableDecodingAttempt;
import hextostring.debug.DebuggableDecodingAttemptList;
import hextostring.debug.DebuggableLineList;
import hextostring.debug.DebuggableStrings;
import hextostring.evaluate.EvaluationResult;
import hextostring.evaluate.EvaluatorFactory;
import hextostring.evaluate.encoding.EncodingEvaluator;
import hextostring.utils.Charsets;

/**
 * Converter choosing the right encoding by itself.
 *
 * @author Maxime PIA
 */
public class EncodingAgnosticConverter implements Converter {

	private AbstractConverter[] converters = {
		(AbstractConverter)
			ConverterFactory.getConverterInstance(Charsets.SHIFT_JIS),
		(AbstractConverter)
			ConverterFactory.getConverterInstance(Charsets.UTF16_BE),
		(AbstractConverter)
			ConverterFactory.getConverterInstance(Charsets.UTF16_LE),
		(AbstractConverter)
			ConverterFactory.getConverterInstance(Charsets.UTF8)
	};

	private EncodingEvaluator encodingEvaluator =
		EvaluatorFactory.getEncodingEvaluatorInstance();

	@Override
	public DebuggableStrings convert(String hex) {
		boolean encodingFound = false;
		int maxValidity = 0;
		DebuggableDecodingAttemptList allAttempts =
			new DebuggableDecodingAttemptList();
		DebuggableDecodingAttempt validAttempt = null;

		for (AbstractConverter c : converters) {
			DebuggableLineList lines = c.convert(hex);
			DebuggableDecodingAttempt currentAttempt =
				new DebuggableDecodingAttempt(lines, c.getCharset());

			EvaluationResult encodingEvaluationResult =
				encodingEvaluator.evaluate(lines);
			currentAttempt.setEncodingEvaluationResult(
				encodingEvaluationResult
			);
			int encodingValidity = encodingEvaluationResult.getMark();

			if (encodingValidity > maxValidity || !encodingFound) {
				maxValidity = encodingValidity;
				validAttempt = currentAttempt;

				encodingFound = true;
			}

			allAttempts.addAttempt(currentAttempt);
		}
		validAttempt.setValidEncoding(true);

		return allAttempts;
	}

}
