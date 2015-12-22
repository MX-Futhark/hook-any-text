package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import gui.views.HistoryDialog;

/**
 * Contains all the actions for the view HistoryDialog.
 *
 * @author Maxime PIA
 */
public class HistoryDialogActions {

	private HistoryDialog historyDialog;

	public HistoryDialogActions(HistoryDialog historyDialog) {
		this.historyDialog = historyDialog;
	}

	/**
	 * Sets the action for the "set output as current" button.
	 *
	 * @param setOutputButton
	 * 			The button.
	 */
	public void setSetOutputButtonAction(JButton setOutputButton) {

		setOutputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				historyDialog.setOutputAsCurrentAt(
					historyDialog.getSelectedRow()
				);
			}
		});
	}

	/**
	 * Sets the action for the "reconvert input" button.
	 *
	 * @param reconvertInputButton
	 * 			The button.
	 */
	public void setReconvertInputButtonAction(JButton reconvertInputButton) {

		reconvertInputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				historyDialog.reconvertInputAt(historyDialog.getSelectedRow());
			}
		});
	}

}
