package main.options;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import hextostring.debug.DebuggingFlags;
import hextostring.utils.Charsets;
import main.options.annotations.CommandLineArgument;
import main.options.annotations.CommandLineValue;
import main.options.domain.Domain;
import main.options.domain.ValueOutOfDomainException;
import main.options.domain.Values;
import main.options.parser.OptionsParser;
import main.utils.GenericSort;
import main.utils.ReflectionUtils;
import main.utils.StringUtils;

/**
 * Abstract Options.
 *
 * The command line manual, the option dialog and the serialization-
 * deserialization of the options are generated automatically by respecting
 * the following conventions:
 *
 * A member that needs to be serialized but can't is declared transient
 * and must have a corresponding constant member of type
 * SerializingFallback in this class.
 * Naming convention: the member's type in SCREAMING_SNAKE_CASE + "_IO"
 * A concrete class with such a member must implement read/writeObject
 * calling the corresponding methods Options.deserialize/serialize
 *
 * If the version of the project is XXXXX.YYYYY.ZZZZZ, the serialVersionUID
 * attribute of serializable option classes must respect the following format:
 * XXXXX0YYYYY0ZZZZZL
 *
 * The usual default value of a member is indicated as follows:
 * Naming convention: "DEFAULT_" + the member's type in SCREAMING_SNAKE_CASE
 *
 * A configurable member must be given a CommandLineArgument annotation and
 * the appropriate accessor methods. Setters for members containing flags are
 * managed differently, by following the model setXXXFlags(long, boolean) where
 * the second argument indicates if the first must be added or substracted
 * from the current value.
 *
 * The domain of a member whose values are constrained is indicated as follows:
 * Naming convention: the member's type in SCREAMING_SNAKE_CASE + "_DOMAIN"
 * If the values in the domain have further constraints or in the case of a
 * member containing flags, the values must be put as public static final
 * members of a separate class.
 * These members must be given a CommandLineValue annotation.
 * Said class is indicated as follows:
 * Naming convention: the member's type in SCREAMING_SNAKE_CASE + "VALUE_CLASS"
 *
 * @author Maxime PIA
 */
public abstract class Options {

	public static final String SERIALIAZATION_FILENAME = "config.data";

	protected interface SerializingFallback<T> {
		Charset read(ObjectInputStream in) throws IOException;
		void write(Object t, ObjectOutputStream out) throws IOException;
	}

	public static final SerializingFallback<Charset> CHARSET_IO =
		new SerializingFallback<Charset>() {

		@Override
		public Charset read(ObjectInputStream in) throws IOException {
			return Charsets.getValidCharset(in.readUTF());
		}

		@Override
		public void write(Object t, ObjectOutputStream out)
			throws IOException {

			out.writeUTF(((Charset) t).name());
		}
	};

	private static final Set<Options> subOptions =
		new HashSet<>();

	public Options() {
		subOptions.add(this);
	}

	/**
	 * Getter on the option objects using this class as a parent.
	 *
	 * @return The option objects using this class as a parent.
	 */
	public static Set<Options> getSubOptions() {
		return subOptions;
	}

	/**
	 * Parses and applies the command line arguments.
	 *
	 * @param args
	 * 			The command line arguments.
	 * @throws ValueOutOfDomainException
	 */
	public final void parseArgs(String[] args)
		throws ValueOutOfDomainException {

		if (args.length == 1 && args[0].equals("--help")) {
			System.out.println(usage(null));
		} else {
			List<String> remainingArgs = Arrays.asList(args);
			for (Options opts : subOptions) {
				OptionsParser parser = new OptionsParser(opts);
				remainingArgs = parser.parse(remainingArgs);
			}
			if (!remainingArgs.isEmpty()) {
				throw new IllegalArgumentException(
					"Illegal command line argument(s) or value(s): "
						+ remainingArgs + "."
				);
			}
		}
	}

	/**
	 * Defines how to use command line options.
	 *
	 * @param message
	 * 			The reason why the usage message is printed. May be null.
	 * @return The usage message.
	 */
	public static String usage(String message) {
		StringBuilder usage = new StringBuilder();
		if(message != null && !message.isEmpty()) {
			usage.append(message);
			usage.append("\n\nPlease use the following options:\n\n");
		}
		try {
			usage.append(generateUsageMessage());
		} catch (IllegalArgumentException | IllegalAccessException
			| NoSuchFieldException | SecurityException
			| NoSuchMethodException e) {

			e.printStackTrace();
		}

		return usage.toString();
	}

