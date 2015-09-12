package hextostring.convert;

import hextostring.debug.DebuggableStrings;

/**
 * Converters transform a hexadecimal string into a readable string.
 *
 * @author Maxime PIA
 */
public interface Converter {

	DebuggableStrings convert(String hex);

}
