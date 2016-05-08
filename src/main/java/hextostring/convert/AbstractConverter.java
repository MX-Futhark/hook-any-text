package hextostring.convert;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import hextostring.ConvertOptions;
import hextostring.debug.DebuggableLine;
import hextostring.debug.DebuggableLineList;
import hextostring.evaluate.EvaluatorFactory;
import hextostring.evaluate.string.StringEvaluator;
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
	private StringEvaluator japaneseStringEvaluator;

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
		this.japaneseStringEvaluator =
			EvaluatorFactory.getStringEvaluatorInstance();
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
	 * Calls extractConvertibleChunks of hex parts of a transitory string and
	 * re-glue mixed parts together whenever possible.
	 *
	 * @param mixed
	 * 			The transitory string
	 * @return A list of convertible transitory strings.
	 */
	private List<String> extractConvertibleChunksFromMixedString(String mixed) {

		List<String> result = new LinkedList<>();
		List<String> parts = HexToStrStrategy.splitParts(mixed);
		List<String> previousHexChunks = null;
		String previousHexPart = null, previousReadablePart = null;
		boolean hexPart = true, stringPreceded = false;

		for (String part : parts) {

			int lastIndex = result.size() - 1;

			if (hexPart) {

				List<String> chunks = extractConvertibleChunks(part);

				if (chunks.size() > 0 && previousReadablePart != null) {
					// the first chunk corresponds to the start of the hex part
					boolean stringPrecedes = part.indexOf(chunks.get(0)) == 0;
					if (stringPrecedes) {
						result.set(
							lastIndex,
							result.get(lastIndex) + chunks.get(0)
						);
						chunks = chunks.subList(1, chunks.size());
						stringPreceded = true;
					} else {
						stringPreceded = false;
					}
				}

				result.addAll(chunks);
				previousHexPart = part;
				previousHexChunks = chunks;

			} else {

				part = "-" + part + "-"; // restore "-" removed by splitParts

				boolean stringFollows = false;

				if (previousHexChunks.size() + (stringPreceded ? 1 : 0) > 0) {
					String lastChunk = result.get(lastIndex);
					// the last chunk corresponds to the end of the hex part
					stringFollows = previousHexPart.lastIndexOf(lastChunk)
						+ lastChunk.length() == previousHexPart.length();
					if (stringFollows) {
						result.set(lastIndex, lastChunk + part);
					}
				}
				if (!stringFollows) {
					result.add(part);
				}

				previousReadablePart = part;
			}

			hexPart = !hexPart;
		}

		return result;
	}

	/**
	 * Converts a hex string into several Japanese lines
	 *
	 * @param hex
	 * 			A hex string copied from Cheat Engine's memory viewer.
	 * @return The result of the conversion wrapped into a debuggable object.
	 */
	@Override
	public DebuggableLineList convert(String hex) {
		DebuggableLineList lines = new DebuggableLineList(preProcessHex(hex));
		lines.setHexInputAfterHexReplacements(
			replacements.apply(lines.getHexInput(), ReplacementType.HEX2HEX)
		);
		lines.setHexInputAfterStrReplacements(
			replacements.apply(
				lines.getHexInputAfterHexReplacements(),
				ReplacementType.HEX2STR
			)
		);
		List<String> hexCollection = extractConvertibleChunksFromMixedString(
			lines.getHexInputAfterStrReplacements()
		);
		for (String hexChunk : hexCollection) {
			DebuggableLine line = new DebuggableLine(hexChunk);
			line.setReadableString(HexToStrStrategy.toReadableString(
				line.getHex(),
				charset
			));
			line.setReadableStringAfterReplacements(replacements.apply(
				line.getReadableString(),
				ReplacementType.STR2STR
			));
			line.setEvaluationResult(
				japaneseStringEvaluator.evaluate(
					line.getReadableStringAfterReplacements()
				)
			);
			lines.addLine(line);
		}
		return lines;
	}

}
