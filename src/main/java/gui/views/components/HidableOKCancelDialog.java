package gui.views.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import gui.GUIOptions;

/**
 * Confirmation dialog that can be definitely hidden.
 *
 * @author Maixme PIA
 */
public class HidableOKCancelDialog {

	public static final int CLOSE_CONFIRM = 0;
	public static final String CLOSE_CONFIRM_MESSAGE =
		"Hook Any Text will be closed. "
			+ "You can open it again from Cheat Egine's File menu.";

	private int id;

	private JPanel body;
	private Component parent;
	private GUIOptions opts;

	public HidableOKCancelDialog(int id, Component parent, String message,
		GUIOptions opts) {

		this.parent = parent;
		this.opts = opts;

		this.body = new JPanel();
		BoxLayout layout = new BoxLayout(this.body, BoxLayout.Y_AXIS);
		this.body.setLayout(layout);

		JLabel messageLabel = new JLabel(message);
		final JCheckBox dontShowCB =
			new JCheckBox("Do not show this message again.");
		dontShowCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dontShowCB.isSelected()) {
					HidableOKCancelDialog.this.opts.addHiddenDialog(
						HidableOKCancelDialog.this.id
					);
				}
			}
		});
		body.add(messageLabel);
		body.add(dontShowCB);
	}

	/**
	 * Getter on whether or not the dialog has been hidden.
	 *
	 * @return True if the dialog has been hidden.
	 */
	public boolean isHidden() {
		return (opts.isDialogHidden(id));
	}

	/**
	 * Shows the dialog if it hasn't been hidden.
	 *
	 * @return JOptionPane.OK_OPTION if hidden
	 * 			or the result of showConfirmDialog of not hidden.
	 */
	public int show() {
		if (isHidden()) return JOptionPane.OK_OPTION;
		return JOptionPane.showConfirmDialog(
			parent, body, "Confirmation", JOptionPane.OK_CANCEL_OPTION
		);
	}

}
