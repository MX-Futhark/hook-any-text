package hextostring.utils;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Enables printing indented strings.
 *
 * @author Maxime PIA
 */
public class IndentablePrintStream {

	private PrintStream out;
	private String tab;

	public IndentablePrintStream() {
		this(System.out, "\t");
	}

	public IndentablePrintStream(PrintStream out, String tab) {
		this.out = out;
		this.tab = tab;
	}

	private String indent(String s, int nbTabs) {
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
	 * Prints a string.
	 *
	 * @param s
	 * 			The string to print.
	 */
	public void print(String s) {
		out.print(s);
	}

	/**
	 * Prints a string and a new line.
	 *
	 * @param s
	 * 			The string to print.
	 */
	public void println(String s) {
		out.println(s);
	}

	/**
	 * Prints an indented string.
	 *
	 * @param s
	 * 			The string to print.
	 * @param indentLevel
	 * 			The number of indentations to add.
	 */
	public void print(String s, int indentLevel) {
		out.print(indent(s, indentLevel));
	}

	/**
	 * Prints an indented string and a new line.
	 *
	 * @param s
	 * 			The string to print.
	 * @param indentLevel
	 * 			The number of indentations to add.
	 */
	public void println(String s, int indentLevel) {
		out.println(indent(s, indentLevel));
	}

}
