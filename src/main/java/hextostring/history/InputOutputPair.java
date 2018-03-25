package hextostring.history;

import hexcapture.HexSelectionsContentSnapshot;

/**
 * A pair of strings representing the input and the output of a conversion.
 *
 * @author Maxime PIA
 */
public class InputOutputPair {

	private HexSelectionsContentSnapshot input;
	private String output;

	public InputOutputPair(HexSelectionsContentSnapshot input, String output) {
		super();
		this.input = input;
		this.output = output;
	}

	/**
	 * Getter on the input.
	 *
	 * @return The input.
	 */
	public HexSelectionsContentSnapshot getInput() {
		return input;
	}

	/**
	 * Getter on the output.
	 *
	 * @return The output.
	 */
	public String getOutput() {
		return output;
	}

}
