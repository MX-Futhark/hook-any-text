package hextostring.format;

/**
 * Represents a collection that formatted with decorations for later display
 *
 * @author Maxime PIA
 */
public interface DecorableList {

	public void setDecorationBefore(String decoration);

	public void setDecorationBetween(String decoration);

	public void setDecorationAfter(String decoration);

	public void setLinesDecorations(String before, String after);

}
