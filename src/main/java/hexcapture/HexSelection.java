package hexcapture;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Observable;

/**
 * Represents an actual or virtual selection in the hex view of Cheat Engine
 *
 * @author Maxime PIA
 */
public class HexSelection extends Observable implements Serializable {

	/**
	 * Backward-compatible with 0.9.0
	 */
	private static final long serialVersionUID = 1000000000009000000L;

	private static int nextId = 0;

	private long start;
	private long end;
	private int id;

	public HexSelection(long start, long end) {
		this.start = start;
		this.end = end;
		this.id = nextId++;
	}

	public HexSelection(long start, long end, int id) {
		this.start = start;
		this.end = end;
		this.id = id;
	}

	/**
	 * Getter on the start index of the selection
	 * @return
	 */
	public long getStart() {
		return start;
	}

	/**
	 * Setter on the start index of the selection
	 * @return
	 */
	public void setStart(long start) {
		this.start = start;
		forceNotifyObservers();
	}

	/**
	 * Getter on the end index of the selection
	 * @return
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * Setter on the end index of the selection
	 * @return
	 */
	public void setEnd(long end) {
		this.end = end;
		forceNotifyObservers();
	}

	/**
	 * Getter on the start id of the selection
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter on the id of the selection. Use with caution.
	 * @return
	 */
	public void setId(int id) {
		this.id = id;
	}

	private void forceNotifyObservers() {
		setChanged();
		notifyObservers();
	}

	private void readObject(ObjectInputStream in) throws IOException,
		ClassNotFoundException {

		in.defaultReadObject();
		if (id >= nextId) nextId = id + 1;
	}

}
