package main;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sun.jna.platform.win32.WinDef.HWND;

import gui.views.MainWindow;
import hexcapture.HexOptions;
import hexcapture.HexSelection;
import hexcapture.HexSelectionsContentCache;
import hextostring.HexProcessor;
import main.utils.WindowsUtils;

/**
 * Interprets all strings coming from the standard input.
 *
 * @author Maxime PIA
 */
public class InputInterpreter {

	private static final String EXIT = "exit";
	private static final String FOCUS_MAIN_WINDOW_CMD = "focus-main-window";
	private static final String HIDE_TERMINAL_CMD = "hide-terminal";
	private static final String ACKNOWNLEDGE_CONFIG_UPDATE =
		"acknownledge-config-update";
	private static final String UPDATE_ACTIVE_SELECTION =
		"update-active-selection";
	private static final String SET_SELECTIONS_CONTENT =
		"set-selections-content";

	private MainOptions opts;
	private PrintStream out;
	private HexProcessor hexProcessor;
	private MainWindow mainWindow;
	private HexSelectionsContentCache cache;

	public InputInterpreter(MainOptions opts, PrintStream out,
		HexProcessor hexProcessor, MainWindow mainWindow, HexOptions hexOptions,
		HexSelectionsContentCache cache) {

		this.opts = opts;
		this.out = out;
		this.hexProcessor = hexProcessor;
		this.mainWindow = mainWindow;
		this.cache = cache;
	}

	private void print(String message) {
		if (out != null) {
			out.println(message);
		}
	}

	/**
	 * Starts a conversion session.
	 * Strings are read from System.in, and may be piped into the program.
	 * Non-hexadecimal commands should start with a colon.
	 */
	public void start() {
		Scanner sc = new Scanner(System.in);
		String input;

		while (true) {
			try {
				input = sc.nextLine();
				if (input.equals(EXIT)) {
					break;
				} else {
					execute(input);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		sc.close();
	}

	/**
	 * Executes a command.
	 * TODO: list possible commands and arguments
	 *
	 * @param input
	 * 			A command without its colon at the beginning
	 */
	public void execute(String input) {
		String[] args = input.split(" ");
		String cmd = args[0];
		if (cmd.equals(FOCUS_MAIN_WINDOW_CMD)) {
			focusMainWindow();
		} else if (cmd.equals(HIDE_TERMINAL_CMD)) {
			hideTerminal(args[1]);
		} else if (cmd.equals(ACKNOWNLEDGE_CONFIG_UPDATE)) {
			acknownledgeConfigUpdate();
		} else if (cmd.equals(UPDATE_ACTIVE_SELECTION)) {
			updateLastAcknownledgedActiveSelection(
				Long.parseLong(args[1]),
				Long.parseLong(args[2])
			);
		} else if (cmd.equals(SET_SELECTIONS_CONTENT)) {
			Map<Integer, String> idToContent = new HashMap<>();
			for (int i = 1; i < args.length - 1; i += 2) {
				idToContent.put(Integer.parseInt(args[i]), args[i + 1]);
			}
			setSelectionsContent(idToContent);
		} else if (cmd.equals(EXIT)) {
			throw new IllegalArgumentException("Cannot exit from this method");
		} else {
			throw new IllegalArgumentException("Unknown command: " + cmd);
		}
	}

	private void setSelectionsContent(Map<Integer, String> idToContent) {
		for (Integer id : idToContent.keySet()) {
			try {
				cache.updateValueById(id, idToContent.get(id));
			} catch (IllegalArgumentException e) {
				// Attempt to update a non-acknowledged deleted selection
			}
		}
		String convertedInput =
			hexProcessor.convert(cache.getSnapshot(), false);
		if (opts.getConvertOptions().getDebuggingFlags() > 0) {
			print(convertedInput);
		}
	}

	private void focusMainWindow() {
		synchronized(mainWindow) {
			mainWindow.setVisible(true);
			mainWindow.toFront();
		}
	}

	private void hideTerminal(String title) {
		List<HWND> windowsToHide = WindowsUtils.findByTitle(title);
		for (HWND window : windowsToHide) {
			WindowsUtils.setVisible(window, false);
		}
	}

	private void acknownledgeConfigUpdate() {
		cache.updateLastAcknownledgedActiveSelectionIndex();
	}

	private void updateLastAcknownledgedActiveSelection(long start, long end) {
		try {
			HexSelection s = opts.getHexOptions().getHexSelections().get(
				cache.getLastAcknownledgedActiveSelectionIndex()
			);
			s.setStart(start);
			s.setEnd(end);
		} catch (Exception e) { /* this id is not available anymore */ }
	}

}
