package hextostring;

import java.util.LinkedList;
import java.util.List;

import hexcapture.HexSelectionsContentSnapshot;
import hextostring.convert.Converter;
import hextostring.convert.ConverterFactory;
import hextostring.debug.DebuggableHexSelectionsContent;
import hextostring.debug.DebuggableStrings;
import hextostring.format.Formatter;
import hextostring.format.FormatterFactory;
import hextostring.history.History;
import hextostring.utils.Clipboard;

/**
 * Full conversion chain for an arbitrary number of input strings.
 *
 * @author Maxime PIA
 */
public class HexProcessor {

	// Result of the previous conversion.
	// This field is used to avoid flooding the clipboard.
	private String previousResult = "";

	private History history;
	private ConvertOptions opts;

	private Formatter standardFormatter =
		FormatterFactory.getFormatterInstance(FormatterFactory.STANDARD);
	private Formatter multiContentFormatter =
		FormatterFactory.getFormatterInstance(FormatterFactory.MULTI_CONTENT);

	public HexProcessor(ConvertOptions opts, History history) {
		this.history = history;
		this.opts = opts;
	}

	/**
	 * Converts an hexadecimal string into a readable string.
	 *
	 * @param selectionsContent
	 * 			An object mapping hex selections to the hex values they contain
	 * 			and that must be updated.
	 * @param forceUpdate
	 * 			True to update the current value even if it is not warranted.
	 * @return The converted string.
	 */
	public synchronized String convert(
		HexSelectionsContentSnapshot selectionsContent, boolean forceUpdate) {

		Converter converter = ConverterFactory.getConverterInstance(
			opts.getCharset(),
			opts.getReplacements()
		);
		List<DebuggableStrings> conversionResults = new LinkedList<>();

		for (int i = 0; i < selectionsContent.getSize(); ++i) {
			String content = selectionsContent.getValueAt(i);
			if (content != null && !content.isEmpty()) {
				DebuggableStrings ds = converter.convert(content);
				standardFormatter.format(ds.getDecorableList());
				conversionResults.add(ds);
			} else {
				conversionResults.add(null);
			}
		}
		DebuggableHexSelectionsContent dhsc =
			new DebuggableHexSelectionsContent(
				selectionsContent,
				conversionResults
			);
		multiContentFormatter.format(dhsc.getDecorableList());
		String result = dhsc.toString(
			opts.getDebuggingFlags(),
			opts.getStrictness()
		);

		// avoid unnecessarily updating the clipboard
		if ((!result.equals(previousResult) && result.length() > 0)
			|| forceUpdate) {

			history.add(selectionsContent, result);
			if (opts.isAutocopy()) {
				Clipboard.set(result);
			}
			previousResult = result;

			return result;
		}

		return null;
	}

}
