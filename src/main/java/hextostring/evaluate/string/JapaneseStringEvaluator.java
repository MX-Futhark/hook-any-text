package hextostring.evaluate.string;

import hextostring.evaluate.EvaluationResult;

import java.util.LinkedList;
import java.util.List;
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
	public EvaluationResult evaluate(String s) {
		StringBuilder details = new StringBuilder();
		List<Integer> points = new LinkedList<>();

		int nbInvalidCharacters = getInvalidCharactersCount(s);
		int nbPunctuation = getNbPunctuation(s);
		boolean finalPunctuation = hasFinalPunctuation(s);
		int nbKana = getNbKana(s);

		details.append(nbInvalidCharacters);
		details.append(" invalid characters; applying malus of ");
		details.append(INVALID_CHARS_MALUS);
		details.append(" for every invalid character: ");
		points.add(-nbInvalidCharacters * INVALID_CHARS_MALUS);
		details.append(points.get(points.size() - 1));

		details.append("\n");

		details.append(nbPunctuation);
		details.append(" punctuation symbols; applying bonus of ");
		details.append(PUNCTUATION_BONUS);
		details.append(" for every punctuation symbol: +");
		points.add(nbPunctuation * PUNCTUATION_BONUS);
		details.append(points.get(points.size() - 1));

		details.append("\n");

		if (finalPunctuation) {
			details.append("No final puncutation; no bonus applied");
		} else {
			details.append("Final punctuation detected");
			details.append(" - applying malus of ");
			details.append(FINAL_PUNCTUATION_BONUS);
			details.append(" once: +");
			points.add(FINAL_PUNCTUATION_BONUS);
			details.append(points.get(points.size() - 1));
		}

		details.append("\n");

		details.append(nbKana);
		details.append(" kana(s); applying ");
		if (nbKana == 0) {
			details.append("malus of ");
			details.append(NO_KANA_MALUS);
			details.append(" once: -");
			points.add(-NO_KANA_MALUS);
		} else {
			details.append("bonus of ");
			details.append(KANA_BONUS);
			details.append(" for every kana: +");
			points.add(nbKana * KANA_BONUS);
		}
		details.append(points.get(points.size() - 1));

		int mark = 0;
		details.append("\nTotal: ");
		for (Integer point : points) {
			if(point >= 0) {
				details.append("+");
			}
			details.append(point);
			mark += point;
		}
		details.append("=");
		details.append(mark);

		return new EvaluationResult(mark, details.toString());
	}

}
