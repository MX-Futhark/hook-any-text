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

import gui.views.MainWindow;
import hexcapture.HexPipeCompleter;
import hextostring.HexProcessor;
import hextostring.history.History;
import main.options.Options;
import main.options.annotations.CommandLineArgument;
import main.utils.IOUtils;
import main.utils.ReflectionUtils;

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
			boolean serializationWarning = false;
			try {
				opts = restoreOptions();

				try {
					serializationWarning =
						correctVersionIncompatibilities(opts);
				} catch (IllegalArgumentException | IllegalAccessException
					| InstantiationException | SecurityException e) {

					e.printStackTrace();
					serializationWarning = true;
					opts = new MainOptions();
				}

				opts.parseArgs(args);
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				serializationWarning = true;
				opts = new MainOptions(args);
			}
			attachSerializeOnClose(opts, hpc);

			hpc.updateConfig(opts.getHexOptions());

			HexProcessor hp =
				new HexProcessor(opts.getConvertOptions(), history);
			MainWindow window =
				new MainWindow(hp, opts, history, serializationWarning);
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
