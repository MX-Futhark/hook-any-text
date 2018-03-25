package main.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Contains variables and utility methods concerning the properties of the
 * project.
 *
 * @author Maxime PIA
 */
public class ProjectProperties {

	public static final String PATH = "/about.prop";

	public static final String KEY_VERSION = "version";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_MAIL = "mail";

	private static Properties props = new Properties();
	private static boolean propsLoaded = false;

	private static void loadProperties() throws IOException {
		if (propsLoaded) return;

		InputStream versionStream =
			ProjectProperties.class.getResourceAsStream("/about.prop");
		props.load(versionStream);
		versionStream.close();
		propsLoaded = true;
	}

	/**
	 * Finds and returns a property.
	 *
	 * @param key
	 * 			The key for this property.
	 * @return The property corresponding to the key.
	 * @throws IOException
	 */
	public static String get(String key) throws IOException {
		loadProperties();
		return props.getProperty(key);
	}

}
