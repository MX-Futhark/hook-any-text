package hextostring.convert;

import hextostring.debug.DebuggableStrings;
import hextostring.replacement.Replacements;

/**
 * Converters transform a hexadecimal string into a readable string.
 *
 * @author Maxime PIA
 */
public interface Converter {

	DebuggableStrings convert(String hex);

	void setReplacements(Replacements r);

}
