package gui.views.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import gui.actions.OptionsDialogActions;
import gui.views.components.renderers.ReorderColumnRenderer;
import gui.views.components.renderers.ToggleButtonRenderer;
import gui.views.models.TableData;
import gui.views.models.TableViewModel;
import gui.views.models.TableViewModelOwner;
import hexcapture.HexSelection;
import hexcapture.HexSelections;

/**
 * Table representing all current hex selections
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class HexSelectionsTable extends TableViewModelOwner<HexSelection>
	implements Observer {

	private static final String[] COLUMN_NAMES =
		{"ID", "Start", "End", "Active", "", ""};

	private TableViewModel<HexSelection> viewModel;
	private HexSelections selections;
	private boolean updateOk = true;

	public HexSelectionsTable(HexSelections selections,
		 OptionsDialogActions acts) {

		acts.addObserver(this);
		selections.addObserver(this);
		this.selections = selections;

		final JRadioButton radio = new JRadioButton("", false);
		radio.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer booleanRenderer =
			new ToggleButtonRenderer(radio);

		ReorderColumnRenderer reorderRenderer = new ReorderColumnRenderer(
			COLUMN_NAMES.length - 2,
			COLUMN_NAMES.length - 1
		);

		viewModel = makeHexSelectionsViewModel(selections);
		acts.setTableModelActions(this, true);

		DefaultTableCellRenderer hexStringRenderer = getHexStringRenderer();
		getColumnModel().getColumn(1).setCellRenderer(hexStringRenderer);
		getColumnModel().getColumn(2).setCellRenderer(hexStringRenderer);

		getColumnModel().getColumn(3).setCellRenderer(booleanRenderer);
		acts.addTableRadioAction(this, 3);

		getColumnModel().getColumn(4).setCellRenderer(reorderRenderer);
		acts.addTableUpCellAction(this, 4);
		getColumnModel().getColumn(5).setCellRenderer(reorderRenderer);
		acts.addTableDownCellAction(this, 5);

		getColumnModel().getColumn(1).setPreferredWidth(180);
		getColumnModel().getColumn(2).setPreferredWidth(180);
		getColumnModel().getColumn(3).setPreferredWidth(50);
		getColumnModel().getColumn(4).setMaxWidth(20);
		getColumnModel().getColumn(5).setMaxWidth(20);

		TableColumn idCol = getColumnModel().getColumn(0);
		idCol.setMaxWidth(0);
		idCol.setMinWidth(0);
		idCol.setPreferredWidth(0);
		idCol.setResizable(false);

		putClientProperty("terminateEditOnFocusLost", true);
	}

	@Override
	public TableViewModel<HexSelection> getViewModel() {
		return viewModel;
	}

	private DefaultTableCellRenderer getHexStringRenderer() {
		final JLabel hexStringLabel = new JLabel();
		Font font = hexStringLabel.getFont();
		font = font.deriveFont(Collections.singletonMap(
			TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD)
		);
		hexStringLabel.setFont(font);

		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

				UIDefaults defaults = javax.swing.UIManager.getDefaults();

				if (isSelected) {
					hexStringLabel.setOpaque(true);
					hexStringLabel.setBackground(
						defaults.getColor("List.selectionBackground")
					);
					hexStringLabel.setForeground(Color.WHITE);
				} else {
					hexStringLabel.setOpaque(false);
					hexStringLabel.setForeground(Color.BLACK);
				}

				hexStringLabel.setText(
					"0x" + String.format("%02X", (Number) value)
				);
				return hexStringLabel;
			}
		};
	}

	private TableViewModel<HexSelection> makeHexSelectionsViewModel(
		final HexSelections selections) {

		return new TableViewModel<HexSelection>() {

			@Override
			public Object[] getArrayFromRowModel(HexSelection model) {
				if (model == null) {
					return new Object[4];
				}

				return new Object[]{
					model.getId(),
					model.getStart(),
					model.getEnd(),
					selections.isActive(model)
				};
			}

			@Override
			public void updateDataFromCoordinates(Object val, int row,
				int column) {

				switch (column) {
				case 0:
					OptionsDialogActions.setHexOptionsModified();
					selections.get(row).setId((Integer) val);
					break;
				case 1:
					selections.get(row).setStart((Long) val);
					break;
				case 2:
					selections.get(row).setEnd((Long) val);
					break;
				case 3:
					if (Boolean.TRUE.equals(val)) {
						OptionsDialogActions.setHexOptionsModified();
						selections.setActiveSelectionIndex(row);
					}
					break;
				}
			}

			@Override
			public Class<?> getColumnClass(int column) {
				if (column == 0) return Integer.class;
				if (column < 3) return Long.class;
				return Boolean.class;
			}

			@Override
			public String[] getColumnNames() {
				return COLUMN_NAMES;
			}

			@Override
			public TableData<HexSelection> getData() {
				return selections;
			}

			@Override
			public HexSelection getNewRowDefaultModel() {
				return new HexSelection(0, 0);
			}

			@Override
			public boolean isUserEditable(int row, int col) {
				return col == 3;
			}
		};
	}

	@Override
	public synchronized void update(Observable o, Object arg) {
		if (o instanceof OptionsDialogActions) {
			updateOk = !((OptionsDialogActions) o).isModificationsOngoing();
		}

		if (!updateOk) return;

		DefaultTableModel m = (DefaultTableModel) this.getModel();
		ArrayList<String> columnList =
			new ArrayList<>(Arrays.asList(COLUMN_NAMES));
		int idCol = columnList.indexOf("ID");
		int activeCol = columnList.indexOf("Active");

		for (int i = 0; i < m.getRowCount(); ++i) {
			int j = 0;
			HexSelection s = selections.getById(
				(Integer) m.getValueAt(i, idCol)
			);
			if (s != null) {
				for (Object v : viewModel.getArrayFromRowModel(s)) {
					// We want to show the selection updates in real time but
					// not change the active selection while the user is
					// using the UI.
					if ((arg == null ||
						!(arg.equals(HexSelections.EXTERNAL_UPDATE) &&
						j == activeCol)) && !v.equals(m.getValueAt(i, j))) {

						m.setValueAt(v, i, j);
					}
					++j;
				}
			}
		}
	}

}
