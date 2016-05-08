package hextostring.replacement;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hextostring.utils.Hex;

/**
 * Replaces hexadecimal sequences and patterns by readable characters between
 * minus signs. A string in such an intermediary state will be called an
 * transitory string, or mixed string.
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
		List<String> parts = splitParts(s);
		StringBuilder noHexString = new StringBuilder();
		boolean hexPart = true;
		for (String part : parts) {
			if (hexPart) {
				noHexString.append(Hex.convertToString(part, cs));
			} else {
				noHexString.append(part.replace("\\-", "-"));
			}
			hexPart = !hexPart;
		}
		return noHexString.toString();
	}

	/**
	 * Split a transitory string into hex parts and readable parts, in the
	 * order they were found. The first element of the result in always
	 * a hex part, empty if necessary. Note that the minuses are removed.
	 *
	 * @param s
	 * 			The string to be split up.
	 * @return The parts of the split up string.
	 */
	public static List<String> splitParts(String s) {
		// HACK: the final "-" ensures that all of s used
		// in the while loop below.
		s = s + "-";
		Matcher m = Pattern.compile("(.*?)-").matcher(s);
		LinkedList<String> parts = new LinkedList<>();
		int previousEnd = 0;
		while (m.find()) {
			String part = s.substring(previousEnd, m.end());
			if (part.endsWith("\\-")) continue;

			part = part.substring(0, part.length() - 1);
			parts.add(part);
			previousEnd = m.end();
		}
		return parts;
	}

}
