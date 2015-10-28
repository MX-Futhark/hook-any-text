package hextostring.debug;

import java.lang.reflect.Field;
import java.util.List;

import main.options.ValueClass;
import main.options.annotations.CommandLineValue;
import main.utils.ReflectionUtils;

/**
 * Contains all the debugging flags.
 *
 * @author Maxime PIA
 */
public class DebuggingFlags implements ValueClass {

	// _input
	private static final String LINE_HEX_INPUT_CHAR = "i";
	// _input after _hexadecimal replacements
	private static final String LINE_HEX_AFTER_HEX_REPL_CHAR = "h";
	// _transitory phase between hex & string
	private static final String LINE_HEX_AFTER_STR_REPL_CHAR = "t";
	// _global
	private static final String LINE_LIST_HEX_INPUT_CHAR = "g";
	// ("validity")16 = _76 61 6c 69 64 69 74 79
	private static final String LINE_HEX_VALIDITY_CHAR = "7";
	// _validity
	private static final String LINE_STRING_VALIDITY_CHAR = "v";
	// _Validity
	private static final String LINE_TOTAL_VALIDITY_CHAR = "V";
	// ("details")16 = _64 65 74 61 69 6c 73
	private static final String LINE_HEX_VALIDITY_DETAILS_CHAR = "6";
	// _details
	private static final String LINE_STRING_VALIDITY_DETAILS_CHAR = "d";
	// _Details
	private static final String LINE_TOTAL_VALIDITY_DETAILS_CHAR = "D";
	// _non formatted
	private static final String LINE_NON_FORMATTED_CHAR = "n";
	// _ultimate phase before formatting
	private static final String LINE_NON_FORMATTED_AFTER_STR_REPL_CHAR = "u";
	// _filtered out
	private static final String LINE_REJECTED_CHAR = "f";
	// _encoding
	private static final String LINE_LIST_ENCODING_CHAR = "e";
	// encoding _score
	private static final String LINE_LIST_ENCODING_VALIDITY_CHAR = "s";
	// _reason for the score
	private static final String LINE_LIST_ENCODING_VALIDITY_DETAILS_CHAR = "r";
	// e_xcluded encodings
	private static final String LINE_LIST_ENCODING_REJECTED_CHAR = "x";

	@CommandLineValue(
		value = LINE_HEX_INPUT_CHAR,
		description = "Hexadecimal versions of the converted lines"
	)
	public static final long LINE_HEX_INPUT = 1 << 0;

	@CommandLineValue(
		value = LINE_HEX_AFTER_HEX_REPL_CHAR,
		description = "Hexadecimal versions of the converted lines after "
			+ "hexadecimal replacements"
	)
	public static final long LINE_HEX_AFTER_HEX_REPL_INPUT = 1 << 1;

	@CommandLineValue(
		value = LINE_HEX_AFTER_STR_REPL_CHAR,
		description = "Transitory versions of the converted lines after "
			+ "hexadecimal to string replacements"
	)
	public static final long LINE_HEX_AFTER_STR_REPL_INPUT = 1 << 2;


	@CommandLineValue(
		value = LINE_LIST_HEX_INPUT_CHAR,
		description = "Global hexadecimal inputs"
	)
	public static final long LINE_LIST_HEX_INPUT = 1 << 3;


	@CommandLineValue(
		value = LINE_HEX_VALIDITY_CHAR,
		description =
			"Validity mark of the the hexadecimal versions of the lines"
	)
	public static final long LINE_HEX_VALIDITY = 1 << 4;

	@CommandLineValue(
		value = LINE_STRING_VALIDITY_CHAR,
		description = "Validity mark of the lines"
	)
	public static final long LINE_STRING_VALIDITY = 1 << 5;

	@CommandLineValue(
		value = LINE_TOTAL_VALIDITY_CHAR,
		description = "Total validity mark of the lines"
	)
	public static final long LINE_TOTAL_VALIDITY =
		LINE_HEX_VALIDITY | LINE_STRING_VALIDITY;


	@CommandLineValue(
		value = LINE_HEX_VALIDITY_DETAILS_CHAR,
		description = "Details on the validity mark of the the hexadecimal "
			+ "versions of the lines"
	)
	public static final long LINE_HEX_VALIDITY_DETAILS =
		(1 << 6) | LINE_HEX_VALIDITY;

	@CommandLineValue(
		value = LINE_STRING_VALIDITY_DETAILS_CHAR,
		description = "Details on the validity mark of the lines"
	)
	public static final long LINE_STRING_VALIDITY_DETAILS =
		(1 << 7) | LINE_STRING_VALIDITY;

	@CommandLineValue(
		value = LINE_TOTAL_VALIDITY_DETAILS_CHAR,
		description = "Total validity mark of the lines"
	)
	public static final long LINE_TOTAL_VALIDITY_DETAILS =
		LINE_HEX_VALIDITY_DETAILS | LINE_STRING_VALIDITY_DETAILS;


	@CommandLineValue(
		value = LINE_NON_FORMATTED_CHAR,
		description = "Non formatted version of the lines"
	)
	public static final long LINE_NON_FORMATTED = 1 << 8;

	@CommandLineValue(
		value = LINE_NON_FORMATTED_AFTER_STR_REPL_CHAR,
		description = "Non formatted version of the lines after string "
			+ "replacements"
	)
	public static final long LINE_NON_FORMATTED_AFTER_STR_REPL = 1 << 9;


	@CommandLineValue(
		value = LINE_REJECTED_CHAR,
		description="Lines rejected due to their validity"
	)
	public static final long LINE_REJECTED = 1 << 10;



	private static final String AUTODETECT_CONDITION =
		"Encoding detection is activated";


	@CommandLineValue(
		value = LINE_LIST_ENCODING_CHAR,
		description = "Encoding detected for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING = 1 << 11;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_VALIDITY_CHAR,
		description =
			"Validity mark of the encoding detected for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_VALIDITY = 1 << 12;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_VALIDITY_DETAILS_CHAR,
		description = "Details on the validity mark of the encoding detected "
			+ "for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_VALIDITY_DETAILS =
		(1 << 13) | LINE_LIST_ENCODING_VALIDITY;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_REJECTED_CHAR,
		description = "Lines converted with a rejected encoding",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_REJECTED = 1 << 14;

	/**
	 * Gives the command line string values corresponding to an actual long
	 * value.
	 *
	 * @param flags
	 * 			The actual numeric value.
	 * @return The command line string values equivalent of the argument.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static String longToCmdFlags(long flags)
		throws IllegalArgumentException, IllegalAccessException {

		List<Field> flagFields = ReflectionUtils.getAnnotatedFields(
			DebuggingFlags.class,
			CommandLineValue.class
		);
		StringBuilder sb = new StringBuilder();
		for (Field flagField : flagFields) {
			long fieldValue = (Long) flagField.get(null);
			if ((flags & fieldValue) == fieldValue) {
				sb.append(
					flagField.getAnnotation(CommandLineValue.class).value()
				);
			}
		}

		return sb.toString();
	}

}
