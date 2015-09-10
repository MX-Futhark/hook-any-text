package hextostring.utils;

import java.util.Scanner;

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

}
