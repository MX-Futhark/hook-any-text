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
	private MainOptions opts;

	public CommandInterpreter(MainWindow mainWindow, MainOptions opts) {
		this.mainWindow = mainWindow;
		this.opts = opts;
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
		} else if (cmd.equals("attach")) {
			opts.getOCROptions().setAttachedPID(Long.parseLong(args[1]));
		}
		return false;
	}
}
