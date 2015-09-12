package hextostring;

import java.net.URL;

/**
 * Standard main class of the program.
 *
 * @author Maxime PIA
 */
public class Main {

	/**
	 * Finds the path of a resource.
	 *
	 * @param test
	 * 			True if the resource is related to tests.
	 * @return The path of the requested resource.
	 */
	public static String getResourcePath(boolean test, String resource) {
		URL currentURL =
			Main.class.getProtectionDomain().getCodeSource().getLocation();
		return currentURL.getPath() + "../../src/" + (test ? "test" : "main")
			+ "/resources/" + resource;
	}

	/**
	 * Starts a conversion session.
	 *
	 * @param args
	 * 			See {@link hextostring.Options}
	 */
	public static void main(String[] args) {
		try {
			HexProcessor hp = new HexProcessor();
			hp.start(new Options(args), System.out);
		} catch (Exception e) {
			System.exit(1);
		}
	}

}
