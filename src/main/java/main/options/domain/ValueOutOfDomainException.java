package main.options.domain;

/**
 * Indicates an attempt at setting a variable to a value outside of its domain.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ValueOutOfDomainException extends Exception {

	public ValueOutOfDomainException() {
		super();
	}

	public ValueOutOfDomainException(String msg) {
		super(msg);
	}

}
