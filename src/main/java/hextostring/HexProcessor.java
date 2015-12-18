package hextostring;

import hextostring.convert.Converter;
import hextostring.convert.ConverterFactory;
import hextostring.debug.DebuggableStrings;
import hextostring.format.Formatter;
import hextostring.format.FormatterFactory;
import hextostring.utils.Clipboard;
import main.history.History;

/**
 * Full conversion chain for an arbitrary number of input strings.
 *
 * @author Maxime PIA
 */
public class HexProcessor {

	// Result of the previous conversion.
	// This field is used to avoid flooding the clipboard.
	private String previousResult = "";

	private History<String, String> history;
	private ConvertOptions opts;

	private Formatter formatter = FormatterFactory.getFormatterInstance(false);

	public HexProcessor(ConvertOptions opts, History<String, String> history) {
		this.history = history;
		this.opts = opts;
	}

	/**
	 * Converts an hexadecimal string into a readable string.
	 *
	 * @param hex
	 * 			The hexadecimal string.
	 * @param forceUpdate
	 * 			True if to the current value even if it is not warranted.
	 * @return The converted string.
	 */
	public synchronized String convert(String hex, boolean forceUpdate) {
		Converter converter = ConverterFactory.getConverterInstance(
			opts.getCharset(),
			opts.getReplacements()
		);
		DebuggableStrings ds = converter.convert(hex);
		formatter.format(ds.getValidLineList());
		String result = ds.toString(
			opts.getDebuggingFlags(),
			opts.getStrictness()
		);
		// avoid unnecessarily updating the clipboard
		if ((!result.equals(previousResult) && result.length() > 0)
			|| forceUpdate) {

			history.add(hex, result);
			if (opts.isAutocopy()) {
				Clipboard.set(result);
			}
			previousResult = result;

			return result;
		}

		return null;
	}

}
