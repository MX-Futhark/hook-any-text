package hextostring.history;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import hexcapture.HexSelectionsContentSnapshot;

public class History extends Observable {

	public static final int HISTORY_MAX_SIZE = 100;

	private Deque<InputOutputPair> content = new LinkedList<InputOutputPair>();

	public synchronized InputOutputPair getLast() {
		return content.peek();
	}

	public synchronized void add(HexSelectionsContentSnapshot input,
		String output) {

		content.addFirst(new InputOutputPair(input, output));
		if (content.size() > HISTORY_MAX_SIZE) {
			content.removeLast();
		}
		setChanged();
		notifyObservers();
	}

	@SuppressWarnings("unchecked")
	public synchronized List<InputOutputPair> getContent() {
		return (List<InputOutputPair>) content;
	}

}
