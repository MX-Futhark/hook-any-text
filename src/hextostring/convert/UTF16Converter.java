package hextostring.convert;

import hextostring.utils.Charsets;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard converter for UTF-16-encoded hexadecimal strings.
 *
 * @author Maxime PIA
 */
public class UTF16Converter extends Converter {

	public UTF16Converter(boolean bigEndian) {
		super(bigEndian ? Charsets.UTF16_BE : Charsets.UTF16_LE);
	}

	@Override
	protected List<String> extractConvertibleChunks(String hex) {
		List<String> results = new LinkedList<>();
		Matcher m = Pattern.compile("([0-9a-f]{4})*?(0000|$)").matcher(hex);
		String match;
		while (m.find()) {
			match = m.group();
			if (match.endsWith("0000")) {
				match = match.substring(0, match.length() - 4);
			}
			if (!match.contains("ffff") && hex.indexOf(match) % 2 == 0
					&& match.length() > 0) {

				results.add(match);
			}
		}
		return results;
	}

}
