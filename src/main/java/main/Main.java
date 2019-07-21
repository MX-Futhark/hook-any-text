package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

import javax.swing.JOptionPane;

import gui.views.MainWindow;
import hexcapture.HexPipeCompleter;
import hexcapture.HexSelectionsContentCache;
import hextostring.HexProcessor;
import hextostring.history.History;
import main.options.Options;
import main.options.annotations.CommandLineArgument;
import main.utils.ExceptionUtils;
import main.utils.IOUtils;
import main.utils.ReflectionUtils;

/**
 * Standard main class of the program.
 *
 * @author Maxime PIA
 */
public class Main {

	private static MainOptions opts;
	private static final HexPipeCompleter hpc = new HexPipeCompleter();
	private static boolean EXPECT_OPTIONS_REFRESH = false;

	private static void attachSerializeOnClose() {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					if (!Main.EXPECT_OPTIONS_REFRESH) {
						saveOptions();
						hpc.closeHandle(false);
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}

	private static void saveOptions() throws IOException {
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

	// The behavior of this method is as follows:
	//  - If a new options has been introduces in opts, a new instance of this
	//    option class is created using a default constructor.
	//  - If a CommandLineArgument annotated field is null due to version
	//    inconsistencies, the default value for this field is fetched.
	//    If no default value can be found (which should never happen),
	//    the containing object is replaced by a new instance of its class.
	//
	// If this method throws an exception, a new instance of MainOptions should
	// be used instead of opts.
	private static boolean correctVersionIncompatibilities(MainOptions opts)
		throws IllegalArgumentException, IllegalAccessException,
		InstantiationException, SecurityException {

		boolean incompatibilityDetected = false;

		Field[] optFields = opts.getClass().getDeclaredFields();
		for (Field optField : optFields) {
			if (!Options.class.equals(optField.getType().getSuperclass())) {
				continue;
			}
			optField.setAccessible(true);
			if (optField.get(opts) == null) {
				// handling new option types
				incompatibilityDetected = true;
				optField.set(opts, optField.getClass().newInstance());
			} else {
				// handling new configurable fields
				List<Field> configurableFields =
					ReflectionUtils.getAnnotatedFields(
					optField.getType(),
					CommandLineArgument.class
				);
				Options subOpts = (Options) optField.get(opts);
				for (Field configurableField : configurableFields) {
					configurableField.setAccessible(true);
					if (configurableField.get(subOpts) == null) {
						incompatibilityDetected = true;
						try {
							configurableField.set(
								subOpts,
								subOpts.getFieldDefaultValue(configurableField)
							);
						} catch (NoSuchFieldException e) {
							optField
								.set(opts, optField.getClass().newInstance());
							continue;
						}
					}
				}
			}
		}
		opts.updateSubOptionsSet();

		return incompatibilityDetected;
	}

	/**
	 * Serializes the current options to disk.
	 * @throws IOException
	 */
	public static void commitOptions () throws IOException {
		Main.saveOptions();
	}

	/**
	 * Refreshes the UI based on the current options.
	 * If called without an complete active pipe with selectionConverter.lua,
	 * the program needs to be restarted manually.
	 * @throws IOException
	 */
	public static void refreshOptions () throws IOException {
		EXPECT_OPTIONS_REFRESH = true;
		hpc.closeHandle(true);
		System.exit(0);
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

			boolean deserializationWarning = false;
			try {
				opts = restoreOptions();

				try {
					deserializationWarning =
						correctVersionIncompatibilities(opts);
				} catch (IllegalArgumentException | IllegalAccessException
					| InstantiationException | SecurityException e) {

					e.printStackTrace();
					deserializationWarning = true;
					opts = new MainOptions();
				}

				opts.parseArgs(args);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				deserializationWarning = true;
				opts = new MainOptions(args);
				saveOptions();
			}
			attachSerializeOnClose();

			hpc.updateConfig(opts.getHexOptions());

			HexSelectionsContentCache cache =
				new HexSelectionsContentCache(opts.getHexOptions());
			HexProcessor hp =
				new HexProcessor(opts.getConvertOptions(), history);
			MainWindow window =
				new MainWindow(hp, opts, history, deserializationWarning);
			final InputInterpreter ii = new InputInterpreter(
				opts, System.out, hp, window, opts.getHexOptions(), cache
			);

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
			JOptionPane.showMessageDialog(null, "An unexpected error occured." +
				" Please take a screenshot of this dialog and contact the " +
				"maintainer: \n" + ExceptionUtils.getPrintableStackTrace(e));
			e.printStackTrace();
			System.exit(1);
		}
	}

}
