package hexcapture;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import gui.views.models.TableData;

/**
 * Represents a collection of virtual selections in the hex view of Cheat Engine
 * NOTE: unless specified otherwise, instances always make sure to contain at
 * least on selection an always have a active one.
 *
 * @author Maxime PIA
 */
public class HexSelections extends Observable
	implements TableData<HexSelection>, Serializable, Observer {

	public static final byte EXTERNAL_UPDATE = 0b1;

	/**
	 * Backward-compatible with 0.9.0
	 */
	private static final long serialVersionUID = 1000000000009000000L;

	private List<HexSelection> orderedSelections = new LinkedList<>();
	private int activeSelectionIndex;

	public HexSelections() {
		add(new HexSelection(0, 0));
	}

	public HexSelections(boolean noZeroLengthCheck) {
		if (!noZeroLengthCheck) {
			add(new HexSelection(0, 0));
		}
	}

	@Override
	public HexSelection get(int index) {
		return orderedSelections.get(index);
	}

	@Override
	public List<HexSelection> getAll() {
		return new LinkedList<HexSelection>(orderedSelections);
	}

	@Override
	public void add(HexSelection s) {
		orderedSelections.add(s);
		activeSelectionIndex = orderedSelections.size() - 1;
		s.addObserver(this);
		forceNotifyObservers(null);
	}

	@Override
	public void remove(int index) {
		orderedSelections.remove(index);
		fixInconsistentState();
		forceNotifyObservers(null);
	}

	/**
	 * Setter on the index of the active selection
	 * @param index
	 */
	public void setActiveSelectionIndex(int index) {
		if (index < 0 || index >= orderedSelections.size()) {
			throw new IndexOutOfBoundsException();
		}

		activeSelectionIndex = index;
		forceNotifyObservers(null);
	}

	/**
	 * Getter on the index of the active selection
	 * @return
	 */
	public int getActiveSelectionIndex() {
		return activeSelectionIndex;
	}

	/**
	 * Determines whether a selection is active or not
	 * @param selection
	 * @return
	 */
	public boolean isActive(HexSelection selection) {
		return orderedSelections.indexOf(selection) == activeSelectionIndex;
	}

	/**
	 * Returns a selection by its id
	 * @param id
	 * @return
	 */
	public HexSelection getById(int id) {
		for (HexSelection s : orderedSelections) {
			if (s.getId() == id) return s;
		}
		return null;
	}

	/**
	 * Getter on the active selection
	 * @return
	 */
	public HexSelection getActive() {
		return orderedSelections.get(activeSelectionIndex);
	}

	@Override
	public void update(Observable o, Object arg) {
		forceNotifyObservers(EXTERNAL_UPDATE);
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		int count = 0;
		res.append("{");
		for (HexSelection s : orderedSelections) {
			res.append("{" + s.getId() + "," + s.getStart() + "," + s.getEnd() +
				"," + isActive(s) + "}");
			if (count < orderedSelections.size() - 1) {
				res.append(",");
			}
			++count;
		}
		res.append("}");
		return res.toString();
	}

	private void fixInconsistentState() {
		if (orderedSelections.isEmpty()) {
			add(new HexSelection(0, 0));
			activeSelectionIndex = 0;
		}
		if (activeSelectionIndex >= orderedSelections.size()) {
			activeSelectionIndex = orderedSelections.size() - 1;
		}
	}

	private void forceNotifyObservers(Object arg) {
		setChanged();
		notifyObservers(arg);
	}

	private void readObject(ObjectInputStream in) throws IOException,
		ClassNotFoundException {

		in.defaultReadObject();
		for (HexSelection s : orderedSelections) {
			s.addObserver(this);
		}
	}

}
