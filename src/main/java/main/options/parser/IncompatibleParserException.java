package main.options.parser;

/**
 * Indicates an attempt to use a parser with an argument different from the one
 * it was created for.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class IncompatibleParserException extends Exception {

	public IncompatibleParserException() {
		super();
	}

	public IncompatibleParserException(String msg) {
		super(msg);
	}

}
