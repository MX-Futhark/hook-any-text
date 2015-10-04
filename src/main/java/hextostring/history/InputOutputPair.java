package hextostring.history;

/**
 * A pair of strings representing the input and the output of a conversion.
 *
 * @author Maxime PIA
 */
public class InputOutputPair {

	private String input;
	private String output;

	public InputOutputPair(String input, String output) {
		super();
		this.input = input;
		this.output = output;
	}

	/**
	 * Getter on the input.
	 *
	 * @return The input.
	 */
	public String getInput() {
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
