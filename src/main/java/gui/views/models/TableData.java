package gui.views.models;

import java.util.List;

/**
 * Data content of a table
 *
 * @param <T> What the rows represent
 *
 * @author Maxime PIA
 */
public interface TableData<T> {

	/**
	 * Getter on an element by index
	 * @param index
	 * @return
	 */
	public T get(int index);

	/**
	 * Getter on all elements
	 * @return
	 */
	public List<T> getAll();

	/**
	 * Adds an element
	 * @param value
	 */
	public void add(T value);

	/**
	 * Removes an element
	 * @param index
	 */
	public void remove(int index);

}
