package hextostring;

import hextostring.convert.Converter;
import hextostring.convert.ConverterFactory;
import hextostring.debug.DebuggableStrings;
import hextostring.format.Formatter;
import hextostring.format.FormatterFactory;
import hextostring.utils.Clipboard;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Full conversion chain for an arbitrary number of input strings.
 *
 * @author Maxime PIA
 */
public class HexProcessor {

	// Result of the previous conversion.
	// This field is used to avoid flooding the clipboard.
	private String previousResult = "";

	private void print(String message, PrintStream out) {
		if (out != null) {
			out.println(message);
		}
	}

	/**
	 * Starts a conversion session.
	 * Hex strings are read from System.in, and may be piped into the program.
	 * Writing the string "exit" will cause the program to exit.
	 *
	 * @param opts
	 * 			Options for the session.
	 * @param out
	 * 			A stream to display converted strings and error messages.
	 * 			May be null.
	 */
	public void start(Options opts, PrintStream out) {
		Scanner sc = new Scanner(System.in);
		String hex, result;

		Converter converter =
			ConverterFactory.getConverterInstance(opts.getCharset());
		Formatter formatter = FormatterFactory.getFormatterInstance(false);

		while (true) {
			try {
				hex = sc.next();
				if (hex.contains("exit")) {
					break;
				}

				DebuggableStrings dInput = converter.convert(hex);
				formatter.format(dInput.getValidLineList());
				result = dInput.toString(
					opts.getDebuggingFlags(),
					opts.getStrictness()
				);

				// avoid unnecessarily updating the clipboard
				if (!result.equals(previousResult)) {
					if(opts.getDebuggingFlags() > 0) {
						print(result, out);
					}
					Clipboard.set(result);
					previousResult = result;
				}
			} catch (Exception e) {
				if (opts.getDebuggingFlags() > 0) {
					e.printStackTrace();
				} else {
					print(e.getMessage(), out);
				}
			}
		}

		sc.close();
	}

}
