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

	// _global
	private static final String LINE_LIST_HEX_INPUT_CHAR = "g";
	// input after _hexadecimal replacements
	private static final String LINE_LIST_HEX_AFTER_HEX_REPL_CHAR = "h";
	// _transitory phase between hex & string
	private static final String LINE_LIST_HEX_AFTER_STR_REPL_CHAR = "t";
	// _input
	private static final String LINE_HEX_INPUT_CHAR = "i";
	// _validity
	private static final String LINE_VALIDITY_CHAR = "v";
	// _details
	private static final String LINE_VALIDITY_DETAILS_CHAR = "d";
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
	// hex selections _boundaries
	private static final String HEX_SELECTIONS_BOUNDS_CHAR = "b";
	// hex selections _content
	private static final String HEX_SELECTIONS_CONTENT_CHAR = "c";


	@CommandLineValue(
			value = LINE_LIST_HEX_INPUT_CHAR,
			description = "Global hexadecimal inputs"
		)
		public static final long LINE_LIST_HEX_INPUT = 1 << 0;

	@CommandLineValue(
		value = LINE_LIST_HEX_AFTER_HEX_REPL_CHAR,
		description = "Hexadecimal versions of the global input after "
			+ "hexadecimal replacements"
	)
	public static final long LINE_LIST_HEX_AFTER_HEX_REPL_INPUT = 1 << 1;

	@CommandLineValue(
		value = LINE_LIST_HEX_AFTER_STR_REPL_CHAR,
		description = "Transitory versions of the global input after "
			+ "hexadecimal to string replacements"
	)
	public static final long LINE_LIST_HEX_AFTER_STR_REPL_INPUT = 1 << 2;


	@CommandLineValue(
			value = LINE_HEX_INPUT_CHAR,
			description = "Hexadecimal versions of the converted lines"
		)
		public static final long LINE_HEX_INPUT = 1 << 3;



	@CommandLineValue(
		value = LINE_VALIDITY_CHAR,
		description = "Validity mark of the lines"
	)
	public static final long LINE_VALIDITY = 1 << 4;

	@CommandLineValue(
		value = LINE_VALIDITY_DETAILS_CHAR,
		description = "Details on the validity mark of the lines"
	)
	public static final long LINE_VALIDITY_DETAILS = (1 << 5) | LINE_VALIDITY;


	@CommandLineValue(
		value = LINE_NON_FORMATTED_CHAR,
		description = "Non formatted version of the lines"
	)
	public static final long LINE_NON_FORMATTED = 1 << 6;

	@CommandLineValue(
		value = LINE_NON_FORMATTED_AFTER_STR_REPL_CHAR,
		description = "Non formatted version of the lines after string "
			+ "replacements"
	)
	public static final long LINE_NON_FORMATTED_AFTER_STR_REPL = 1 << 7;


	@CommandLineValue(
		value = LINE_REJECTED_CHAR,
		description="Lines rejected due to their validity"
	)
	public static final long LINE_REJECTED = 1 << 8;



	private static final String AUTODETECT_CONDITION =
		"Encoding detection is activated";


	@CommandLineValue(
		value = LINE_LIST_ENCODING_CHAR,
		description = "Encoding detected for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING = 1 << 9;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_VALIDITY_CHAR,
		description =
			"Validity mark of the encoding detected for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_VALIDITY = 1 << 10;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_VALIDITY_DETAILS_CHAR,
		description = "Details on the validity mark of the encoding detected "
			+ "for the converted lines",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_VALIDITY_DETAILS =
		(1 << 11) | LINE_LIST_ENCODING_VALIDITY;

	@CommandLineValue(
		value = LINE_LIST_ENCODING_REJECTED_CHAR,
		description = "Lines converted with a rejected encoding",
		condition = AUTODETECT_CONDITION
	)
	public static final long LINE_LIST_ENCODING_REJECTED = 1 << 12;

	@CommandLineValue(
		value = HEX_SELECTIONS_BOUNDS_CHAR,
		description = "Selection bounds"
	)
	public static final long HEX_SELECTIONS_BOUNDS = 1 << 13;

	@CommandLineValue(
		value = HEX_SELECTIONS_CONTENT_CHAR,
		description = "Hex content of a selection"
	)
	public static final long HEX_SELECTIONS_CONTENT = 1 << 14;

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
