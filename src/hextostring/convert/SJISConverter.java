package hextostring.convert;

import hextostring.utils.Charsets;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard converter for Shift-JIS-encoded hexadecimal strings.
 *
 * @author Maxime PIA
 */
public class SJISConverter extends Converter {

	public SJISConverter() {
		super(Charsets.SHIFT_JIS);
	}

	@Override
	protected List<String> extractConvertibleChunks(String hex) {
		List<String> results = new LinkedList<>();
		// Keeps everything that looks like Shift-JIS,
		// with some margin for non-standard characters.
		Matcher m = Pattern.compile(
			"(8[a-f1-9]|9[a-f0-9]|23)([a-f1-9][a-f0-9]|0[a-f1-9])+(00|$)")
			.matcher(hex);
		String match;
		while (m.find()) {
			match = m.group();
			if (match.endsWith("00")) {
				if (match.length() % 2 == 0) {
					match = match.substring(0, match.length() - 2);
				} else {
					match = match.substring(0, match.length() - 1);
				}
			}
			if (!match.contains("ffff") && hex.indexOf(match) % 2 == 0) {
				results.add(match);
			}
		}
		return results;
	}

}
