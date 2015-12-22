package hextostring.utils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;

import main.options.ValueClass;
import main.options.annotations.CommandLineValue;
import main.utils.ReflectionUtils;

/**
 * Charsets used in the project.
 *
 * @author Maxime PIA
 */
public class Charsets implements ValueClass {

	// Charsets used in Japanese games
	@CommandLineValue(
		value = "sjis",
		shortcut = "j",
		description = "Shift JIS"
	)
	public static final Charset SHIFT_JIS = Charset.forName("Shift_JIS");
	@CommandLineValue(
		value = "utf16-le",
		shortcut = "l",
		description = "UTF16 Little Endian"
	)
	public static final Charset UTF16_LE = Charset.forName("UTF-16LE");
	@CommandLineValue(
		value = "utf16-be",
		shortcut = "b",
		description = "UTF16 Bid Endian"
	)
	public static final Charset UTF16_BE = Charset.forName("UTF-16BE");
	// also used for test files
	@CommandLineValue(
		value = "utf8",
		shortcut = "u",
		description = "UTF8"
	)
	public static final Charset UTF8 = Charset.forName("UTF-8");

	// not a charset, used for automatic recognition
	@CommandLineValue(
		value = "detect",
		shortcut = "d",
		description = "Detect the right encoding among the other ones"
	)
	public static final Charset DETECT = CharsetAutodetect.getInstance();

	public static final Charset[] ALL_CHARSETS = getAllCharsets();

	public static Charset getValidCharset(String charsetName) {
		for (Charset cs : ALL_CHARSETS) {
			if (cs.name().equals(charsetName)) {
				return cs;
			}
		}
		return null;
	}

	private static Charset[] getAllCharsets() {

		List<Field> charsetFields = ReflectionUtils.getAnnotatedFields(
			Charsets.class,
			CommandLineValue.class
		);
		Charset[] allCharsets = new Charset[charsetFields.size()];
		int eltCounter = 0;
		for (Field charsetField : charsetFields) {
			try {
				allCharsets[eltCounter++] = (Charset) charsetField.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return allCharsets;
	}

}
