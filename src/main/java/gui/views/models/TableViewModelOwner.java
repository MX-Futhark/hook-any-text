package gui.views.models;

import javax.swing.JTable;

/**
 * Interface for table associated to a view model
 *
 * @param <T> Type parameter of the view model
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public abstract class TableViewModelOwner<T> extends JTable {

	/**
	 * Getter on the view model
	 * @return
	 */
	public abstract TableViewModel<T> getViewModel();

}
