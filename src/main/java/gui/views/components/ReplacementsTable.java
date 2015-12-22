package gui.views.components;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import gui.actions.OptionsDialogActions;
import gui.utils.Images;
import hextostring.replacement.ReplacementType;
import hextostring.replacement.Replacements;

/**
 * A table to store and modify replacements.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ReplacementsTable extends JTable {

	private static final String[] COLUMN_NAMES =
		{"Sequence", "Replacement", "Escape", "Regex", "Type", "", ""};

	public ReplacementsTable(Replacements replacements,
		 OptionsDialogActions acts) {

		acts.setReplacementTableModelActions(this, COLUMN_NAMES, replacements);

		final JCheckBox checkbox = new JCheckBox("", false);
		checkbox.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer booleanRenderer =
			new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

				checkbox.setSelected((Boolean) value);
				return checkbox;
			}
		};

		final JButton up = new JButton(Images.resize(Images.TRIANGLE, 10, 10));
		final JButton down =
			new JButton(Images.resize(Images.INVERTED_TRIANGLE, 10, 10));
		DefaultTableCellRenderer triangleRenderer =
			new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

				return column == COLUMN_NAMES.length - 1 ? down : up;
			}
		};

		getColumnModel().getColumn(2).setCellRenderer(booleanRenderer);
		getColumnModel().getColumn(3).setCellRenderer(booleanRenderer);
		getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(
			new JComboBox<ReplacementType>(ReplacementType.values())
		));
		getColumnModel().getColumn(5).setCellRenderer(triangleRenderer);
		acts.addReplacementTableUpCellAction(this, 5);
		getColumnModel().getColumn(6).setCellRenderer(triangleRenderer);
		acts.addReplacementTableDownCellAction(this, 6);

		getColumnModel().getColumn(2).setPreferredWidth(40);
		getColumnModel().getColumn(3).setPreferredWidth(40);
		getColumnModel().getColumn(5).setPreferredWidth(20);
		getColumnModel().getColumn(6).setPreferredWidth(20);

		putClientProperty("terminateEditOnFocusLost", true);
	}

}
