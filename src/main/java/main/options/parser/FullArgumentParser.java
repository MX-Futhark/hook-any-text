package main.options.parser;

import java.lang.reflect.Field;
import java.util.Map;

import main.options.Options;
import main.options.annotations.CommandLineArgument;

/**
 * Parses a argument in the form --command=value
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the value of the argument.
 */
public class FullArgumentParser<T> extends ArgumentParser<T> {

	public FullArgumentParser(Options opts, Field f, String argument) {
		super(opts, f, "--" + argument + "=");
	}

	@Override
	protected T getArgumentValue(String arg)
		throws IncompatibleParserException {

		if (!arg.substring(0, arg.indexOf("=") + 1).equals(getArgument())) {
			throw new IncompatibleParserException();
		}
		String strValue = arg.replace(getArgument(), "");
		boolean isFlags = getAffectedOptField()
			.getAnnotation(CommandLineArgument.class).flags();
		try {
			Map<String, T> possibleValues = getAffectedOptObject()
				.getValueDomainToActualValue(getAffectedOptField());
			T res = isFlags
				? getFlagsValue(strValue, possibleValues)
				: (possibleValues == null
					? parsePrimitiveValue(strValue)
					: getUniqueValue(strValue, possibleValues));
			return res;
		} catch (NoSuchMethodException | SecurityException
			| IllegalAccessException e) {

			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private T parsePrimitiveValue(String strValue) {
		Class<?> fieldType = getAffectedOptField().getType();
		if (fieldType.equals(boolean.class)) {
			return (T) new Boolean(Boolean.parseBoolean(strValue));
		} else if  (fieldType.equals(int.class)
			|| fieldType.equals(long.class)) {

			return (T) new Integer(Integer.parseInt(strValue));
		} else if (fieldType.equals(float.class)
			|| fieldType.equals(double.class)) {

			return (T) new Double(Double.parseDouble(strValue));
		}
		return null;
	}

	private T getUniqueValue(String strValue, Map<String, T> possibleValues) {
		for (String val : possibleValues.keySet()) {
			if (val.equals(strValue)) {
				return possibleValues.get(val);
			}
		}
		throw new IllegalArgumentException(
			"Illegal command line value: " + getArgument() + strValue + "."
		);
	}

	@SuppressWarnings("unchecked")
	private T getFlagsValue(String strValue, Map<String, T> possibleValues) {
		long res = 0;
		String remainingFlags = strValue;
		for (String val : possibleValues.keySet()) {
			if (remainingFlags.contains(val)) {
				remainingFlags = remainingFlags.replace(val, "");
				res |= (Long) possibleValues.get(val);
			}
		}
		if (!remainingFlags.isEmpty()) {
			throw new IllegalArgumentException(
				"Illegal flag(s): " + getArgument() + remainingFlags + "."
			);
		}
		return (T) new Long(res);
	}

}
