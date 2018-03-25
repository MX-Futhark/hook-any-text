package hexcapture;

import java.util.Map;

/**
 * Static snapshot of a mapping from a collection of selections to their content
 *
 * @author Maxime PIA
 */
public class HexSelectionsContentSnapshot {

	private static final String STRING_REPRESENTATION_SEPARATOR = "|";

	protected HexSelections selections;
	protected Map<HexSelection, String> selectionValues;

	protected HexSelectionsContentSnapshot() {}

	/**
	 * Getter on the value of a selection by its index
	 * @param index
	 * @return
	 */
	public String getValueAt(int index) {
		return selectionValues.get(selections.get(index));
	}

	/**
	 * Getter on the id of a selection by its index
	 * @param index
	 * @return
	 */
	public int getSelectionIdAt(int index) {
		return selections.get(index).getId();
	}

	/**
	 * Getter on the start index of a selection by its index
	 * @param index
	 * @return
	 */
	public long getSelectionStartAt(int index) {
		return selections.get(index).getStart();
	}

	/**
	 * Getter on the end index of a selection by its index
	 * @param index
	 * @return
	 */
	public long getSelectionEndAt(int index) {
		return selections.get(index).getEnd();
	}

	/**
	 * Getter on the index of the active selection
	 * @return
	 */
	public int getActiveSelectionIndex() {
		return selections.getActiveSelectionIndex();
	}

	/**
	 * Getter on the size of the selection collection
	 * @return
	 */
	public int getSize() {
		return selections.getAll().size();
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (HexSelection s : selections.getAll()) {
			res.append(selectionValues.get(s));
			if (s != selections.getAll().get(getSize() - 1)) {
				res.append(STRING_REPRESENTATION_SEPARATOR);
			}
		}
		return res.toString();
	}

}
