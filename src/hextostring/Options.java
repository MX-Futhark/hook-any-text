package hextostring;

import hextostring.utils.Charsets;

import java.nio.charset.Charset;

/**
 * Options for string conversion.
 *
 * @author Maxime PIA
 */
public class Options {

	public static final int DEFAULT_DEBUG_LEVEL = 0;
	public static final int DEFAULT_STRICTNESS = 20;
	public static final Charset DEFAULT_CHARSET = Charsets.SHIFT_JIS;

	private int strictness = DEFAULT_STRICTNESS;
	private int debugLevel = DEFAULT_DEBUG_LEVEL;
	private Charset charset = DEFAULT_CHARSET;

	/**
	 * Parses program arguments into an Options object.
	 *
	 * @param args
	 * 			Command line arguments.
	 */
	public Options(String[] args) {
		try {
			parseOptions(args);
		} catch (Exception e) {
			System.err.println(usage(e.getMessage()));
		}
	}

	private void parseOptions(String[] args) throws IllegalArgumentException {
		int charsetSet = 0;

		for (String arg : args) {
			if (arg.substring(0, 3).equals("-d=")) {
				debugLevel = Integer.parseInt(arg.substring(3));
				if (debugLevel < 0 || debugLevel > 5) {
					throw new IllegalArgumentException(
						"Invalid debug level: "	+ arg
					);
				}
			} else if (arg.substring(0, 3).equals("-s=")) {
				strictness = Integer.parseInt(arg.substring(3));
			} else if (arg.equals("-sjis")) {
				charset = Charsets.SHIFT_JIS;
				++charsetSet;
			} else if (arg.equals("-utf16-le")) {
				charset = Charsets.UTF16_LE;
				++charsetSet;
			} else if (arg.equals("-utf16-be")) {
				charset = Charsets.UTF16_BE;
				++charsetSet;
			} else {
				throw new IllegalArgumentException("Invalid Argument: " + arg);
			}
		}

		if (charsetSet > 1) {
			throw new IllegalArgumentException(
				"More than one encoding argument."
			);
		}
	}

	/**
	 * Defines how to use command line options.
	 *
	 * @param message
	 * 			The reason why the usage message is printed.
	 * @return The usage message.
	 */
	public String usage(String message) {
		StringBuilder usage = new StringBuilder(message);
		usage.append(
			"\n\nPlease use the following arguments:"
		);
		usage.append(
			"\n\t-d=N\tSet debug traces to level N, 0 <= N <= 5 (optional)");
		usage.append(
			"\n\t-s=N\tWhere N in an integer. The lower the value, the " +
			"higher the chance of considering invalid hex data as a " +
			"valid string (optional, default = " + DEFAULT_STRICTNESS + ")"
		);
		usage.append(
			"\n\t-sjis\tTo interpret the input as Shift JIS " +
			"(default if no encoding flag set, incompatible with other " +
			"encoding flags)"
		);
		usage.append(
			"\n\t-utf16-be\tTo interpret the input as UTF-16 Big Endian" +
			"(optional, incompatible with other encoding flags)"
		);
		usage.append(
			"\n\t-utf16-le\tTo interpret the input as UTF-16 Little Endian" +
			"(optional, incompatible with other encoding flags)"
		);
		return usage.toString();
	}

	/**
	 * Getter on the strictness of the conversion process.
	 *
	 * Strictness defines a limit below which a string will be considered
	 * unworthy to be put into the final conversion result.
	 *
	 * @return The strictness of the program.
	 */
	public int getStrictness() {
		return strictness;
	}

	/**
	 * Getter on the debugging level to use.
	 *
	 * There are five level for debugging, every level including the lower ones:
	 *  - 0: No debug traces.
	 *  - 1: Display the hex string corresponding to each line.
	 *  - 2: Display the validity of each line.
	 *  - 3: Display the input.
	 *  - 4: Display strings for which validity < strictness.
	 *  - 5: Display the non formatted version of each line.
	 *
	 * @return The debug level of the program.
	 */
	public int getDebugLevel() {
		return debugLevel;
	}

	/**
	 * Getter on the charset to work with.
	 *
	 * @return The charset in which to convert hex input strings.
	 */
	public Charset getCharset() {
		return charset;
	}

}
