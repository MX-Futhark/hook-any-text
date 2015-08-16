package hextostring.debug;

import java.nio.charset.Charset;
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

	private Charset charset;
	private boolean charsetAutoDetected = false;

	private String hexInput;
	private List<DebuggableLine> lines = new LinkedList<>();

	private String decorationBefore = "";
	private String decorationBetween = "";
	private String decorationAfter = "";

	public DebuggableLineList(String hex) {
		this.hexInput = hex;
	}

	public DebuggableLineList(String hex, Charset charset) {
		this(hex);
		this.charset = charset;
	}

	/**
	 * Setter on the charset used for this list of lines.
	 *
	 * @param charset
	 * 			The charset used for this list of lines.
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * Setter on the boolean determining whether or not the charset was
	 * detected for this list of lines.
	 *
	 * @param charsetAutoDetected
	 * 			Whether or not the charset was detected for this list of lines.
	 */
	public void setCharsetAutoDetected(boolean charsetAutoDetected) {
		this.charsetAutoDetected = charsetAutoDetected;
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
			if((hex ? line.getHexValidity() : line.getReadableStringValidity())
					>= filterUnder) {

				total += hex
					? line.getHexValidity()
					: line.getReadableStringValidity();
			}
		}
		return total;
	}

	/**
	 * Getter on the sum of the validity of all lines in the list.
	 *
 	 * @param filterUnder
	 * 			The threshold below which a line is not counted in the result.
	 * @return The sum of the validity of all lines in the list.
	 */
	public int getTotalValidity(int filterUnder) {
		return getTotalHexValidity(filterUnder)
			+ getTotalReadableStringValidity(filterUnder);
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
		if (debugLevel >= 1 && charsetAutoDetected) {
			sb.append("Detected: " + charset + "\n");
		}

		List<DebuggableLine> displayedLines;
		if (debugLevel >= 4) {
			displayedLines = lines;
		} else {
			displayedLines = new LinkedList<>();
			for (DebuggableLine line : lines) {
				if (line.getValidity() >= converterStrictness
						&& charset == line.getCharset()) {

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
