package hextostring.debug;

/**
 * Contains all the debugging flags.
 *
 * @author Maxime PIA
 */
public class DebuggingFlags {

	/**
	 * -d=i (_input)
	 * Hexadecimal versions of the converted lines
	 */
	public static final long LINE_HEX_INPUT = 1 << 0;
	/**
	 * -d=g (_global input)
	 * Global hexadecimal inputs
	 */
	public static final long LINE_LIST_HEX_INPUT = 1 << 1;



	/**
	 * -d=7 (validity = _76 61 6c 69 64 69 74 79)
	 * Included in -d=V and -d=6
	 * Validity mark of the the hexadecimal versions of the lines
	 */
	public static final long LINE_HEX_VALIDITY = 1 << 2;
	/**
	 * -d=v (_validity)
	 * Included in -d=V and -d=d
	 * Validity mark of the liens
	 */
	public static final long LINE_STRING_VALIDITY = 1 << 3;



	/**
	 * -d=6 (details = _64 65 74 61 69 6c 73)
	 * Included in -d=D
	 * Details on the validity mark of the the hexadecimal versions of the
	 * lines
	 */
	public static final long LINE_HEX_VALIDITY_DETAILS =
		(1 << 4) | LINE_HEX_VALIDITY;
	/**
	 * -d=d (_details)
	 * Included in -d=D
	 * Details on the validity mark of the lines
	 */
	public static final long LINE_STRING_VALIDITY_DETAILS =
		(1 << 5) | LINE_STRING_VALIDITY;



	/**
	 * -d=n (_non formatted)
	 * Non formatted version of the lines
	 */
	public static final long LINE_NON_FORMATTED = 1 << 6;



	/**
	 * -d=f (_filtered out)
	 * Lines rejected due to their validity
	 */
	public static final long LINE_REJECTED = 1 << 7;



	/**
	 * -d=e (_encoding)
	 * Encoding detected for the converted lines
	 * Valid only if encoding detection is on
	 */
	public static final long LINE_LIST_ENCODING = 1 << 8;
	/**
	 * -d=s (encoding _score)
	 * Validity mark of the encoding detected for the converted lines
	 * Valid only if encoding detection is on
	 */
	public static final long LINE_LIST_ENCODING_VALIDITY = 1 << 9;
	/**
	 * -d=r (_reason for the score)
	 * Details on the validity mark of the encoding detected for the converted
	 * lines
	 * Valid only if encoding detection is on
	 */
	public static final long LINE_LIST_ENCODING_VALIDITY_DETAILS =
		(1 << 10) | LINE_LIST_ENCODING_VALIDITY;
	/**
	 * -d=x (e_xcluded encodings)
	 * Lines converted with a rejected encoding
	 * Valid only if encoding detection is on
	 */
	public static final long LINE_LIST_ENCODING_REJECTED = 1 << 11;

}