	private static String generateUsageMessage()
		throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException, NoSuchMethodException {

		StringBuilder usage = new StringBuilder();

		final int INDENT_LENGTH = 4;
		final int DESC_LINE_LENGTH = 80 - INDENT_LENGTH;

		Collection<Options> sortedOptions = GenericSort.apply(subOptions, null);
		for (Options opt : sortedOptions) {
			Collection<Field> sortedFields = GenericSort.apply(
				ReflectionUtils.getAnnotatedFields(
					opt.getClass(),
					CommandLineArgument.class
				),
				null
			);
			for (Field argField : sortedFields) {
				CommandLineArgument argInfo =
					argField.getAnnotation(CommandLineArgument.class);
				usage.append("--" + argInfo.command() + "=");
				if (argInfo.flags()) {
					usage.append(" combination of ");
				}

				Domain<?> argDomain = null;
				try {
					argDomain = opt.generateArgumentValueDomain(argField);
					if (argDomain == null) {
						argDomain = opt.getFieldDomain(argField);
					}
				} catch (NoSuchFieldException e) {}
				Collection<Field> valFields = opt.getValueFields(argField);

				if (argDomain != null) {
					usage.append(argDomain);
				} else {
					usage.append(argField.getType().getSimpleName());
				}

				Object argDefault = opt.getFieldDefaultValue(argField);
				if (argDefault != null) {
					usage.append(", default=");
					if (argInfo.flags()) {
						String flagsStr = DebuggingFlags.longToCmdFlags(
							(Long) opt.getFieldDefaultValue(argField)
						);
						usage.append(flagsStr.isEmpty() ? "none" : flagsStr);
					} else if (valFields == null) {
						usage.append(argDefault);
					} else {
						usage.append(
							getDefaultValueString(argDefault, valFields)
						);
					}
				}
				usage.append(
					"\n" + StringUtils.indent(
						StringUtils.breakText(
							argInfo.description(),
							DESC_LINE_LENGTH
						), " ", INDENT_LENGTH
					)
				);
				if (!argInfo.usageExample().isEmpty()) {
					usage.append("\n" + StringUtils.indent(
						"example: " + argInfo.usageExample(), " ", INDENT_LENGTH
					));
				}

				Collection<CommandLineValue> valDescs;
				try {
					valDescs = opt.getValuesDescriptions(
						opt.getFieldValueClass(argField)
					);
					usage.append("\n" + StringUtils.indent(
						generateValueTable(valDescs),
						" ",
						INDENT_LENGTH
					));
				} catch (NoSuchFieldException e) {}

				usage.append("\n\n");
			}
		}

		usage.append("--help (cannot be used with other options)\n");
		usage.append(
			StringUtils.indent("Displays this message.", " ", INDENT_LENGTH)
		);

		return usage.toString();
	}

	private static String getDefaultValueString(Object defaultValue,
		Collection<Field> valFields) throws IllegalArgumentException,
		IllegalAccessException {

		for (Field valField : valFields) {
			if (valField.get(null).toString().equals(defaultValue.toString())) {
				return valField.getAnnotation(CommandLineValue.class).value();
			}
		}
		return defaultValue.toString();
	}

