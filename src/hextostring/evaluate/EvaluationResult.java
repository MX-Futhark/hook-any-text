package hextostring.evaluate;

/**
 * Contains the information concerning the evaluation of an object.
 *
 * @author Maxime PIA
 */
public class EvaluationResult {

	private int mark;
	private String details;

	public EvaluationResult(int mark, String details) {
		this.mark = mark;
		this.details = details;
	}

	/**
	 * Getter on the mark obtained by the object.
	 *
	 * @return the mark obtained by the object.
	 */
	public int getMark() {
		return mark;
	}

	/**
	 * Getter on the details of the evaluation.
	 *
	 * @return the details of the evaluation.
	 */
	public String getDetails() {
		return details;
	}

}
