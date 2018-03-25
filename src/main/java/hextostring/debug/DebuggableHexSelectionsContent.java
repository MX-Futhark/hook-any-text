package hextostring.debug;

import java.util.List;

import hexcapture.HexSelectionsContentSnapshot;
import hextostring.format.DecorableList;

/**
 * Wraps the content of hex selections into an object containing the necessary
 * information for debugging
 *
 * @author Maxime PIA
 */
public class DebuggableHexSelectionsContent implements DebuggableStrings,
	DecorableList {

	private HexSelectionsContentSnapshot snapshot;
	private List<DebuggableStrings> orderedResults;

	private String decorationBefore = "";
	private String decorationBetween = "";
	private String decorationAfter = "";

	public DebuggableHexSelectionsContent(
		HexSelectionsContentSnapshot snapshot,
		List<DebuggableStrings> orderedResults) {

		this.snapshot = snapshot;
		this.orderedResults = orderedResults;

		if (snapshot.getSize() != orderedResults.size()) {
			throw new IllegalArgumentException("Incompatible sizes");
		}
	}

	@Override
	public void setDecorationBefore(String decorationBefore) {
		this.decorationBefore = decorationBefore;
	}

	@Override
	public void setDecorationBetween(String decorationBetween) {
		this.decorationBetween = decorationBetween;
	}

	@Override
	public void setDecorationAfter(String decorationAfter) {
		this.decorationAfter = decorationAfter;
	}

	@Override
	public void setLinesDecorations(String before, String after) {}

	@Override
	public DecorableList getDecorableList() {
		return this;
	}

	@Override
	public String toString(long debuggingFlags, int converterStrictness) {
		StringBuilder sb = new StringBuilder();
		int index = 0;
		boolean selectionsBoundsFlagOn =
			(debuggingFlags & DebuggingFlags.HEX_SELECTIONS_BOUNDS) > 0;
		boolean selectionsContentFlagOn =
			(debuggingFlags & DebuggingFlags.HEX_SELECTIONS_CONTENT) > 0;
		boolean selectionDebuggingOn =
			selectionsBoundsFlagOn || selectionsContentFlagOn;

		sb.append(decorationBefore);

		for (DebuggableStrings ds : orderedResults) {
			if (selectionDebuggingOn) {
				sb.append("[");
			}
			if (selectionsBoundsFlagOn) {
				sb.append(
					"#" + snapshot.getSelectionIdAt(index) +
					(snapshot.getActiveSelectionIndex() == index
						? " (active)"
						: "") +
					", from " +
					formatToHexString(snapshot.getSelectionStartAt(index)) +
					" to " +
					formatToHexString(snapshot.getSelectionEndAt(index))
				);
			}
			if (selectionsContentFlagOn) {
				sb.append(
					" (selected as: \"" + snapshot.getValueAt(index) + "\")"
				);
			}
			if (selectionDebuggingOn) {
				sb.append(":\n");
			}
			if (ds != null) {
				sb.append(ds.toString(debuggingFlags, converterStrictness));
			} else if (selectionDebuggingOn) {
				sb.append("<null>");
			}

			if (selectionDebuggingOn) {
				sb.append("]");
			}
			if (index != orderedResults.size() - 1) {
				sb.append(decorationBetween);
			}

			++index;
		}

		sb.append(decorationAfter);
		if (selectionDebuggingOn) {
			sb.append("\n");
		}

		return sb.toString();
	}

	private String formatToHexString(Number n) {
		return "0x" + String.format("%02X", n);
	}

}
