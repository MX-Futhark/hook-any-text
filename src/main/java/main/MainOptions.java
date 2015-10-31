package main;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gui.GUIOptions;
import hexcapture.HexOptions;
import hextostring.ConvertOptions;
import main.options.EncompassingOptions;
import main.options.Options;
import main.options.domain.ValueOutOfDomainException;
import main.options.parser.OptionsParser;

/**
 * Contains all the options of the various parts of the program.
 *
 * @author Maxime PIA
 */
public class MainOptions extends Options implements Serializable,
	EncompassingOptions {

	/**
	 * Backward-compatible with 0.7.0
	 */
	private static final long serialVersionUID = 00000000007000000L;

	private HexOptions hexOptions;
	private ConvertOptions convertOptions;
	private GUIOptions GUIOptions;

	private Set<Options> subOptions = new HashSet<>();

	public MainOptions() {
		initSubOptions();
	}

	public MainOptions(String[] args) throws ValueOutOfDomainException {
		this();
		parseArgs(args);
	}

	private void initSubOptions() {
		this.hexOptions = new HexOptions();
		this.convertOptions = new ConvertOptions();
		this.GUIOptions = new GUIOptions();
		updateSubOptionsSet();

	}

	public void updateSubOptionsSet() {
		subOptions.clear();
		subOptions.add(this);
		subOptions.add(this.hexOptions);
		subOptions.add(this.convertOptions);
		subOptions.add(this.GUIOptions);
	}

	/**
	 * Getter on the option objects contained in this class.
	 *
	 * @return The option objects contained in this class.
	 */
	public Set<Options> getSubOptions() {
		return subOptions;
	}

	/**
	 * Getter on the options relative to the behavior of the main lua file.
	 *
	 * @return The options relative to the behavior of the main lua file.
	 */
	public synchronized HexOptions getHexOptions() {
		return hexOptions;
	}

	/**
	 * Getter on the conversion options.
	 *
	 * @return The conversion options.
	 */
	public synchronized ConvertOptions getConvertOptions() {
		return convertOptions;
	}

	/**
	 * Getter of the options for the GUI.
	 *
	 * @return The options for the GUI.
	 */
	public synchronized GUIOptions getGUIOptions() {
		return GUIOptions;
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
			System.out.println(usage(null, subOptions));
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

}
