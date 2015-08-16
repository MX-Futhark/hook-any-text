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
	public static final String JAPANESE_CHARS_REGEX =
		"([a-zA-Z0-9#]|[ａ-ｚＡ-Ｚ０-９]|[\u3000-\u303F]|\u2048|\u2049|" +
		"[\u3040-\u309F]|[\u30A0-\u30FF]|[\uFF00-\uFFEF]|“|”|…|—|" +
		"[\u4E00-\u9FAF]|[\u2605-\u2606]|[\u2190-\u2195]|\u203B)+";

	// kana
	public static final String KANA_REGEX = "[\u3040-\u309F]|[\u30A0-\u30FF]";

	// valid Japanese punctuation
	public static final String[] JAPANESE_PUNCTUATION =
		{"”", "—", "。", "！", "？", "…", "、", "）", "】", "」", "』", "〜",
		 "\u2048", // one-character ?!
		 "\u2049"  // one-character !?
		};

	public static final int FINAL_PUNCTUATION_BONUS = 24;
	public static final int PUNCTUATION_BONUS = 8;

	public static final int NO_KANA_MALUS = 24;
	public static final int KANA_BONUS = 4;

	private int getInvalidCharactersCount(String jp) {
		String jpNoLineBreaks = jp.replace("\n", "");
		Matcher m =
			Pattern.compile(JAPANESE_CHARS_REGEX).matcher(jpNoLineBreaks);
		String match = "";
		while (m.find()) {
			match += m.group();
		}
		return jpNoLineBreaks.length() - match.length();
	}

	private int getNbKana(String jp) {
		String jpNoLineBreaks = jp.replace("\n", "");
		Matcher m = Pattern.compile(KANA_REGEX).matcher(jpNoLineBreaks);
		String match = "";
		while (m.find()) {
			match += m.group();
		}
		return match.length();
	}

	private boolean hasFinalPunctuation(String s) {
		if (s.length() == 0) return false;

		String lastChar = s.substring(s.length() - 1);
		for (String punctuation : JAPANESE_PUNCTUATION) {
			if (lastChar.matches(punctuation)) {
				return true;
			}
		}
		return false;
	}

	private int getNbPunctuation(String s) {
		int total = 0;

		for (int i = 0; i < s.length(); ++i) {
			for (String punctuation : JAPANESE_PUNCTUATION) {
				if (punctuation.equals(s.charAt(i))) {
					++total;
					break;
				}
			}
		}
		return total;
	}

	@Override
	public int evaluate(String s) {
		int nbKana = getNbKana(s);
		return -getInvalidCharactersCount(s) * INVALID_CHARS_MALUS
			+ getNbPunctuation(s) * PUNCTUATION_BONUS
			+ (hasFinalPunctuation(s) ? FINAL_PUNCTUATION_BONUS : 0)
			+ (nbKana == 0 ? - NO_KANA_MALUS : nbKana * KANA_BONUS);
	}

}
