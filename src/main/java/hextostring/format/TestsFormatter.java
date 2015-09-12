package hextostring.format;


/**
 * Formatter for testing conversion sessions.
 * Uses the same format as expected outputs files.
 *
 * @author Maxime PIA
 */
public class TestsFormatter extends Formatter {

	public TestsFormatter() {
		super("[", ",\n", "]", "\"", "\"");
	}

}
