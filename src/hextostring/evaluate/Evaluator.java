package hextostring.evaluate;

/**
 * Interface for evaluators.
 * Evaluators provide a way to give a mark to an object to determine whether
 * it is worth considering in the final output or not.
 *
 * @author Maxime PIA
 */
public interface Evaluator<O> {

	/**
	 * Provides a validity mark for a string.
	 *
	 * @param o
	 * 			The object to evaluate.
	 * @return A validity mark for the object in parameter.
	 */
	int evaluate(O o);

}
