package main.options.parser;

import java.lang.reflect.Field;

import main.options.Options;
import main.options.domain.Domain;
import main.options.domain.ValueOutOfDomainException;

/**
 * Parses and applies one command line argument.
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the value of the argument.
 */
public abstract class ArgumentParser<T> {

	private Options affectedOptObject;
	private Field affectedOptField;
	private String argument;

	public ArgumentParser(Options opts, Field f, String argument) {
		this.affectedOptObject = opts;
		this.affectedOptField = f;
		this.argument = argument;
	}

	/**
	 * Sets the configurable member corresponding to the argument to a value.
	 *
	 * @param arg
	 * 			The command line argument giving the value to be set.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws ValueOutOfDomainException
	 * @throws IncompatibleParserException
	 */
	@SuppressWarnings("unchecked")
	public void parse(String arg) throws IllegalArgumentException,
		IllegalAccessException, SecurityException, ValueOutOfDomainException,
		IncompatibleParserException {

		T value =  getArgumentValue(arg);
		Domain<T> fieldDomain = null;
		try {
			fieldDomain =
				(Domain<T>) affectedOptObject.getFieldDomain(affectedOptField);
		} catch (NoSuchFieldException e) {}
		if (fieldDomain == null || fieldDomain.inDomain((T) value)) {
			affectedOptField.setAccessible(true);
			affectedOptField.set(affectedOptObject, value);
		} else {
			throw new ValueOutOfDomainException(
				"Domain error on " + argument + ", " + value
					+ " not in " + fieldDomain
			);
		}
	}

	/**
	 * Getter on the argument corresponding to the parser.
	 *
	 * @return The argument corresponding to the parser.
	 */
	public String getArgument() {
		return argument;
	}

	protected Options getAffectedOptObject() {
		return affectedOptObject;
	}

	protected Field getAffectedOptField() {
		return affectedOptField;
	}

	protected abstract T getArgumentValue(String arg)
		throws IncompatibleParserException;

}
