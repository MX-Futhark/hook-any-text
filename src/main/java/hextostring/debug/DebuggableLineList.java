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
public class DebuggableLineList implements DebuggableStrings {

	private String hexInput;
	private List<DebuggableLine> lines = new LinkedList<>();

	private String decorationBefore = "";
	private String decorationBetween = "";
	private String decorationAfter = "";

	public DebuggableLineList(String hex) {
		this.hexInput = hex;
	}

	public DebuggableLineList getValidLineList() {
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
		for (DebuggableLine line : lines) {
			line.setDecorationBefore(before);
			line.setDecorationAfter(after);
		}
	}

	/**
	 * Getter on the sum of the validity of all non-converted strings in the
	 * list.
	 *
	 * @param filterUnder
	 * 			The threshold below which a line is not counted in the result.
	 * @return The sum of the validity of all non-converted strings in the list.
	 */
	public int getTotalHexValidity(int filterUnder) {
		return getTotalValidity(true, filterUnder);
	}

	/**
	 * Getter on the sum of the validity of all converted strings in the list.
	 *
	 * @param filterUnder
	 * 			The threshold below which a line is not counted in the result.
	 * @return The sum of the validity of all converted strings in the list.
	 */
	public int getTotalReadableStringValidity(int filterUnder) {
		return getTotalValidity(false, filterUnder);
	}

	private int getTotalValidity(boolean hex, int filterUnder) {
		int total = 0;
		for (DebuggableLine line : lines) {
			int hexValidity = line.getHexEvaluationResult().getMark();
			int readableStringValidity =
				line.getReadableStringEvaluationResult().getMark();

			if((hex ? hexValidity : readableStringValidity)
					>= filterUnder) {

				total += hex
					? hexValidity
					: readableStringValidity;
			}
		}
		return total;
	}

	/**
	 * Getter on the sum of the validity of all lines in the list.
	 *
 	 * @param filterUnder
	 * 			The threshold below which a line is excluded from the total.
	 * @return The sum of the validity of all lines in the list.
	 */
	public int getTotalValidity(int filterUnder) {
		return getTotalHexValidity(filterUnder)
			+ getTotalReadableStringValidity(filterUnder);
	}

	@Override
	public String toString(long debuggingFlags, int converterStrictness) {
		StringBuilder sb = new StringBuilder();

		if ((debuggingFlags & DebuggingFlags.LINE_LIST_HEX_INPUT) > 0) {
			sb.append("input: 0x" + hexInput + "\n");
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
