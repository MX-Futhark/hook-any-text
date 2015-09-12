package hextostring.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

/**
 * Helper to put strings in the clipboard.
 *
 * @author Maxime PIA
 */
public class Clipboard {

	public static final java.awt.datatransfer.Clipboard cb =
		Toolkit.getDefaultToolkit().getSystemClipboard();

	/**
	 * Sets a string in the clipboard.
	 *
	 * @param s
	 * 			The string to set.
	 */
	public static void set(String s) {
		StringSelection select = new StringSelection(s);
		cb.setContents(select, select);
	}

}
