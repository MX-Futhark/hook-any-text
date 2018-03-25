package hexcapture;

import java.util.HashMap;

/**
 * Maps the current selections to their content
 *
 * @author Maxime PIA
 */
public class HexSelectionsContentCache extends HexSelectionsContentSnapshot {

	private HexOptions opts;
	private int lastAcknownledgedActiveSelectionIndex = 0;

	public HexSelectionsContentCache(HexOptions opts) {
		super();
		this.opts = opts;
		selections = opts.getHexSelections();
		selectionValues = new HashMap<>();
		for (HexSelection s : selections.getAll()) {
			if (!selectionValues.containsKey(s)) {
				selectionValues.put(s, "");
			}
		}
	}

	/**
	 * Returns a static snapshot of the current state of the cache
	 * @return
	 */
	public HexSelectionsContentSnapshot getSnapshot() {
		HexSelectionsContentCache snapshot =
			new HexSelectionsContentCache(opts);
		snapshot.selections = new HexSelections(true);
		for (HexSelection s : selections.getAll()) {
			HexSelection selectionCopy =
				new HexSelection(s.getStart(), s.getEnd(), s.getId());
			snapshot.selections.add(selectionCopy);
			snapshot.selectionValues.put(selectionCopy, selectionValues.get(s));
		}
		snapshot.selections.setActiveSelectionIndex(
			selections.getActiveSelectionIndex()
		);
		return snapshot;
	}

	/**
	 * Updates the content of a selection by its id
	 * @param id
	 * @param hex
	 */
	public void updateValueById(int id, String hex) {
		HexSelection s = selections.getById(id);
		if (s == null) {
			throw new IllegalArgumentException("No such selection id: " + id);
		}
		selectionValues.put(s, hex);
	}

	/**
	 * Takes the current active selection index into account
	 */
	public void updateLastAcknownledgedActiveSelectionIndex() {
		lastAcknownledgedActiveSelectionIndex =
			selections.getActiveSelectionIndex();
	}

	/**
	 * Returns the active selection index at the time of the last call to
	 * updateLastAcknownledgedActiveSelectionIndex
	 * @return
	 */
	public int getLastAcknownledgedActiveSelectionIndex() {
		return lastAcknownledgedActiveSelectionIndex;
	}
}
