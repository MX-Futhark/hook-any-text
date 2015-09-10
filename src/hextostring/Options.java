package hextostring;

import hextostring.debug.DebuggingFlags;
import hextostring.utils.Charsets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Options for string conversion.
 *
 * @author Maxime PIA
 */
public class Options {

	public static final long DEFAULT_DEBUGGING_FLAGS = 0;
	public static final int DEFAULT_STRICTNESS = 20;
	public static final Charset DEFAULT_CHARSET = Charsets.DETECT;

	private int strictness = DEFAULT_STRICTNESS;
	private long debuggingFlags = DEFAULT_DEBUGGING_FLAGS;
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
			System.exit(0);
		}
	}

	private void parseOptions(String[] args) throws IllegalArgumentException {
		int charsetSet = 0;

		for (String arg : args) {
			if (arg.startsWith("--strictness=")) {
				strictness = Integer.parseInt(arg.substring(13));
			} else if (arg.equals("--encoding=detect") || arg.equals("-d")) {
				charset = null;
				++charsetSet;
			} else if (arg.equals("--encoding=sjis") || arg.equals("-j")) {
				charset = Charsets.SHIFT_JIS;
				++charsetSet;
			} else if (arg.equals("--encoding=utf16-le") || arg.equals("-l")) {
				charset = Charsets.UTF16_LE;
				++charsetSet;
			} else if (arg.equals("--encoding=utf16-be") || arg.equals("-b")) {
				charset = Charsets.UTF16_BE;
				++charsetSet;
			} else if (arg.equals("--encoding=utf8") || arg.equals("-u")) {
				charset = Charsets.UTF8;
				++charsetSet;
			} else if (arg.startsWith("--debug=")) {
				String flags = arg.substring(8);
				parseDebuggingFlags(flags);
			} else if (arg.equals("--help") && arg == args[0]) {
				System.out.println(usage(null));
			} else {
				throw new IllegalArgumentException("Invalid argument: " + arg);
			}
		}

		if (charsetSet > 1) {
			throw new IllegalArgumentException(
				"More than one encoding argument."
			);
		}
	}

	private void parseDebuggingFlags(String flags) {
		for (char f : flags.toCharArray()) {
			switch (f) {
			case 'i':
				debuggingFlags |= DebuggingFlags.LINE_HEX_INPUT;
				break;
			case 'g':
				debuggingFlags |= DebuggingFlags.LINE_LIST_HEX_INPUT;
				break;
			case '7':
				debuggingFlags |= DebuggingFlags.LINE_HEX_VALIDITY;
				break;
			case 'v':
				debuggingFlags |= DebuggingFlags.LINE_STRING_VALIDITY;
				break;
			case 'V':
				debuggingFlags |= DebuggingFlags.LINE_HEX_VALIDITY
								| DebuggingFlags.LINE_STRING_VALIDITY;
				break;
			case '6':
				debuggingFlags |= DebuggingFlags.LINE_HEX_VALIDITY_DETAILS;
				break;
			case 'd':
				debuggingFlags |= DebuggingFlags.LINE_STRING_VALIDITY_DETAILS;
				break;
			case 'D':
				debuggingFlags |= DebuggingFlags.LINE_HEX_VALIDITY_DETAILS
								| DebuggingFlags.LINE_STRING_VALIDITY_DETAILS;
				break;
			case 'n':
				debuggingFlags |= DebuggingFlags.LINE_NON_FORMATTED;
				break;
			case 'f':
				debuggingFlags |= DebuggingFlags.LINE_REJECTED;
				break;
			case 'e':
				debuggingFlags |= DebuggingFlags.LINE_LIST_ENCODING;
				break;
			case 's':
				debuggingFlags |= DebuggingFlags.LINE_LIST_ENCODING_VALIDITY;
				break;
			case 'r':
				debuggingFlags |=
					DebuggingFlags.LINE_LIST_ENCODING_VALIDITY_DETAILS;
				break;
			case 'x':
				debuggingFlags |= DebuggingFlags.LINE_LIST_ENCODING_REJECTED;
				break;
			default:
				throw new IllegalArgumentException(
					"Invalid debugging flag: " + f
				);
			}
		}
	}

	/**
	 * Defines how to use command line options.
	 *
	 * @param message
	 * 			The reason why the usage message is printed. May be null.
	 * @return The usage message.
	 */
	public String usage(String message) {
		StringBuilder usage = new StringBuilder();
		if(message != null && message.length() != 0) {
			usage.append(message);
			usage.append("\n\nPlease use the following options:\n\n");
		}

		URL currentURL =
			Main.class.getProtectionDomain().getCodeSource().getLocation();
		File f =
			new File(currentURL.getPath() + "../resources/usage_message.txt");

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = null;
			while ((line = br.readLine()) != null) {
				usage.append(line);
				usage.append("\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	 * Getter on the debugging flags currently activated.
	 * See {@link hextostring.debug.DebuggingFlags}.
	 *
	 * @return The debug level of the program.
	 */
	public long getDebuggingFlags() {
		return debuggingFlags;
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
