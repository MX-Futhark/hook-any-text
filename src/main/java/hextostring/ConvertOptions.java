package hextostring;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;

import hextostring.debug.DebuggingFlags;
import hextostring.utils.Charsets;
import main.options.Options;
import main.options.ValueClass;
import main.options.annotations.CommandLineArgument;
import main.options.domain.Bounds;
import main.options.domain.Values;

/**
 * Options for string conversion.
 *
 * @author Maxime PIA
 */
public class ConvertOptions extends Options implements Serializable {

	/**
	 * Backward-compatible with 6.0.0
	 */
	private static final long serialVersionUID = 00000600000000000L;

	public static final int DEFAULT_STRICTNESS = 20;
	public static final Charset DEFAULT_CHARSET = Charsets.DETECT;
	public static final boolean DEFAULT_AUTOCOPY = true;

	public static final long DEFAULT_DEBUGGING_FLAGS = 0;

	@CommandLineArgument(
		command = "strictness",
		description = "The strictness of the conversion.\nThe strictness is a "
			+ "value determining at what point a string is considered garbage "
			+ "and should not be displayed. The lower the value, the higher "
			+ "the chance of having garbage in the result. The higher the "
			+ "value, the higher the chance of mistakenly excluding a valid "
			+ "string."
	)
	private int strictness = DEFAULT_STRICTNESS;
	public static final Bounds<Integer> STRICTNESS_DOMAIN =
		new Bounds<>(-500, 500);

	@CommandLineArgument(
		command = "encoding",
		description = "Defines the encoding used to decode the input"
	)
	private transient Charset charset = DEFAULT_CHARSET;
	public static final Values<Charset> CHARSET_DOMAIN =
		new Values<>(Charsets.ALL_CHARSETS);
	public static final Class<? extends ValueClass> CHARSET_VALUE_CLASS =
		Charsets.class;

	@CommandLineArgument(
		command = "autocopy",
		description = "Defines whether or not to automatically copy newly "
			+ "converted strings in the clipboard."
	)
	private boolean autocopy = DEFAULT_AUTOCOPY;

	@CommandLineArgument(
		command = "debug",
		description = "Aimed at developers. Sets debugging flags.",
		usageExample = "--debug=i6n",
		flags = true
	)
	private long debuggingFlags = DEFAULT_DEBUGGING_FLAGS;
	public static final
		Class<? extends ValueClass> DEBUGGING_FLAGS_VALUE_CLASS =
			DebuggingFlags.class;

	public ConvertOptions() {
		super();
	}

	/**
	 * Getter on the strictness of the conversion process.
	 *
	 * @return The strictness of the program.
	 */
	public synchronized int getStrictness() {
		return strictness;
	}

	/**
	 * Setter on the strictness of the conversion process.
	 *
	 * @param strictness
	 * 			The new strictness of the program.
	 */
	public synchronized void setStrictness(int strictness) {
		this.strictness = strictness;
	}

	/**
	 * Getter on the debugging flags currently activated.
	 *
	 * @return The debug level of the program.
	 */
	public synchronized long getDebuggingFlags() {
		return debuggingFlags;
	}

	/**
	 * Setter on the debugging flags currently activated.
	 *
	 * @param flags
	 * 			The flags affecting the current value.
	 * @param add
	 * 			True to add the value to the current flags, false to remove it.
	 */
	public synchronized void setDebuggingFlags(long flags, boolean add) {
		debuggingFlags = add
			? (debuggingFlags | flags)
			: (debuggingFlags & ~flags);
	}

	/**
	 * Getter on the charset to work with.
	 *
	 * @return The charset in which to convert hex input strings.
	 */
	public synchronized Charset getCharset() {
		return charset;
	}

	/**
	 * Setter on the charset to work with.
	 *
	 * @param charset
	 * 			The new charset in which to convert hex input strings.
	 */
	public synchronized void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * Getter on whether or not the conversion result is set to the clipboard.
	 *
	 * @return Whether or not the conversion result is set to the clipboard.
	 */
	public synchronized boolean isAutocopy() {
		return autocopy;
	}

	/**
	 * Setter on whether or not the conversion result is set to the clipboard.
	 *
	 * @param autocopy
	 * 			True if the conversion result is set to the clipboard.
	 */
	public synchronized void setAutocopy(boolean autocopy) {
		this.autocopy = autocopy;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		serialize(out);
	}

	private void readObject(ObjectInputStream in) throws IOException,
		ClassNotFoundException {

		deserialize(in);
	}

}
