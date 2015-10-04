package main.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains IO utility methods.
 *
 * @author Maxime PIA
 */
public class IOUtils {

	public static final String SUBDIRECTORY = "HookAnyText";

	public static final Map<String, Path> DRICTORIES = new HashMap<>();

	private static final String[] TEMP_DIRECTORIES = {"TMP", "TEMP"};
	private static final String[] APPDATA_DIRECTORIES = {"APPDATA"};

	/**
	 * Finds a file in the user's temporary directory.
	 *
	 * @param filename
	 * 			The name of the file.
	 * @return The file "filename" contained in the user's temporary directory.
	 * @throws IOException
	 */
	public static File getFileInTempDirectory(String filename)
		throws IOException {

		return getFileInDirectory(filename, TEMP_DIRECTORIES);
	}

	/**
	 * Finds a file in the user's appdata directory.
	 *
	 * @param filename
	 * 			The name of the file.
	 * @return The file "filename" contained in the user's temporary directory.
	 * @throws IOException
	 */
	public static File getFileInAppdataDirectory(String filename)
		throws IOException {

		return getFileInDirectory(filename, APPDATA_DIRECTORIES);
	}

	private static File getFileInDirectory(String filename,
		String ... possibleDirs) throws IOException {

		initializePath(possibleDirs);
		return getFileInFolder(DRICTORIES.get(possibleDirs[0]), filename);
	}

	private static void initializePath(String ... envVarNames)
		throws FileNotFoundException {

		String keyEnvVarName = envVarNames[0];

		Path p = DRICTORIES.get(keyEnvVarName);
		if (p != null) return;

		String directory = null;
		for (int i = 0; i < envVarNames.length && directory == null; ++i) {
			directory = System.getenv(envVarNames[i]);
		}
		if (directory == null) {
			throw new FileNotFoundException(
				"No " + keyEnvVarName + " directory found."
			);
		}
		p = Paths.get(directory, SUBDIRECTORY);
		new File(p.toString()).mkdirs();
		DRICTORIES.put(keyEnvVarName, p);
	}

	private static File getFileInFolder(Path p, String filename)
		throws IOException {

		Path path = Paths.get(p.toString(), filename);
		File f = new File(path.toString());
		f.createNewFile();
		return f;
	}
}
