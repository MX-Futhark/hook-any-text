package gui.views.components.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderer for toggle buttons
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ToggleButtonRenderer extends DefaultTableCellRenderer {

	private JToggleButton toggleButton;

	public ToggleButtonRenderer(JToggleButton toggleButton) {
		this.toggleButton = toggleButton;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
		boolean isSelected, boolean hasFocus, int row, int column) {

		toggleButton.setSelected((Boolean) value);
		return toggleButton;
	}

}
