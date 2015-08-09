package hextostring.evaluate;

/**
 * Interface for evaluators.
 * Evaluators provide a way to give a mark to a string to determine whether
 * it is worth considering in the final output or not.
 *
 * @author Maxime PIA
 */
public interface Evaluator {

	/**
	 * Provides a validity mark for a string.
	 *
	 * @param s
	 * 			The string to evaluate.
	 * @return A validity mark for the string in parameter.
	 */
	int evaluate(String s);

}
