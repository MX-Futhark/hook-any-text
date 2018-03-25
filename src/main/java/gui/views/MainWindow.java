package gui.views;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.actions.MainWindowActions;
import gui.utils.GUIErrorHandler;
import gui.utils.Images;
import gui.views.components.HidableOKCancelDialog;
import hextostring.HexProcessor;
import hextostring.history.History;
import main.MainOptions;

/**
 * The main window of the GUI.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame implements Observer {

	private MainWindowActions acts = new MainWindowActions(this);

	private MainOptions opts;
	private HexProcessor hp;

	private JTextArea convertedStringsArea = new JTextArea("Welcome to HAT!");

	public MainWindow(HexProcessor hp, MainOptions opts,
		History observedHistory, boolean deserializationWarning) {

		super("Hook Any Text");
		setSize(640, 240);
		setIconImage(Images.DEFAULT_ICON.getImage());

		this.opts = opts;
		this.hp = hp;
		observedHistory.addObserver(this);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			appendMenuBar(observedHistory);
			appendFrameContent();
			acts.setCloseAction(new HidableOKCancelDialog(
				HidableOKCancelDialog.CLOSE_CONFIRM,
				this,
				HidableOKCancelDialog.CLOSE_CONFIRM_MESSAGE,
				opts.getGUIOptions()
			));
		} catch (ClassNotFoundException | InstantiationException
			| IllegalAccessException | UnsupportedLookAndFeelException
			| NoSuchMethodException | SecurityException e) {

			new GUIErrorHandler(e);
		}
		setVisible(true);

		if (deserializationWarning) {
			JOptionPane.showMessageDialog(
				this,
				"You must be using a new version of Hook Any Text. "
					+ "Due to version incompatibilities, "
					+ "certain settings may have been reset."
			);
		}
	}

	private void appendMenuBar(History observedHistory) {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(getFileMenu());
		menuBar.add(getEditMenu(observedHistory));
		menuBar.add(getToolsMenu());
		menuBar.add(getAboutMenu());

		setJMenuBar(menuBar);
	}

	private JMenu getFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Exit");
		acts.setExitItemAction(exitItem);
		fileMenu.add(exitItem);
		return fileMenu;
	}

	private JMenu getEditMenu(History observedHistory) {
		JMenu editMenu = new JMenu("Edit");

		JMenuItem previousLineItem = new JMenuItem("Previous Line");
		previousLineItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_PAGE_UP, ActionEvent.ALT_MASK
		));
		editMenu.add(previousLineItem);

		JMenuItem nextLineItem = new JMenuItem("Next Line");
		nextLineItem.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_PAGE_DOWN, ActionEvent.ALT_MASK
		));
		editMenu.add(nextLineItem);

		JMenuItem historyItem = new JMenuItem("History");
		final HistoryDialog historyDialog =
			new HistoryDialog(this, observedHistory, hp);

		acts.setDialog(historyItem, historyDialog);
		acts.setPreviousLineItemAction(previousLineItem, historyDialog);
		acts.setNextLineItemAction(nextLineItem, historyDialog);

		editMenu.add(historyItem);

		return editMenu;
	}

	private JMenu getToolsMenu() {
		JMenu toolsMenu = new JMenu("Tools");
		JMenuItem optionsItem = new JMenuItem("Options");
		OptionsDialog optionsDialog = new OptionsDialog(opts);
		acts.setDialog(optionsItem, optionsDialog);
		toolsMenu.add(optionsItem);
		return toolsMenu;
	}

	private JMenu getAboutMenu() {
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About Hook Any text");
		acts.setAboutItemAction(aboutItem);
		helpMenu.add(aboutItem);
		return helpMenu;
	}

	private void appendFrameContent()
		throws NoSuchMethodException, SecurityException {

		JScrollPane areaScrollPane = new JScrollPane(convertedStringsArea);
		areaScrollPane.setVerticalScrollBarPolicy(
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
		);
		add(areaScrollPane);
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		History history = (History) o;
		convertedStringsArea.setText(history.getLast().getOutput());
	}

	/**
	 * Setter on the content of the text area.
	 *
	 * @param content
	 * 			The content of the text area.
	 */
	public synchronized void setTextAreaContent(String content) {
		convertedStringsArea.setText(content);
	}

}
