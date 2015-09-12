package hextostring.format;

/**
 * Determines which formatter to provide.
 *
 * @author Maxime PIA
 */
public class FormatterFactory {

	private static StandardFormatter standardFormatterInstance =
		new StandardFormatter();
	private static TestsFormatter testsFormatterInstance =
		new TestsFormatter();

	/**
	 * Provides a formatter based on whether the current conversion session
	 * is intended for tests or not.
	 *
	 * @param tests
	 * 			True to use the same format as expected outputs
	 * 			during test runs. See {@link hextostring.tests.TestsLauncher}
	 * @return A formatter.
	 */
	public static Formatter getFormatterInstance(boolean tests) {
		return tests ? testsFormatterInstance : standardFormatterInstance;
	}

}
