package hextostring.debug;

import java.util.LinkedList;
import java.util.List;

import hextostring.format.DecorableList;

/**
 * Wraps all the necessary information to debug list of lines:
 *  - the original hex input
 *  - the debuggable lines themselves,
 *    see {@link hextostring.debug.DebuggableLine}
 *
 * @author Maxime PIA
 */
public class DebuggableLineList implements DebuggableStrings, DecorableList {

	private String hexInput;
	private String hexInputAfterHexReplacements;
	private String hexInputAfterStrReplacements;
	private List<DebuggableLine> lines = new LinkedList<>();

	private String decorationBefore = "";
	private String decorationBetween = "";
	private String decorationAfter = "";

	public DebuggableLineList(String hex) {
		this.hexInput = hex;
		this.hexInputAfterHexReplacements = hex;
		this.hexInputAfterStrReplacements = hex;
	}

	@Override
	public DebuggableLineList getDecorableList() {
		return this;
	}

	/**
	 * Adds a line to this group.
	 *
	 * @param line
	 * 			The line to add.
	 */
	public void addLine(DebuggableLine line) {
		lines.add(line);
	}

	/**
	 * Getter on the hex input from which these lines originate.
	 *
	 * @return The hex input from which these lines originate.
	 */
	public String getHexInput() {
		return hexInput;
	}

	/**
	 * Getter on the hex input from which this line originates to which HEX2HEX
	 * replacements were applied.
	 *
	 * @return The hex input from which this line originates to which HEX2HEX
	 * 		replacements were applied.
	 */
	public String getHexInputAfterHexReplacements() {
		return hexInputAfterHexReplacements;
	}

	/**
	 * Setter on the hex input from which this line originates to which HEX2HEX
	 * replacements were applied.
	 *
	 * @param hexInputAfterHexReplacements
	 * 			The hex input from which this line originates to which HEX2HEX
	 * 			replacements were applied.
	 */
	public void setHexInputAfterHexReplacements(
		String hexInputAfterHexReplacements) {

		this.hexInputAfterHexReplacements = hexInputAfterHexReplacements;
		setHexInputAfterStrReplacements(hexInputAfterHexReplacements);
	}

	/**
	 * Getter on the hex input from which this line originates to which HEX2HEX
	 * & HEX2STR replacements were applied.
	 *
	 * @return The hex input from which this line originates to which HEX2HEX
	 * 		& HEX2STR replacements were applied.
	 */
	public String getHexInputAfterStrReplacements() {
		return hexInputAfterStrReplacements;
	}

	/**
	 * Setter on the hex input from which this line originates to which HEX2HEX
	 * & HEX2STR replacements were applied.
	 *
	 * @param hexInputAfterStrReplacements
	 * 			The hex input from which this line originates to which HEX2HEX
	 * 			& HEX2STR replacements were applied.
	 */
	public void setHexInputAfterStrReplacements(
		String hexInputAfterStrReplacements) {

		this.hexInputAfterStrReplacements = hexInputAfterStrReplacements;
	}

	/**
	 * Getter on the lines of this group.
	 *
	 * @return The lines of this group.
	 */
	public List<DebuggableLine> getLines() {
		return lines;
	}

	/**
	 * Getter on one line of this group.
	 *
	 * @param index
	 * 			The index of the line in the list.
	 * @return The line in the list at the given index.
	 */
	public DebuggableLine getLine(int index) {
		return lines.get(index);
	}

	/**
	 * Setter on the string put before these lines in the toString method.
	 *
	 * @param decorationBefore
	 * 			The string put before these lines in the toString method.
	 */
	@Override
	public void setDecorationBefore(String decorationBefore) {
		this.decorationBefore = decorationBefore;
	}

	/**
	 * Setter on the string put between these lines in the toString method.
	 *
	 * @param decorationBetween
	 * 			The string put between these lines in the toString method.
	 */
	@Override
	public void setDecorationBetween(String decorationBetween) {
		this.decorationBetween = decorationBetween;
	}

	/**
	 * Setter on the string put after these lines in the toString method.
	 *
	 * @param decorationAfter
	 * 			The string put after these lines in the toString method.
	 */
	@Override
	public void setDecorationAfter(String decorationAfter) {
		this.decorationAfter = decorationAfter;
	}

	/**
	 * Setter on the strings decorating every line of this group.
	 *
	 * @param before
	 * 			The string put before every line in the toString method.
	 * @param after
	 * 			The string put after every line in the toString method.
	 */
	@Override
	public void setLinesDecorations(String before, String after) {
		for (DebuggableLine line : lines) {
			line.setDecorationBefore(before);
			line.setDecorationAfter(after);
		}
	}

	/**
	 * Getter on the sum of the validity of all converted strings in the list.
	 *
	 * @param filterUnder
	 * 			The threshold below which a line is not counted in the result.
	 * @return The sum of the validity of all converted strings in the list.
	 */
	public int getTotalValidity(int filterUnder) {
		int total = 0;
		for (DebuggableLine line : lines) {
			int validity = line.getValidity();
			if(validity >= filterUnder) {
				total += validity;
			}
		}
		return total;
	}

	@Override
	public String toString(long debuggingFlags, int converterStrictness) {
		StringBuilder sb = new StringBuilder();

		if ((debuggingFlags & DebuggingFlags.LINE_LIST_HEX_INPUT) > 0) {
			sb.append("input: 0x" + hexInput + "\n");
		}
		if ((debuggingFlags & DebuggingFlags.LINE_LIST_HEX_AFTER_HEX_REPL_INPUT)
			> 0) {

			sb.append("input after hex replacements: \n0x"
				+ hexInputAfterHexReplacements + "\n");
		}
		if ((debuggingFlags
			& DebuggingFlags.LINE_LIST_HEX_AFTER_STR_REPL_INPUT)
			> 0) {

			sb.append("input after str replacements: \n0x"
				+ hexInputAfterStrReplacements + "\n");
		}

		List<DebuggableLine> displayedLines;
		if ((debuggingFlags & DebuggingFlags.LINE_REJECTED) > 0) {
			displayedLines = lines;
		} else {
			displayedLines = new LinkedList<>();
			for (DebuggableLine line : lines) {
				if (line.getValidity() >= converterStrictness) {
					displayedLines.add(line);
				}
			}
		}

		sb.append(decorationBefore);
		if (debuggingFlags > 0) {
			sb.append("\n");
		}
		if (displayedLines.size() > 0) {
			DebuggableLine lastDisplayedLine = displayedLines
				.get(displayedLines.size() - 1);
			for (DebuggableLine line : displayedLines) {
				sb.append(line.toString(debuggingFlags));
				if (line != lastDisplayedLine) {
					sb.append(decorationBetween);
				}
				if (debuggingFlags > 0) {
					sb.append("\n");
				}
			}
		}
		sb.append(decorationAfter);

		if (debuggingFlags > 0) {
			sb.append("\n");
		}

		return sb.toString().trim();
	}

}
