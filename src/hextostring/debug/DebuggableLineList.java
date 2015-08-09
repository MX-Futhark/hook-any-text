package hextostring.debug;

import java.util.LinkedList;
import java.util.List;

/**
 * Wraps all the necessary information to debug list of lines:
 *  - the original hex input
 *  - the debuggable lines themselves,
 *    see {@link hextostring.debug.DebuggableLine}
 *
 * @author Maxime PIA
 */
public class DebuggableLineList {

	private String hexInput;
	private List<DebuggableLine> lines = new LinkedList<>();

	private String decorationBefore = "";
	private String decorationBetween = "";
	private String decorationAfter = "";

	public DebuggableLineList(String hex) {
		this.hexInput = hex;
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
	public void setDecorationBefore(String decorationBefore) {
		this.decorationBefore = decorationBefore;
	}

	/**
	 * Setter on the string put between these lines in the toString method.
	 *
	 * @param decorationBetween
	 * 			The string put between these lines in the toString method.
	 */
	public void setDecorationBetween(String decorationBetween) {
		this.decorationBetween = decorationBetween;
	}

	/**
	 * Setter on the string put after these lines in the toString method.
	 *
	 * @param decorationAfter
	 * 			The string put after these lines in the toString method.
	 */
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
	public void setLinesDecorations(String before, String after) {
		for (DebuggableLine chunk : lines) {
			chunk.setDecorationBefore(before);
			chunk.setDecorationAfter(after);
		}
	}

	/**
	 * Formats these lines depending on the debugging level.
	 *
	 * @param debugLevel
	 * 			The debug level used to format these lines.
	 * @return a string representing these lines, with or without debug traces.
	 */
	public String toString(int debugLevel, int converterStrictness) {
		StringBuilder sb = new StringBuilder();

		if (debugLevel >= 3) {
			sb.append("input: " + hexInput + "\n");
		}

		List<DebuggableLine> displayedLines;
		if (debugLevel >= 4) {
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
		if (debugLevel >= 1) {
			sb.append("\n");
		}
		if(displayedLines.size() > 0) {
			DebuggableLine lastDisplayedLine = displayedLines
				.get(displayedLines.size() - 1);
			for (DebuggableLine line : displayedLines) {
				sb.append(line.toString(debugLevel));
				if (line != lastDisplayedLine) {
					sb.append(decorationBetween);
				}
				if (debugLevel >= 1) {
					sb.append("\n");
				}
			}
		}
		sb.append(decorationAfter);

		return sb.toString();
	}

}
