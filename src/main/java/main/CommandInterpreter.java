package main;

import gui.views.MainWindow;

/**
 * Interprets non-hexadecimal strings coming from the standard input.
 *
 * @author Maxime PIA
 */
public class CommandInterpreter {

	private MainWindow mainWindow;

	public CommandInterpreter(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	/**
	 * Executes a command.
	 *
	 * @param input
	 * 			A command without its colon at the beginning
	 * @return True if the command requests the program to exit.
	 */
	public boolean isExitAndExecute(String input) {
		if (input.equals("exit")) {
			return true;
		} else if (input.equals("focus")) {
			synchronized(mainWindow) {
				mainWindow.setVisible(true);
				mainWindow.toFront();
			}
		}
		return false;
	}
}
