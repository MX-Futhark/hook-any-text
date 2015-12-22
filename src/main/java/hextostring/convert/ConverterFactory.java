package hextostring.convert;

import java.nio.charset.Charset;

import hextostring.replacement.Replacements;
import hextostring.utils.Charsets;

/**
 * Determine which converter to provide.
 *
 * @author Maxime PIA
 */
public class ConverterFactory {

	private static SJISConverter sjisConverterInstance = new SJISConverter();
	private static UTF16Converter utf16BEConverterInstance =
		new UTF16Converter(true);
	private static UTF16Converter utf16LEConverterInstance =
		new UTF16Converter(false);
	private static UTF8Converter utf8ConverterInstance = new UTF8Converter();

	private static EncodingAgnosticConverter encodingAgnosticConverterInstance =
		new EncodingAgnosticConverter();

	/**
	 * Provides a converter for a given charset.
	 *
	 * @param cs
	 * 			The charset encoding the hex input.
	 * @return A fitting converter.
	 */
	public static Converter getConverterInstance(Charset charset,
		Replacements r) {

		Converter c;
		if (charset == Charsets.DETECT) {
			c = encodingAgnosticConverterInstance;
		} else if (charset == Charsets.SHIFT_JIS) {
			c = sjisConverterInstance;
		} else if (charset == Charsets.UTF16_BE) {
			c = utf16BEConverterInstance;
		} else if (charset == Charsets.UTF16_LE) {
			c = utf16LEConverterInstance;
		} else if (charset == Charsets.UTF8) {
			c = utf8ConverterInstance;
		} else {
			throw new IllegalArgumentException("Invalid charset in options.");
		}
		c.setReplacements(r);
		return c;
	}

}
