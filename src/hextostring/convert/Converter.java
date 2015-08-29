package hextostring.convert;

import hextostring.debug.DebuggableLine;
import hextostring.debug.DebuggableLineList;
import hextostring.evaluate.EvaluatorFactory;
import hextostring.evaluate.hex.HexStringEvaluator;
import hextostring.evaluate.string.ReadableStringEvaluator;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract converter.
 * Converters transform a hexadecimal string into a readable string.
 *
 * @author Maxime PIA
 */
public abstract class Converter {

	private Charset charset;
	private HexStringEvaluator hexStringEvaluator;
	private ReadableStringEvaluator japaneseStringEvaluator;

	public Converter(Charset charset) {
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
	 * Converts a byte array to a string.
	 *
	 * @param data
	 * 			An array of bytes.
	 * @return The corresponding string.
	 */
	private String byteArrayToString(byte[] data) {
		return new String(data, charset);
	}

	/**
	 * Converts a byte list to a string.
	 *
	 * @param data
	 * 			A list of bytes.
	 * @return The corresponding string.
	 */
	private String byteListToString(List<Byte> data) {
		byte[] arrayData = new byte[data.size()];
		int cmpt = 0;
		for (Byte b : data) {
			arrayData[cmpt++] = b;
		}
		return byteArrayToString(arrayData);
	}

	private static List<Byte> HexStringToByteList(String hex) {
		List<Byte> byteList = new LinkedList<>();
		boolean littleEnd = false;
		int currVal = 0;
		for (int i = 0; i < hex.length(); ++i) {
			if (littleEnd) {
				currVal = currVal | (Character.digit(hex.charAt(i), 16));
			} else {
				currVal = (Character.digit(hex.charAt(i), 16)) << 4;
			}
			if (littleEnd) {
				byteList.add((byte) (currVal & 0xFF));
			}
			littleEnd = !littleEnd;
		}

		return byteList;
	}

	/**
	 * Converts a hex string into several Japanese lines
	 *
	 * @param hex
	 * 			A hex string copied from Cheat Engine's memory viewer.
	 * @return
	 */
	public DebuggableLineList convert(String hex) {
		DebuggableLineList lines =
			new DebuggableLineList(preProcessHex(hex), charset);
		List<String> hexCollection = extractConvertibleChunks(lines.getHexInput());
		for (String hexChunk : hexCollection) {
			DebuggableLine line = new DebuggableLine(hexChunk, charset);
			line.setHexValidity(hexStringEvaluator.evaluate(hexChunk));
			line.setReadableString(
				byteListToString(
					HexStringToByteList(hexChunk)
				)
			);
			line.setReadableStringValidity(
				japaneseStringEvaluator.evaluate(line.getReadableString())
			);
			lines.addLine(line);
		}
		return lines;
	}

}
