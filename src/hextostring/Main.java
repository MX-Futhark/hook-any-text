package hextostring;

/**
 * Standard main class of the program.
 *
 * @author Maxime PIA
 */
public class Main {

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
