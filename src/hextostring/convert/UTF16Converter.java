package hextostring.convert;

import hextostring.evaluate.EvaluatorFactory;
import hextostring.utils.Charsets;

import java.util.List;

/**
 * Standard converter for UTF-16-encoded hexadecimal strings.
 *
 * @author Maxime PIA
 */
public class UTF16Converter extends Converter {

	public UTF16Converter() {
		super(
			Charsets.UTF16,
			EvaluatorFactory.getHexStringEvaluatorInstance(Charsets.UTF16)
		);
	}

	@Override
	protected List<String> extractConvertibleChunks(String hex) {
		// TODO
		throw new UnsupportedOperationException(
			"UTF-16 has yet to be supported."
		);
	}

}
