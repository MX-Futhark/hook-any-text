package hextostring.replacement;

/**
 * Represents the respective types of a sequence and its replacement in a
 * Replacement object.
 *
 * There are to such type : hexadecimal (HEX) and readable (STR).
 *
 * For example, a HEX2STR replacement replaces sequences in an hexadecimal
 * string by readable characters.
 *
 * @author Maxime PIA
 */
public enum ReplacementType {
	HEX2HEX, HEX2STR, STR2STR
}
