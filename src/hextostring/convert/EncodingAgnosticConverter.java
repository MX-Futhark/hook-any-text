package hextostring.convert;

import hextostring.debug.DebuggableLineList;
import hextostring.evaluate.EvaluatorFactory;
import hextostring.evaluate.encoding.EncodingEvaluator;
import hextostring.utils.Charsets;

import java.util.List;

/**
 * Converter choosing the right encoding by itself.
 *
 * @author Maxime PIA
 */
public class EncodingAgnosticConverter extends Converter {

	private Converter[] converters = {
		ConverterFactory.getConverterInstance(Charsets.SHIFT_JIS),
		ConverterFactory.getConverterInstance(Charsets.UTF16_BE),
		ConverterFactory.getConverterInstance(Charsets.UTF16_LE),
		ConverterFactory.getConverterInstance(Charsets.UTF8)
	};

	private EncodingEvaluator encodingEvaluator =
		EvaluatorFactory.getEncodingEvaluatorInstance();


	public EncodingAgnosticConverter() {
		super(Charsets.DETECT);
	}

	@Override
	protected List<String> extractConvertibleChunks(String hex) {
		return null;
	}

	@Override
	public DebuggableLineList convert(String hex) {
		boolean encodingFound = false;
		int maxValidity = 0;
		DebuggableLineList allLines =
			new DebuggableLineList(preProcessHex(hex));
		allLines.setCharsetAutoDetected(true);

		for (Converter c : converters) {
			DebuggableLineList lines = c.convert(hex);
			allLines.getLines().addAll(lines.getLines());
			int encodingValidity = encodingEvaluator.evaluate(lines);

			if (encodingValidity > maxValidity || !encodingFound) {
				maxValidity = encodingValidity;
				allLines.setCharset(c.getCharset());

				encodingFound = true;
			}
		}

		return allLines;
	}

}
