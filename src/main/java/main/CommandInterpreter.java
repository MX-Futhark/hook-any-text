package main;

import java.util.List;

import com.sun.jna.platform.win32.WinDef.HWND;

import gui.views.MainWindow;
import main.utils.WindowsUtils;

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
	 * TODO: list possible commands and arguments
	 *
	 * @param input
	 * 			A command without its colon at the beginning
	 * @return True if the command requests the program to exit.
	 */
	public boolean isExitAndExecute(String input) {
		String[] args = input.split(" ");
		String cmd = args[0];
		if (cmd.equals("exit")) {
			return true;
		} else if (cmd.equals("focus")) {
			synchronized(mainWindow) {
				mainWindow.setVisible(true);
				mainWindow.toFront();
			}
		} else if (cmd.equals("hide")) {
			List<HWND> windowsToHide = WindowsUtils.findByTitle(args[1]);
			for (HWND window : windowsToHide) {
				WindowsUtils.setVisible(window, false);
			}
		}
		return false;
	}
}
