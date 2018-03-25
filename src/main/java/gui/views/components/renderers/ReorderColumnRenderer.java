package gui.views.components.renderers;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import gui.utils.Images;

/**
 * Renderer for table columns that reorders rows
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ReorderColumnRenderer extends DefaultTableCellRenderer {

	private static final JButton UP_BUTTON =
		new JButton(Images.resize(Images.TRIANGLE, 10, 10));
	private static final JButton DOWN_BUTTON =
		new JButton(Images.resize(Images.INVERTED_TRIANGLE, 10, 10));

	private int upColumnIndex;
	private int downColumnIndex;

	public ReorderColumnRenderer(int upColumnIndex, int downColumnIndex) {
		this.upColumnIndex = upColumnIndex;
		this.downColumnIndex = downColumnIndex;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column) {

		if (column == upColumnIndex) {
			return UP_BUTTON;
		} else if (column == downColumnIndex) {
			return DOWN_BUTTON;
		}

		return null;
	}

}
