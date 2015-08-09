package hextostring.convert;

import hextostring.utils.Charsets;

import java.nio.charset.Charset;

/**
 * Determine which converter to provide.
 *
 * @author Maxime PIA
 */
public class ConverterFactory {

	private static SJISConverter sjisConverterInstance = new SJISConverter();
	private static UTF16Converter utf16ConverterInstance = new UTF16Converter();

	/**
	 * Provides a converter for a given charset.
	 *
	 * @param cs
	 * 			The charset encoding the hex input.
	 * @return A fitting converter.
	 */
	public static Converter getConverterInstance(Charset charset) {
		if (charset.equals(Charsets.SHIFT_JIS)) {
			return sjisConverterInstance;
		} else if (charset.equals(Charsets.UTF16)) {
			return utf16ConverterInstance;
		} else {
			throw new IllegalArgumentException("Invalid charset in options.");
		}
	}

}
