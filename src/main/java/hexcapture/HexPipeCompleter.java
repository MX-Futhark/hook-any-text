package hexcapture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import main.utils.IOUtils;
import main.utils.ReflectionUtils;
import main.utils.StringUtils;

/**
 * Contains method enabling double-sided communication with the main lua script.
 *
 * @author Maxime PIA
 */
public class HexPipeCompleter {

	public static final String FILENAME = "hex_config";

	public static final String HAT_IS_RUNNING = "HAT_IS_RUNNING";

	/**
	 * Updates options affecting the behavior of the main lua script.
	 *
	 * @param hexOptions
	 * 			The option object containing the configuration variables.
	 *
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IOException
	 */
	public void updateConfig(HexOptions hexOptions)
		throws IllegalAccessException, IllegalArgumentException,
		InvocationTargetException, IOException {

		PrintWriter writer =
			new PrintWriter(IOUtils.getFileInTempDirectory(FILENAME));
		List<Method> getters =
			ReflectionUtils.getGetters(hexOptions.getClass());
		for (Method getter : getters) {
			writer.println(
				StringUtils.camelToScreamingSnake(getter.getName().substring(3))
					+ "=" + getter.invoke(hexOptions)
			);
		}
		writer.close();
	}

	/**
	 * Indicates to the main lua script that the program has been terminated.
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void closeHandle() throws FileNotFoundException, IOException {
		PrintWriter writer =
			new PrintWriter(IOUtils.getFileInTempDirectory(FILENAME));
		writer.println(HAT_IS_RUNNING + "=false");
		writer.close();

	}
}
