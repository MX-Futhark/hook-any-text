package hextostring.replacement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gui.views.models.TableData;

/**
 * A collection of replacements
 *
 * @author Maxime PIA
 */
public class Replacements
	implements TableData<Replacement>, Serializable {

	/**
	 * Backward-compatible with 0.7.0
	 */
	private static final long serialVersionUID = 00000000007000000L;

	private List<Replacement> orderedReplacements;
	private Map<ReplacementType, List<Replacement>> replacementsByType;

	public Replacements() {
		orderedReplacements = new ArrayList<>();
		replacementsByType = new HashMap<>();
		for (ReplacementType type : ReplacementType.values()) {
			replacementsByType.put(type, new LinkedList<Replacement>());
		}
	}

	/**
	 * Getter on a replacement by its index.
	 *
	 * @param index
	 * 			The index of the replacement.
	 * @return The replacement at the specified index.
	 */
	@Override
	public Replacement get(int index) {
		return orderedReplacements.get(index);
	}

	/**
	 * Getter on the replacements.
	 *
	 * @return The replacements.
	 */
	@Override
	public List<Replacement> getAll() {
		return new ArrayList<Replacement>(orderedReplacements);
	}

	/**
	 * Adds a replacement at the end of the list.
	 *
	 * @param r
	 * 			The replacement to add.
	 */
	@Override
	public void add(Replacement r) {
		orderedReplacements.add(r);
		replacementsByType.get(r.getType()).add(r);
	}

	/**
	 * Removes a replacement at the specified index.
	 *
	 * @param index
	 * 			The index of the replacement to remove.
	 */
	@Override
	public void remove(int index) {
		Replacement deleted = orderedReplacements.remove(index);
		replacementsByType.get(deleted.getType()).remove(deleted);
	}

	/**
	 * Empties the list.
	 */
	public void clear() {
		orderedReplacements.clear();
		for (ReplacementType type : replacementsByType.keySet()) {
			replacementsByType.get(type).clear();
		}
	}

	/**
	 * Get all replacements of a specified type.
	 *
	 * @param type
	 * 			The type of the replacements.
	 * @return All replacements of a specified type.
	 */
	public List<Replacement> getByType(ReplacementType type) {
		return replacementsByType.get(type);
	}

	/**
	 * Setter on the type of a replacement.
	 *
	 * @param r
	 * 			The replacement to modify.
	 * @param type
	 * 			The new type of the replacement.
	 */
	public void setType(Replacement r, ReplacementType type) {
		for (ReplacementType previoustType : replacementsByType.keySet()) {
			replacementsByType.get(previoustType).remove(r);
		}
		replacementsByType.get(type).add(r);
		r.setType(type);
	}

	/**
	 * Applies all the replacements of a given type.
	 *
	 * @param s
	 * 			The string to which replacements are applied.
	 * @param type
	 * 			The type of the replacements to use.
	 * @return The string to which replacements were applied.
	 */
	public String apply(String s, ReplacementType type) {
		String res = new String(s);
		for (Replacement r : replacementsByType.get(type)) {
			res = r.apply(res);
		}
		return res;
	}

}
