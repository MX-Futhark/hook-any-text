package hextostring.convert;

import hextostring.utils.Charsets;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard converter for UTF-8-encoded hexadecimal strings.
 *
 * @author Maxime PIA
 */
public class UTF8Converter extends Converter {

	public UTF8Converter() {
		super(Charsets.UTF8);
	}

	@Override
	protected List<String> extractConvertibleChunks(String hex) {
		List<String> results = new LinkedList<>();
		Matcher m = Pattern.compile(
			"(" +
			// U+0000 to U+007F: 0xxxxxxx
			"[0-7][0-9a-f]|" +
			// U+0080 to U+07FF: 110xxxxx 10xxxxxx
			"(c[0-2]|d[0-9a-f])[8-9a-b][0-9a-f]|" +
			// U+0800 to U+0FFF: 11100000 101xxxxx 10xxxxxx
			"e0[a-b][0-9a-f][8-9a-b][0-9a-f]|" +
			// U+1000 to U+1FFF: 11100001 10xxxxxx 10xxxxxx
			"e1([8-9a-b][0-9a-f]){2}|" +
			// U+2000 to U+3FFF: 1110001x 10xxxxxx 10xxxxxx
			"e[2-3]([8-9a-b][0-9a-f]){2}|" +
			// U+4000 to U+7FFF: 111001xx 10xxxxxx 10xxxxxx
			"e[4-7]([8-9a-b][0-9a-f]){2}|" +
			// U+8000 to U+BFFF: 111010xx 10xxxxxx 10xxxxxx
			"e[8-9a-b]([8-9a-b][0-9a-f]){2}|" +
			// U+C000 to U+CFFF: 11101100 10xxxxxx 10xxxxxx
			"ec([8-9a-b][0-9a-f]){2}|" +
			// U+D000 to U+D7FF: 11101101 100xxxxx 10xxxxxx
			"ed[8-9][0-9a-f][8-9a-b][0-9a-f]|" +
			// U+E000 to U+FFFF: 1110111x 10xxxxxx 10xxxxxx
			"e[e-f]([8-9a-b][0-9a-f]){2}|" +
			// U+10000 to U+1FFFF: 11110000 10(01|10|11)xxxx 10xxxxxx 10xxxxxx
			"f0[9a-b][0-9a-f]([8-9a-b][0-9a-f]){2}|" +
			// U+40000 to U+7FFFF: 11110001 10xxxxxx 10xxxxxx 10xxxxxx
			"f1([8-9a-b][0-9a-f]){3}|" +
			// U+80000 to U+FFFFF: 1111001x 10xxxxxx 10xxxxxx 10xxxxxx
			"f[2-3]([8-9a-b][0-9a-f]){3}|" +
			// U+100000 to U+10FFFF: 11110100 1000xxxx 10xxxxxx 10xxxxxx
			"f48[0-9a-f]([8-9a-b][0-9a-f]){2}|" +
			")+?(0000|$)"
		).matcher(hex);
		String match;
		while (m.find()) {
			match = m.group();
			if (match.endsWith("0000")) {
				match = match.substring(0, match.length() - 4);
			}
			if (!match.contains("ffff") && hex.indexOf(match) % 2 == 0) {

				results.add(match);
			}
		}
		return results;
	}

}
