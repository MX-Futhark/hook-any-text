package hextostring.format;

import hextostring.debug.DebuggableLineList;

/**
 * Abstract formatter.
 * Formatters add the final details to conversion results.
 *
 * @author Maxime PIA
 */
public abstract class Formatter {

	private String linesDecorationBefore;
	private String linesDecorationBetween;
	private String linesDecorationAfter;
	private String lineDecorationBefore;
	private String lineDecorationAfter;

	public Formatter(String linesDecorationBefore,
		String linesDecorationBetween, String linesDecorationAfter,
		String lineDecorationBefore, String lineDecorationAfter) {

		this.linesDecorationBefore = linesDecorationBefore;
		this.linesDecorationBetween = linesDecorationBetween;
		this.linesDecorationAfter = linesDecorationAfter;
		this.lineDecorationBefore = lineDecorationBefore;
		this.lineDecorationAfter = lineDecorationAfter;
	}

	/**
	 * Formats the result of a conversion.
	 *
	 * @param lines
	 * 			A List of lines.
	 */
	public void format(DebuggableLineList lines) {
		decorateLines(lines);
	}

	private void decorateLines(DebuggableLineList lines) {
		lines.setDecorationBefore(linesDecorationBefore);
		lines.setDecorationBetween(linesDecorationBetween);
		lines.setDecorationAfter(linesDecorationAfter);
		lines.setLinesDecorations(lineDecorationBefore, lineDecorationAfter);
	}

}
