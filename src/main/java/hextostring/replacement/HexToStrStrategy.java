package hextostring.replacement;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hextostring.utils.Hex;

/**
 * Replaces hexadecimal sequences and patterns by readable characters between
 * minus signs. A string in such an intermediary state will be called an
 * transitory string.
 *
 * @author Maxime PIA
 */
public class HexToStrStrategy extends ReplacementStrategy {

	@Override
	public String replacePattern(String s, String pattern, String replacement) {
		// captured patterns will be converted
		return s.replaceAll(pattern, "-" + replacement.replace("-", "\\-")
			// isolates group references not preceded by an antislash
			.replaceAll("(?<!\\\\)(\\$[0-9]*)", "-$1-") + "-");
	}

	@Override
	public String replaceSequence(String s, String sequence,
		String replacement) {

		return s.replace(sequence, "-" + replacement.replace("-", "\\-") + "-");
	}

	/**
	 * Converts a transitory string into a completely readable string.
	 *
	 * @param s
	 * 			The string to convert.
	 * @param cs
	 * 			The charset used to convert s.
	 * @return A completely readable string.
	 */
	public static String toReadableString(String s, Charset cs) {
		// HACK: the final "-" ensures that all of s will be converted
		// in the while loop below.
		s = s + "-";
		Matcher m = Pattern.compile("(.*?)-").matcher(s);
		StringBuilder noHexString = new StringBuilder();
		int previousEnd = 0;
		boolean hexPart = true;
		while (m.find()) {
			String part = s.substring(previousEnd, m.end());
			if (part.endsWith("\\-")) continue;

			part = part.substring(0, part.length() - 1);
			if (hexPart) {
				noHexString.append(Hex.convertToString(part, cs));
			} else {
				noHexString.append(part);
			}
			previousEnd = m.end();
			hexPart = !hexPart;
		}
		return noHexString.toString().replace("\\-", "-");
	}

}
