package gui;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import main.options.Options;

/**
 * Options for the GUI.
 *
 * @author Maxime PIA
 */
public class GUIOptions extends Options implements Serializable {

	/**
	 * Backward-compatible with 6.0.0
	 */
	private static final long serialVersionUID = 00000600000000000L;

	private Set<Integer> hiddenDialogIDs = new HashSet<>();

	public GUIOptions() {
		super();
	}

	/**
	 * Definitely hides the dialog identified by the parameter.
	 *
	 * @param id
	 * 			The number used to identified the dialog.
	 */
	public void addHiddenDialog(int id) {
		hiddenDialogIDs.add(id);
	}

	/**
	 * Determines whether a dialog is hidden or not.
	 *
	 * @param id
	 * 			The number used to identified the dialog.
	 * @return True if the dialog is hidden.
	 */
	public boolean isDialogHidden(int id) {
		return hiddenDialogIDs.contains(id);
	}

}
