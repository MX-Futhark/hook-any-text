package hextostring.convert;

import java.nio.charset.Charset;
import java.util.List;

import hextostring.ConvertOptions;
import hextostring.debug.DebuggableLine;
import hextostring.debug.DebuggableLineList;
import hextostring.evaluate.EvaluatorFactory;
import hextostring.evaluate.hex.HexStringEvaluator;
import hextostring.evaluate.string.ReadableStringEvaluator;
import hextostring.replacement.HexToStrStrategy;
import hextostring.replacement.ReplacementType;
import hextostring.replacement.Replacements;

/**
 * Abstract converter using a definite encoding.
 *
 * @author Maxime PIA
 */
public abstract class AbstractConverter implements Converter {

	private Charset charset;
	private Replacements replacements;
	private HexStringEvaluator hexStringEvaluator;
	private ReadableStringEvaluator japaneseStringEvaluator;

	public AbstractConverter(Charset charset) {
		setCharset(charset);
	}

	/**
	 * Getter on the charset used by the converter.
	 *
	 * @param charset
	 * 			The charset used by the converter.
	 */
	protected Charset getCharset() {
		return charset;
	}

	/**
	 * Sets the charset for the converter and adapts its evaluators accordingly.
	 *
	 * @param charset
	 * 			The new charset for this converter.
	 */
	protected void setCharset(Charset charset) {
		this.charset = charset;
		this.hexStringEvaluator =
			EvaluatorFactory.getHexStringEvaluatorInstance(charset);
		this.japaneseStringEvaluator =
			EvaluatorFactory.getReadableStringEvaluatorInstance();
	}

	@Override
	public void setReplacements(Replacements r) {
		replacements = r;
		if (replacements == null) {
			replacements = ConvertOptions.DEFAULT_REPLACEMENTS;
		}
	}

	/**
	 * Verifies if the input is valid and sets it to lowercase without spaces.
	 *
	 * @param hex
	 * 			The input string.
	 * @return
	 * 			The lowercase, sans spaces version of the input string.
	 */
	protected static String preProcessHex(String hex) {
		String lowercaseHex =
			hex.toLowerCase().replace(" ", "").replace("\n", "");
		if (!lowercaseHex.matches("[a-f0-9]+")) {
			throw new IllegalArgumentException("Invalid hex string.");
		}
		return lowercaseHex;
	}

	/**
	 * Inputs strings may contains areas of zeros. This method removes them.
	 *
	 * @param hex
	 * 			The lowercase version of the input string.
	 * @return A list a strings found between areas of zeros.
	 */
	protected abstract List<String> extractConvertibleChunks(String hex);

	/**
	 * Converts a hex string into several Japanese lines
	 *
	 * @param hex
	 * 			A hex string copied from Cheat Engine's memory viewer.
	 * @return The result of the conversion wrapped into a debuggable object.
	 */
	public DebuggableLineList convert(String hex) {
		DebuggableLineList lines = new DebuggableLineList(preProcessHex(hex));
		List<String> hexCollection =
			extractConvertibleChunks(lines.getHexInput());
		for (String hexChunk : hexCollection) {
			DebuggableLine line = new DebuggableLine(hexChunk);
			line.setHexAfterHexReplacements(
				replacements.apply(hexChunk, ReplacementType.HEX2HEX)
			);
			line.setHexEvaluationResult(
				hexStringEvaluator.evaluate(line.getHexAfterHexReplacements())
			);
			line.setHexAfterStrReplacements(replacements.apply(
				line.getHexAfterHexReplacements(),
				ReplacementType.HEX2STR
			));
			line.setReadableString(HexToStrStrategy.toReadableString(
				line.getHexAfterStrReplacements(),
				charset
			));
			line.setReadableStringAfterReplacements(replacements.apply(
				line.getReadableString(),
				ReplacementType.STR2STR
			));
			line.setReadableStringEvaluationResult(
				japaneseStringEvaluator.evaluate(
					line.getReadableStringAfterReplacements()
				)
			);
			lines.addLine(line);
		}
		return lines;
	}

}
