package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import gui.views.HistoryDialog;
import gui.views.MainWindow;
import gui.views.components.AboutPanel;
import gui.views.components.HidableOKCancelDialog;

/**
 * Contains all the actions for the view MainWindow.
 *
 * @author Maxime PIA
 */
public class MainWindowActions {

	private MainWindow mainWindow;

	public MainWindowActions(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	/**
	 * Sets the close action for the window.
	 *
	 * @param closeConfirm
	 * 			The confirmation dialog.
	 */
	public void setCloseAction(final HidableOKCancelDialog closeConfirm) {

		mainWindow.setDefaultCloseOperation(
			WindowConstants.DO_NOTHING_ON_CLOSE
		);
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int userResponse = closeConfirm.show();
				if (userResponse == JOptionPane.OK_OPTION) {
					mainWindow.dispose();
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Associates a dialog to a menu item.
	 *
	 * @param item
	 * 			The menu item.
	 * @param dialog
	 * 			The dialog.
	 */
	public void setDialog(final JMenuItem item, final JDialog dialog) {
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(true);
			}
		});
	}

	/**
	 * Sets the action for the "exit" menu item.
	 *
	 * @param quitItem
	 * 			The menu item.
	 */
	public void setExitItemAction(JMenuItem quitItem) {
		quitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainWindow.dispatchEvent(
					new WindowEvent(mainWindow, WindowEvent.WINDOW_CLOSING)
				);
			}
		});
	}

	/**
	 * Sets the actions for the "previous line" menu item.
	 *
	 * @param previousLineItem
	 * 			The menu item.
	 * @param historyDialog
	 * 			The dialog contain the history of the converted lines.
	 */
	public void setPreviousLineItemAction(final JMenuItem previousLineItem,
		final HistoryDialog historyDialog) {

		previousLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (historyDialog.getRowCount() == 0) return;
				historyDialog.setOutputAsCurrentAt(
					Math.max(0, historyDialog.getLatestDisplayedRowIndex() - 1)
				);
			}
		});
	}

	/**
	 * Sets the actions for the "next line" menu item.
	 *
	 * @param nextLineItem
	 * 			The menu item.
	 * @param historyDialog
	 * 			The dialog contain the history of the converted lines.
	 */
	public void setNextLineItemAction(final JMenuItem nextLineItem,
		final HistoryDialog historyDialog) {

		nextLineItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (historyDialog.getRowCount() == 0) return;
				historyDialog.setOutputAsCurrentAt(Math.min(
					historyDialog.getRowCount() - 1,
					historyDialog.getLatestDisplayedRowIndex() + 1
				));
			}
		});
	}

	/**
	 * Sets the action for the "about" menu item.
	 *
	 * @param aboutItem
	 * 			The menu item.
	 */
	public void setAboutItemAction(final JMenuItem aboutItem) {

		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				JOptionPane.showMessageDialog(
					mainWindow,
					new AboutPanel(),
					aboutItem.getName(),
					JOptionPane.PLAIN_MESSAGE
				);
			}
		});
	}

}
