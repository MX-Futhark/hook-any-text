package hextostring.format;

/**
 * Determines which formatter to provide.
 *
 * @author Maxime PIA
 */
public class FormatterFactory {

	public static final String STANDARD = "Standard";
	public static final String TESTS = "Test";
	public static final String MULTI_CONTENT = "Multi-content";

	private static StandardFormatter standardFormatterInstance =
		new StandardFormatter();
	private static TestsFormatter testsFormatterInstance =
		new TestsFormatter();
	private static MultiContentFormatter multiContentFormatterInstance =
		new MultiContentFormatter();

	/**
	 * Provides a formatter based on whether the current conversion session
	 * is intended for tests or not.
	 *
	 * @param tests
	 * 			True to use the same format as expected outputs
	 * 			during test runs. See {@link hextostring.tests.TestsLauncher}
	 * @return A formatter.
	 */
	public static Formatter getFormatterInstance(String type) {
		if (type.equals(STANDARD)) {
			return standardFormatterInstance;
		}
		if (type.equals(TESTS)) {
			return testsFormatterInstance;
		}
		if (type.equals(MULTI_CONTENT)) {
			return multiContentFormatterInstance;
		}
		throw new IllegalArgumentException("Unknown formatter type: " + type);
	}

}
