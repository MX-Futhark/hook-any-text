package main;

import java.io.PrintStream;
import java.util.Scanner;

import gui.views.MainWindow;
import hextostring.HexProcessor;

/**
 * Interprets all strings coming from the standard input.
 *
 * @author Maxime PIA
 */
public class InputInterpreter {

	private MainOptions opts;
	private PrintStream out;
	private HexProcessor hexProcessor;
	private CommandInterpreter cmdInterpreter;

	public InputInterpreter(MainOptions opts, PrintStream out,
		HexProcessor hexProcessor, MainWindow mainWindow) {

		this.opts = opts;
		this.out = out;
		this.hexProcessor = hexProcessor;
		this.cmdInterpreter = new CommandInterpreter(mainWindow, opts);
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
				if (input.startsWith(":")) {
					if (cmdInterpreter.isExitAndExecute(input.substring(1))) {
						break;
					}
				} else {
					String convertedInput = hexProcessor.convert(input, false);
					if (opts.getConvertOptions().getDebuggingFlags() > 0) {
						print(convertedInput);
					}
				}
			} catch (Exception e) {
				if (opts.getConvertOptions().getDebuggingFlags() > 0) {
					e.printStackTrace();
				} else {
					print(e.getMessage());
				}
			}
		}

		sc.close();
	}

}
