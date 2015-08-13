package hextostring.evaluate.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standard evaluator for Japanese strings.
 *
 * @author Maxime PIA
 */
public class JapaneseStringEvaluator extends ReadableStringEvaluator {

	// valid Japanese characters
	public static final String japaneseCharsRegex =
		"([a-zA-Z0-9#]|[ａ-ｚＡ-Ｚ０-９]|[\u3000-\u303F]|\u2048|\u2049|" +
		"[\u3040-\u309F]|[\u30A0-\u30FF]|[\uFF00-\uFFEF]|“|”|…|—|" +
		"[\u4E00-\u9FAF]|[\u2605-\u2606]|[\u2190-\u2195]|\u203B)+";

	// valid Japanese punctuation
	public static final String[] japanesePunctuation =
		{"”", "—", "。", "！", "？", "…", "、", "）", "】", "」", "』", "〜",
		 "\u2048", // one-character ?!
		 "\u2049"  // one-character !?
		};

	public static final int PUNCTUATION_BONUS = 24;

	private int getInvalidCharactersCount(String jp) {
		String jpNoLineBreaks = jp.replace("\n", "");
		Matcher m = Pattern.compile(japaneseCharsRegex).matcher(jpNoLineBreaks);
		String match = "";
		while (m.find()) {
			match += m.group();
		}
		return jpNoLineBreaks.length() - match.length();
	}

	@Override
	public int evaluate(String s) {
		return -getInvalidCharactersCount(s) * INVALID_CHARS_MALUS
			+ (hasPunctuation(s) ? PUNCTUATION_BONUS : 0);
	}

	private boolean hasPunctuation(String s) {
		if (s.length() == 0) return false;

		String lastChar = s.substring(s.length() - 1);
		for (String punctuation : japanesePunctuation) {
			if (lastChar.matches(punctuation)) {
				return true;
			}
		}
		return false;
	}

}