	/**
	 * Getter on the possible values of a configurable member.
	 *
	 * @param argField
	 * 			The configurable member.
	 * @return The possible values of the configurable member.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	public Collection<Field> getValueFields(Field argField)
		throws IllegalArgumentException, IllegalAccessException,
		SecurityException {

		Class<?> argValsClass = null;
		Collection<Field> valFields;
		try {
			argValsClass = getFieldValueClass(argField);
			valFields = ReflectionUtils.getAnnotatedFields(
				argValsClass,
				CommandLineValue.class
			);
		} catch (NoSuchFieldException e) {
			valFields = null;
		}
		return valFields;
	}

	private Collection<CommandLineValue> getValuesDescriptions(
		Class<?> valueClass) throws NoSuchMethodException,
		SecurityException {

		return valueClass == null
			? null
			: GenericSort.apply(
				ReflectionUtils.getAnnotations(
					valueClass,
					CommandLineValue.class
				),
				CommandLineValue.class.getMethod("value")
			);
	}

	/**
	 * Maps the command line string values to their actual value.
	 *
	 * @param argField
	 * 			A configurable member with a restricted number of values.
	 * @return A map of the command line string values to their actual value.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getValueDomainToActualValue(Field argField)
		throws NoSuchMethodException, SecurityException,
		IllegalArgumentException, IllegalAccessException {

		Collection<Field> valFields = getValueFields(argField);
		if (valFields == null) {
			return null;
		}

		Map<String, T> res = new HashMap<>();

		for (Field valField : valFields) {
			CommandLineValue valDesc =
				valField.getAnnotation(CommandLineValue.class);
			String key = valDesc.value().isEmpty()
				? valField.get(null).toString()
				: valDesc.value();
			res.put(key, (T) valField.get(null));
		}

		return res;
	}

	private Values<String> generateArgumentValueDomain(Field argField)
		throws NoSuchMethodException, SecurityException,
		IllegalArgumentException, IllegalAccessException {

		Map<String, Object> valuesMap = getValueDomainToActualValue(argField);
		if (valuesMap == null) {
			return null;
		}
		return new Values<String>(
			valuesMap.keySet().toArray(new String[valuesMap.keySet().size()])
		);
	}

	private static String generateValueTable(
		Collection<CommandLineValue> valuesDesc) {

		final String VALUE = "value";
		final String DESCRIPTION = "description";
		final String SHORTCUT = "shortcut";
		final String CONDITION = "condition";

		final String COLUMN_SEPARATOR = "   ";
		final String HEADER_SEPARATOR = "-";

		Map<Integer, String> headers = new TreeMap<>();
		headers.put(0, VALUE);
		Map<String, Map<String, String>> content = new TreeMap<>();
		for (CommandLineValue desc : valuesDesc) {
			Map<String, String> lineContent = new HashMap<>();
			lineContent.put(DESCRIPTION, desc.description());
			headers.put(2, DESCRIPTION);
			if (desc.shortcut().length() > 0) {
				lineContent.put(SHORTCUT, "-" + desc.shortcut());
				headers.put(1, SHORTCUT);
			}
			if (desc.condition().length() > 0) {
				lineContent.put(CONDITION, desc.condition());
				headers.put(3, CONDITION);
			}
			content.put(desc.value(), lineContent);
		}

		StringBuilder table = new StringBuilder();
		int[] widths = new int[headers.size()];
		Arrays.fill(widths, 30);
		widths[0] = 10;
		if (headers.containsValue(SHORTCUT)) {
			widths[1] = 10;
		}

		int eltCounter = 0;
		for (String title : headers.values()) {
			table.append(
				StringUtils.fillWithSpaces(title, widths[eltCounter++])
			);
			table.append(COLUMN_SEPARATOR);
		}
		table.append("\n");
		eltCounter = 0;
		for (String title : headers.values()) {
			table.append(
				StringUtils.fillWithSpaces(
					title.replaceAll(".", HEADER_SEPARATOR),
					widths[eltCounter++]
				)
			);
			table.append(COLUMN_SEPARATOR);
		}
		table.append("\n");
		headers.remove(0);
		for (String value : content.keySet()) {
			String[] paragraphs = new String[headers.values().size() + 1];
			paragraphs[0] = value;
			int columnIndex = 1;
			for (String title : headers.values()) {
				paragraphs[columnIndex++] = content.get(value).get(title);
			}
			table.append(
				StringUtils.putParagraphsSideBySide(
					paragraphs,
					widths,
					COLUMN_SEPARATOR
				)
			);
		}

		return table.toString();
	}

	/**
	 * Getter on the default value of a configurable member.
	 *
	 * @param argField
	 * 			The configurable member.
	 * @return The default value of the configurable member.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public Object getFieldDefaultValue(Field argField)
		throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException {

		return getFieldAssociatedInformation("DEFAULT_", argField, "");
	}

	/**
	 * Getter on the value domain of a configurable member.
	 *
	 * @param @param argField
	 * 			The configurable member.
	 * @return The value domain of the configurable member.
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public Domain<?> getFieldDomain(Field argField)
		throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException {

		return (Domain<?>)
			getFieldAssociatedInformation("", argField, "_DOMAIN");
	}

	/**
	 * Getter on the value class of a configurable member.
	 *
	 * @param @param argField
	 * 			The configurable member.
	 * @return The value class of the configurable member.
	 * @param argField
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public Class<?> getFieldValueClass(Field argField)
		throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException {

		return (Class<?>)
			getFieldAssociatedInformation("", argField, "_VALUE_CLASS");
	}

	private Object getFieldAssociatedInformation(String prefix,
		Field argField, String suffix) throws IllegalArgumentException,
		IllegalAccessException, NoSuchFieldException, SecurityException {

		Field f = getClass().getDeclaredField(
			prefix
				+ StringUtils.camelToScreamingSnake(argField.getName())
				+ suffix
		);
		f.setAccessible(true);
		return f.get(this);
	}

	protected synchronized void serialize(ObjectOutputStream out)
		throws IOException {

		out.defaultWriteObject();
		try {
			Map<Field, SerializingFallback<?>> fallbacks =
				getSerializingFallbacks();
			for (Field f : fallbacks.keySet()) {
				fallbacks.get(f).write(f.get(this), out);
			}
		} catch (IllegalArgumentException | IllegalAccessException
			| NoSuchFieldException | SecurityException e) {

			throw new IOException(e);
		}
	}

	protected synchronized void deserialize(ObjectInputStream in)
		throws IOException, ClassNotFoundException {

		in.defaultReadObject();
		try {
			Map<Field, SerializingFallback<?>> fallbacks =
				getSerializingFallbacks();
			for (Field f : fallbacks.keySet()) {
				f.set(this, fallbacks.get(f).read(in));
			}
		} catch (IllegalArgumentException | IllegalAccessException
			| NoSuchFieldException | SecurityException e) {

			throw new IOException(e);
		}
	}

	protected Map<Field, SerializingFallback<?>> getSerializingFallbacks()
		throws IllegalArgumentException, IllegalAccessException,
		NoSuchFieldException, SecurityException {

		List<Field> nonSerializableFields = ReflectionUtils.getModifiedFields(
			this.getClass(),
			Modifier.TRANSIENT
		);
		Map<Field, SerializingFallback<?>> fallbacks = new HashMap<>();
		for (Field f : nonSerializableFields) {
			f.setAccessible(true);
			String fallbackName = StringUtils.camelToScreamingSnake(
				f.getType().getSimpleName()
			) + "_IO";
			fallbacks.put(
				f,
				(SerializingFallback<?>) Options.class.getField(fallbackName)
					.get(this)
			);
		}
		return fallbacks;
	}

}
