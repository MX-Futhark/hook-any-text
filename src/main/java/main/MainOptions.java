package main;

import java.io.Serializable;

import gui.GUIOptions;
import hexcapture.HexOptions;
import hextostring.ConvertOptions;
import main.options.Options;
import main.options.domain.ValueOutOfDomainException;

/**
 * Contains all the options of the various parts of the program.
 *
 * @author Maxime PIA
 */
public class MainOptions extends Options implements Serializable {

	/**
	 * Backward-compatible with 6.0.0
	 */
	private static final long serialVersionUID = 00000600000000000L;

	private HexOptions hexOptions;
	private ConvertOptions convertOptions;
	private GUIOptions GUIOptions;

	public MainOptions() {
		super();
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

}
