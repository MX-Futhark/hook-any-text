package main.options.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import main.options.Options;
import main.options.annotations.CommandLineArgument;
import main.options.annotations.CommandLineValue;
import main.options.domain.ValueOutOfDomainException;
import main.utils.ReflectionUtils;

/**
 * Parses the arguments corresponding to an option object.
 *
 * @author Maxime PIA
 */
public class OptionsParser {

	private Options opts;

	public OptionsParser(Options opts) {
		this.opts = opts;
	}

	/**
	 * Parses all the arguments corresponding to the parsers associated to the
	 * option object.
	 *
	 * @param args
	 * 			The command line arguments.
	 * @return The list of all unrecognized sequences.
	 * @throws ValueOutOfDomainException
	 */
	public List<String> parse(List<String> args)
		throws ValueOutOfDomainException {

		List<String> remainingArgs = new LinkedList<>(args);

		List<ArgumentParser<?>> parsers;
		try {
			parsers = getArgumentParsers();
			for (String arg : args) {
				for (ArgumentParser<?> parser : parsers) {
					try {
						parser.parse(arg);
					} catch (IncompatibleParserException e) {
						continue;
					}

					remainingArgs.remove(arg);
				}
			}
		} catch (IllegalAccessException | SecurityException
			| NoSuchMethodException | IllegalArgumentException
			| InstantiationException | InvocationTargetException e) {

			e.printStackTrace();
		}

		return remainingArgs;
	}

	private List<ArgumentParser<?>> getArgumentParsers()
		throws IllegalArgumentException, IllegalAccessException,
		SecurityException, NoSuchMethodException, InstantiationException,
		InvocationTargetException {

		List<ArgumentParser<?>> res = new LinkedList<>();

		Collection<Field> fieldsToSet = ReflectionUtils.getAnnotatedFields(
			opts.getClass(),
			CommandLineArgument.class
		);
		for (Field f : fieldsToSet) {
			CommandLineArgument cmdArg =
				f.getAnnotation(CommandLineArgument.class);

			try {
				res.add(opts.getFieldParser(f)
					.getConstructor(Options.class, Field.class, String.class)
					.newInstance(opts, f, cmdArg.command()));
				continue;
			} catch (NoSuchFieldException e) {}

			FullArgumentParser<Object> fap =
				new FullArgumentParser<>(opts, f, cmdArg.command());
			res.add(fap);
			Collection<Field> valFields = opts.getValueFields(f);
			if (valFields != null) {
				for (Field valField : valFields) {
					CommandLineValue valDesc =
						valField.getAnnotation(CommandLineValue.class);
					if (!valDesc.shortcut().isEmpty()) {
						res.add(new ShortcutArgumentParser<Object>(
							opts, f, valDesc.shortcut(), valField.get(null), fap
						));
					}
				}
			}
		}
		return res;
	}

}
