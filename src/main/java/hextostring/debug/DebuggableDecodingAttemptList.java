package hextostring.debug;

import java.util.LinkedList;
import java.util.List;

/**
 * Wraps all the necessary information to debug an attempt at decoding a list
 * of lines whose encoding is unknown.
 *
 * @author Maxime PIA
 */
public class DebuggableDecodingAttemptList implements DebuggableStrings {

	private List<DebuggableDecodingAttempt> attempts = new LinkedList<>();

	public void addAttempt(DebuggableDecodingAttempt attempt) {
		attempts.add(attempt);
	}

	@Override
	public DebuggableLineList getDecorableList() {
		for (DebuggableDecodingAttempt attempt : attempts) {
			if (attempt.isValidEncoding()) {
				return attempt.getAttempt();
			}
		}
		return null;
	}

	@Override
	public String toString(long debuggingFlags, int converterStrictness) {
		StringBuilder sb = new StringBuilder();

		if ((debuggingFlags & DebuggingFlags.LINE_LIST_ENCODING_REJECTED) > 0) {
			sb.append("Lines with detected encoding: \n");
		}
		for (DebuggableDecodingAttempt attempt : attempts) {
			if (attempt.isValidEncoding()) {
				sb.append(
					attempt.toString(debuggingFlags, converterStrictness)
				);
				break;
			}
		}
		if ((debuggingFlags & DebuggingFlags.LINE_LIST_ENCODING_REJECTED) > 0) {
			sb.append("\nFailed attempts at decoding: \n");
			for (DebuggableDecodingAttempt attempt : attempts) {
				if (!attempt.isValidEncoding()) {
					sb.append(
						attempt.toString(debuggingFlags,converterStrictness)
					);
					sb.append("\n");
				}
			}
		}

		return sb.toString().trim();
	}

}
