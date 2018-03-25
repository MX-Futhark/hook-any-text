package main.utils;

import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Utility methods for strings.
 *
 * @author Maxime PIA
 */
public class StringUtils {

	/**
	 * Indents a string.
	 *
	 * @param s
	 * 			The string to indent.
	 * @param tab
	 * 			The string used as an indentation.
	 * @param nbTabs
	 * 			The number of indentations.
	 * @return An indented string.
	 */
	public static String indent(String s, String tab, int nbTabs) {
		Scanner scanner = new Scanner(s);
		StringBuilder tabsBuilder = new StringBuilder();
		for (int i = 0; i < nbTabs; ++i) {
			tabsBuilder.append(tab);
		}
		String tabs = tabsBuilder.toString();

		StringBuilder indentedString = new StringBuilder();
		while (scanner.hasNextLine()) {
			indentedString.append(tabs);
			indentedString.append(scanner.nextLine());
			if (scanner.hasNextLine()) {
				indentedString.append("\n");
			}
		}

		scanner.close();

		return indentedString.toString();
	}

	/**
	 * Translates "A_STRING" into "a string"
	 * @param name
	 * 			THE_STRING
	 * @return "the string"
	 */
	public static String screamingSnakeToWords(String name) {
		return name.replace("_", " ").toLowerCase();
	}

	/**
	 * Translates "aString" into "a string"
	 * @param name
	 * 			theString
	 * @return "the string"
	 */
	public static String camelToWords(String name) {
		return name.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase();
	}

	/**
	 * Translates "a string" into "AString"
	 * @param name
	 * 			"the string"
	 * @return TheString
	 */
	public static String wordsToScreamingCamel(String words) {
		String[] splitWords = words.split(" ");
		StringBuffer camel = new StringBuffer();

		for (int i = 0; i < splitWords.length; i++) {
			camel.append(Character.toUpperCase(splitWords[i].charAt(0)))
				.append(splitWords[i].substring(1));
		}
		return camel.toString();
	}

	/**
	 * Translates "a string" into "A_STRING"
	 * @param name
	 * 			"the string"
	 * @return THE_STRING
	 */
	public static String wordsToScreamingSnake(String words) {
		return words.replace(" ", "_").toUpperCase();
	}

	/**
	 * Translates "aString" into "A_STRING"
	 * @param name
	 * 			theString
	 * @return THE_STRING
	 */
	public static String camelToScreamingSnake(String name) {
		return wordsToScreamingSnake(camelToWords(name));
	}

	public static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	/**
	 * Translates HTML into plain text.
	 *
	 * @param HTML
	 * 			The HTML.
	 * @return The plain text version of the HTML.
	 */
	public static String HTMLToPlainText(String HTML) {
		return HTML
			.replaceAll("<br>", "\n")
			.replaceAll("<.*?>", "")
			.replace("&nbsp;&nbsp;", "\t");
	}

	/**
	 * Translates plain text into HTML.
	 *
	 * @param HTML
	 * 			The plain text.
	 * @return The HTML version of the plain text.
	 */
	public static String plainTextToHTML(String txt) {
		return "<html>" + txt
			.replace("\n", "<br>")
			.replace("\t", "&nbsp;&nbsp;") + "</html>";
	}

	/**
	 * Breaks a line of text into several lines without considering separators.
	 *
	 * @param txt
	 * 			The text to break.
	 * @param maxLineLength
	 * 			The maximum number of characters in a single line.
	 * @return The broken text.
	 */
	public static String breakNoSpaceText(String txt, int maxLineLength) {
		StringBuilder brokenString = new StringBuilder();
		int charCount = 0;
		while (charCount < txt.length()) {
			int nextLineBreakInd = txt.substring(charCount).indexOf('\n');
			int nextCharInd, prevCharCount = charCount;
			if (nextLineBreakInd == -1 || nextLineBreakInd > maxLineLength) {
				nextCharInd = Math.min(charCount + maxLineLength, txt.length());
				charCount += maxLineLength;
			} else {
				nextCharInd =
					Math.min(charCount + nextLineBreakInd, txt.length());
				charCount += nextLineBreakInd + 1;
			}
			brokenString.append(txt.substring(prevCharCount, nextCharInd));
			brokenString.append("\n");
		}
		return brokenString.toString();
	}

	/**
	 * Breaks a line of text into several lines using existing separators.
	 *
	 * @param txt
	 * 			The text to break.
	 * @param maxLineLength
	 * 			The maximum number of characters in a single line.
	 * @return The broken text.
	 */
	public static String breakText(String txt, int maxLineLength) {
		StringTokenizer st = new StringTokenizer(txt, " ");
		StringBuilder brokenString = new StringBuilder(txt.length());
		int lineLength = 0;
		while (st.hasMoreTokens()) {
			String word = st.nextToken();

			boolean nextLine = lineLength + word.length() > maxLineLength;
			if (!nextLine && lineLength > 0) {
				brokenString.append(" ");
			}
			if (nextLine) {
				brokenString.append("\n");
				lineLength = 0;
			}
			brokenString.append(word);
			lineLength += word.length() + 1;
			if (word.contains("\n")) {
				brokenString.append(" ");
				lineLength = 0;
			}
		}
		return brokenString.toString();
	}

	/**
	 * Stuffs the end of a string with spaces.
	 *
	 * @param s
	 * 			The string to stuff.
	 * @param targetSize
	 * 			The end size of the string.
	 * @return The stuffed string.
	 */
	public static String fillWithSpaces(String s, int targetSize) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < targetSize - s.length(); ++i) {
			sb.append(" ");
		}
		return sb.toString().substring(0, targetSize);
	}

	/**
	 * Puts paragraphs side-by-side.
	 *
	 * @param paragraphs
	 * 			The paragraphs.
	 * @param widths
	 * 			The maximum width of a line in each paragraph.
	 * @param separator
	 * 			The separator to use between the lines of the paragraphs.
	 * @return The paragraphs side-by-side.
	 */
	public static String putParagraphsSideBySide(String[] paragraphs,
		int[] widths, String separator) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < paragraphs.length; ++i) {
			if (paragraphs[i] == null) {
				paragraphs[i] = "";
			} else {
				paragraphs[i] = breakText(paragraphs[i], widths[i]);
			}
		}
		boolean addedLine;
		do {
			StringBuilder newLine = new StringBuilder();
			addedLine = false;
			boolean addedCell = false;
			for (int i = 0; i < paragraphs.length; ++i) {
				if (paragraphs[i].length() > 0) {
					addedCell = true;
				}
				int lineEndIndex = paragraphs[i].indexOf("\n");
				if (lineEndIndex == -1) {
					lineEndIndex = paragraphs[i].length();
				}
				String line = paragraphs[i].substring(0, lineEndIndex);
				if (!line.equals(paragraphs[i])) {
					paragraphs[i] =
						paragraphs[i].substring(lineEndIndex + 1);
				} else {
					paragraphs[i] = "";
				}
				newLine.append(fillWithSpaces(line, widths[i]));
				if (i < paragraphs.length - 1) {
					newLine.append(separator);
				}
			}
			if (addedCell) {
				addedLine = true;
				sb.append(newLine);
				sb.append("\n");
			}
		} while (addedLine);

		return sb.toString();
	}

}
