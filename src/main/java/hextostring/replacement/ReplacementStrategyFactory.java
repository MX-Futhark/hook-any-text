package hextostring.replacement;

/**
 * Factory for replacement strategies.
 *
 * @author Maxime PIA
 */
public class ReplacementStrategyFactory {

	private static SameTypeStrategy sameTypeStrategy = new SameTypeStrategy();
	private static HexToStrStrategy hexToStrStrategy = new HexToStrStrategy();

	/**
	 * Provides a fitting strategy given a replacement type.
	 *
	 * @param type
	 * 			The type of the replacements to apply in the strategy.
	 * @return A fitting strategy given the replacement type.
	 */
	public static ReplacementStrategy getStrategy(ReplacementType type) {
		switch (type) {
		case HEX2HEX : ;
		case STR2STR : return sameTypeStrategy;
		case HEX2STR : return hexToStrStrategy;
		default : return null;
		}
	}
}
