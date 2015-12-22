package main.history;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class History<I, O> extends Observable {

	public static final int HISTORY_MAX_SIZE = 100;

	private Deque<InputOutputPair<I, O>> content =
		new LinkedList<InputOutputPair<I, O>>();

	public synchronized InputOutputPair<I, O> getLast() {
		return content.peek();
	}

	public synchronized void add(I input, O output) {
		content.addFirst(new InputOutputPair<I, O>(input, output));
		if (content.size() > HISTORY_MAX_SIZE) {
			content.removeLast();
		}
		setChanged();
		notifyObservers();
	}

	@SuppressWarnings("unchecked")
	public synchronized List<InputOutputPair<I, O>> getContent() {
		return (List<InputOutputPair<I, O>>) content;
	}

}
