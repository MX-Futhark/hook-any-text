package hextostring.debug;

/**
 * Wraps all the necessary information to debug a line:
 *  - the original hex string for the line and its validity
 *  - the result of the conversion of said hex string and its validity
 *  - the same string with all native formatting information processed
 *
 * @author Maxime PIA
 */
public class DebuggableLine {

	private String hex;
	private String readableString;
	private String formattedString;

	private int hexValidity;
	private int readableStringValidity;

	private String decorationBefore = "";
	private String decorationAfter = "";

	public DebuggableLine(String hex) {
		this.hex = hex;
	}

	/**
	 * Getter on the hex chunk from which this line originates.
	 *
	 * @return The hex chunk from which this line originates.
	 */
	public String getHex() {
		return hex;
	}

	/**
	 * Getter on the non-formatted string.
	 *
	 * @return The non-formatted string.
	 */
	public String getReadableString() {
		return readableString;
	}

	/**
	 * Setter on the non-formatted string.
	 *
	 * @param readableString
	 * 			The new non-formatted string.
	 */
	public void setReadableString(String readableString) {
		this.readableString = readableString;
	}

	/**
	 * Getter on the formatted string.
	 *
	 * @return The formatted string.
	 */
	public String getFormattedString() {
		return formattedString;
	}

	/**
	 * Setter on the formatted string.
	 *
	 * @param formattedString
	 * 			The new formatted string.
	 */
	public void setFormattedString(String formattedString) {
		this.formattedString = formattedString;
	}

	/**
	 * Getter on the validity of this line.
	 *
	 * @return The validity of this line.
	 */
	public int getValidity() {
		return hexValidity + readableStringValidity;
	}

	/**
	 * Getter on the validity of the non-converted string.
	 *
	 * @return The validity of the non-converted string.
	 */
	public int getHexValidity() {
		return hexValidity;
	}

	/**
	 * Setter on the validity of the non-converted string.
	 *
	 * @param hexValidity
	 * 			The new validity of the non-converted string.
	 */
	public void setHexValidity(int hexValidity) {
		this.hexValidity = hexValidity;
	}

	/**
	 * Getter on the validity of the converted string.
	 *
	 * @return The validity of the converted string.
	 */
	public int getReadableStringValidity() {
		return readableStringValidity;
	}

	/**
	 * Setter on the validity of the converted string.
	 *
	 * @param readableStringValidity
	 * 			The new validity of the converted string.
	 */
	public void setReadableStringValidity(int readableStringValidity) {
		this.readableStringValidity = readableStringValidity;
	}

	/**
	 * Setter on the string put before this line in the toString method.
	 *
	 * @param decorationBefore
	 * 			The new string put before this line in the toString method.
	 */
	public void setDecorationBefore(String decorationBefore) {
		this.decorationBefore = decorationBefore;
	}

	/**
	 * Setter on the string put after this line in the toString method.
	 *
	 * @param decorationAfter
	 * 			The new string put after this line in the toString method.
	 */
	public void setDecorationAfter(String decorationAfter) {
		this.decorationAfter = decorationAfter;
	}

	/**
	 * Formats this line depending on the debugging level.
	 *
	 * @param debugLevel
	 * 			The debug level used to format this line.
	 * @return a string representing this line, with or without debug traces.
	 */
	public String toString(int debugLevel) {
		StringBuilder sb = new StringBuilder();

		if (debugLevel >= 1) {
			sb.append("hex: 0x" + hex + "\n");
		}
		if (debugLevel >= 2) {
			sb.append(
				"validity: " +
				"(hex: " + hexValidity + ") + " +
				"(jsStr: " + readableStringValidity + ") = " +
				getValidity() + "\n"
			);
		}
		if (debugLevel >= 5) {
			sb.append("non formatted: \n" + readableString + "\n");
			sb.append("formatted: \n");
		}

		sb.append(decorationBefore);
		sb.append(formattedString);
		sb.append(decorationAfter);

		return sb.toString();
	}

}
