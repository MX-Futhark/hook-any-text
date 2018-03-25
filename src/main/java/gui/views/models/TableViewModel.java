package gui.views.models;

/**
 * View model of a table
 *
 * @param <T> Type represented by a row of the table
 *
 * @author Maxime PIA
 */
public interface TableViewModel<T> {

	/**
	 * Returns the content of a row in array form
	 * NOTE: Always returns an array of the right length, even if T is null
	 * @param model The model from which the array is constructed
	 * @return
	 */
	public Object[] getArrayFromRowModel(T model);

	/**
	 * Updates the relevant model given coordinates in the table
	 * @param val
	 * @param row
	 * @param column
	 */
	public void updateDataFromCoordinates(Object val, int row, int column);

	/**
	 * Returns the class corresponding to a column
	 * @param column
	 * @return
	 */
	public Class<?> getColumnClass(int column);

	/**
	 * Provides the header of the table
	 * @return
	 */
	public String[] getColumnNames();

	/**
	 * Getter on the underlying data of the table
	 * @return
	 */
	public TableData<T> getData();

	/**
	 * Provides the default model for new rows
	 * @return
	 */
	public T getNewRowDefaultModel();

	/**
	 * Determines whether or not a cell is editable
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isUserEditable(int row, int col);

}
