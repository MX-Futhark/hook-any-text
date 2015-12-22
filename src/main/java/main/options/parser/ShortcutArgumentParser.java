package main.options.parser;

import java.lang.reflect.Field;

import main.options.Options;

/**
 * Parses a argument in the form -value
 *
 * @author Maxime PIA
 *
 * @param <T>
 * 			The type of the value of the argument.
 */
public class ShortcutArgumentParser<T> extends ArgumentParser<T> {

	private T value;
	private FullArgumentParser<T> fullParser;

	public ShortcutArgumentParser(Options opts, Field f, String argument,
		T value, FullArgumentParser<T> fullParser) {

		super(opts, f, "-" + argument);
		this.value = value;
		this.fullParser = fullParser;
	}

	public FullArgumentParser<T> getFullParser() {
		return fullParser;
	}


	@Override
	protected T getArgumentValue(String arg)
		throws IncompatibleParserException {

		if (!arg.equals(getArgument())) throw new IncompatibleParserException();
		return value;
	}

}
