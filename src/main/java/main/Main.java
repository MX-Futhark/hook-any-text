package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

import gui.views.MainWindow;
import hexcapture.HexPipeCompleter;
import hextostring.HexProcessor;
import hextostring.history.History;
import main.options.Options;
import main.utils.IOUtils;

/**
 * Standard main class of the program.
 *
 * @author Maxime PIA
 */
public class Main {

	private static void attachSerializeOnClose(final MainOptions opts,
		final HexPipeCompleter hpc) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	try {
		    		saveOptions(opts);
		    		hpc.closeHandle();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
		    }
		});
	}

	private static void saveOptions(MainOptions opts) throws IOException {
		FileOutputStream fos = new FileOutputStream(
			IOUtils.getFileInAppdataDirectory(Options.SERIALIAZATION_FILENAME)
		);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(opts);
		oos.close();
	}

	private static MainOptions restoreOptions() throws IOException,
		ClassNotFoundException {

		FileInputStream fin = new FileInputStream(
			IOUtils.getFileInAppdataDirectory(Options.SERIALIAZATION_FILENAME)
		);
		ObjectInputStream ois = new ObjectInputStream (fin);
		MainOptions res = (MainOptions) ois.readObject();
		ois.close();
		return res;
	}

	/**
	 * Starts a conversion session and displays the GUI.
	 *
	 * @param args
	 * 			See {@link main.MainOptions}
	 */
	public static void main(String[] args) {
		try {
			final History history = new History();
			HexPipeCompleter hpc = new HexPipeCompleter();

			MainOptions opts;
			try {
				opts = restoreOptions();
				opts.parseArgs(args);
			} catch (IOException | ClassNotFoundException e) {
				opts = new MainOptions(args);
			}
			attachSerializeOnClose(opts, hpc);

			hpc.updateConfig(opts.getHexOptions());

			HexProcessor hp =
				new HexProcessor(opts.getConvertOptions(), history);
			MainWindow window = new MainWindow(hp, opts, history);
			final InputInterpreter ii =
				new InputInterpreter(opts, System.out, hp, window);

			CompletionService<Boolean> ecs =
				new ExecutorCompletionService<Boolean>(new Executor() {

				@Override
				public void execute(Runnable command) {
					command.run();
				}
			});
			ecs.submit(new Runnable() {
				@Override
				public void run() {
					ii.start();
				}
			}, true);
			if (ecs.take().get() != null) {
				window.dispose();
			}

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
