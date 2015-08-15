package hextostring.utils;

import java.nio.charset.Charset;

/**
 * Charsets used in the project.
 *
 * @author Maxime PIA
 */
public class Charsets {

	// Charsets used in Japanese games
	public static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");
	public static final Charset UTF16_LE = Charset.forName("UTF-16LE");
	public static final Charset UTF16_BE = Charset.forName("UTF-16BE");

	// Charset used for test files
	public static final Charset UTF8 = Charset.forName("UTF-8");

}
