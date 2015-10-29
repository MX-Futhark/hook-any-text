package gui.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import gui.actions.HistoryDialogActions;
import gui.utils.Images;
import gui.views.components.HistoryTable;
import hextostring.HexProcessor;
import hextostring.history.History;
import main.utils.StringUtils;

/**
 * The dialog containing the history of the lines.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class HistoryDialog extends JDialog implements Observer {

	private HistoryDialogActions acts = new HistoryDialogActions(this);

	private MainWindow mainWindow;
	private HistoryTable historyTable;

	private HexProcessor hp;

	private int latestDisplayed = -1;

	public HistoryDialog(MainWindow mainWindow, History observedHistory,
		HexProcessor hp) {

		setTitle("History");
		setModal(false);
		setIconImage(Images.DEFAULT_ICON.getImage());

		this.mainWindow = mainWindow;
		this.hp = hp;
		observedHistory.addObserver(this);

		appendBody();
		setSize(400, 400);
		setMinimumSize(new Dimension(260, 100));
	}

	private void appendBody() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(getTableScrollPane(), BorderLayout.CENTER);
		mainPanel.add(getButtonsPanel(), BorderLayout.SOUTH);
		add(mainPanel);
	}

	private JScrollPane getTableScrollPane() {
		historyTable = new HistoryTable();
		JScrollPane scrollPane = new JScrollPane(historyTable);
		return scrollPane;
	}

	private JPanel getButtonsPanel() {
		JPanel buttonsPanel = new JPanel(new BorderLayout());

		JButton setOutputButton = new JButton("Set output as current");
		acts.setSetOutputButtonAction(setOutputButton);
		buttonsPanel.add(setOutputButton, BorderLayout.EAST);

		JButton reconvertInputButton = new JButton("Reconvert input");
		acts.setReconvertInputButtonAction(reconvertInputButton);
		buttonsPanel.add(reconvertInputButton, BorderLayout.WEST);

		return buttonsPanel;
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		History history = (History) o;
		DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
		model.addRow(new Object[]{
			breakTooltipContent(history.getLast().getInput(), 100),
			breakTooltipContent(history.getLast().getOutput(), 100)
		});
		if (model.getRowCount() > History.HISTORY_MAX_SIZE) {
			model.removeRow(0);
		}
		latestDisplayed = model.getRowCount() - 1;
	}

	/**
	 * Entirely reconverts an input.
	 *
	 * @param row
	 * 			The row containing the input.
	 */
	public void reconvertInputAt(int row) {
		hp.convert(getRowContentAt(row, true), true);
		int lastRow = historyTable.getRowCount() - 1;
		setSelectedRow(lastRow);
		latestDisplayed = lastRow;
	}

	/**
	 * Sets an output in the text area of the main window.
	 *
	 * @param row
	 * 			The row containing the output.
	 */
	public void setOutputAsCurrentAt(int row) {
		mainWindow.setTextAreaContent(getRowContentAt(row, false));
		setSelectedRow(row);
		latestDisplayed = row;
	}

	/**
	 * Getter on the index of the latest displayed row.
	 *
	 * @return The index of the latest displayed row.
	 */
	public int getLatestDisplayedRowIndex() {
		return latestDisplayed;
	}

	private String getRowContentAt(int row, boolean input) {
		return StringUtils.HTMLToPlainText(
			historyTable.getModel().getValueAt(row, input ? 0 : 1).toString()
		);
	}

	private String breakTooltipContent(String content, int maxLineLength) {
		return StringUtils.plainTextToHTML(
			StringUtils.breakNoSpaceText(content, maxLineLength)
		);
	}

	/**
	 * Getter on the index of the currently selected row.
	 *
	 * @return The index of the currently selected row.
	 */
	public int getSelectedRow() {
		return historyTable.getSelectedRow();
	}

	/**
	 * Setter on the index of the currently selected row.
	 *
	 * @param row
	 * 			The index of the row to select.
	 */
	public void setSelectedRow(int row) {
		historyTable.setRowSelectionInterval(row, row);
	}

	/**
	 * Getter on the number of rows.
	 *
	 * @return The number of rows.
	 */
	public int getRowCount() {
		return historyTable.getRowCount();
	}
}
