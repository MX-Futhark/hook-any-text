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
	 * Assigns a mark to an object, with all the evaluations details.
	 *
	 * @param o
	 * 			The object to be evaluated.
	 * @return The result of the evaluation.
	 */
	EvaluationResult evaluate(O o);

}
