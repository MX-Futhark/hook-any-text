package main.history;

/**
 * A pair of objects representing the input and the output of a conversion.
 *
 * @author Maxime PIA
 */
public class InputOutputPair<I, O> {

	private I input;
	private O output;

	public InputOutputPair(I input, O output) {
		super();
		this.input = input;
		this.output = output;
	}

	/**
	 * Getter on the input.
	 *
	 * @return The input.
	 */
	public I getInput() {
		return input;
	}

	/**
	 * Getter on the output.
	 *
	 * @return The output.
	 */
	public O getOutput() {
		return output;
	}

}
