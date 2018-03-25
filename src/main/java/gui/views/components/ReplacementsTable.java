package gui.views.components;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import gui.actions.OptionsDialogActions;
import gui.views.components.renderers.ReorderColumnRenderer;
import gui.views.components.renderers.ToggleButtonRenderer;
import gui.views.models.TableData;
import gui.views.models.TableViewModel;
import gui.views.models.TableViewModelOwner;
import hextostring.replacement.Replacement;
import hextostring.replacement.ReplacementType;
import hextostring.replacement.Replacements;

/**
 * A table to store and modify replacements.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ReplacementsTable extends TableViewModelOwner<Replacement> {

	private static final String[] COLUMN_NAMES =
		{"Sequence", "Replacement", "Escape", "Regex", "Type", "", ""};

	private TableViewModel<Replacement> viewModel;

	public ReplacementsTable(Replacements replacements,
		 OptionsDialogActions acts) {

		final JCheckBox checkbox = new JCheckBox("", false);
		checkbox.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer booleanRenderer =
			new ToggleButtonRenderer(checkbox);

		ReorderColumnRenderer reorderRenderer = new ReorderColumnRenderer(
			COLUMN_NAMES.length - 2,
			COLUMN_NAMES.length - 1
		);

		viewModel = makeReplacementsViewModel(replacements);
		acts.setTableModelActions(this, false);

		getColumnModel().getColumn(2).setCellRenderer(booleanRenderer);
		getColumnModel().getColumn(3).setCellRenderer(booleanRenderer);
		getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(
			new JComboBox<ReplacementType>(ReplacementType.values())
		));
		getColumnModel().getColumn(5).setCellRenderer(reorderRenderer);
		acts.addTableUpCellAction(this, 5);
		getColumnModel().getColumn(6).setCellRenderer(reorderRenderer);
		acts.addTableDownCellAction(this, 6);

		getColumnModel().getColumn(2).setPreferredWidth(60);
		getColumnModel().getColumn(3).setPreferredWidth(60);
		getColumnModel().getColumn(5).setMaxWidth(20);
		getColumnModel().getColumn(6).setMaxWidth(20);

		putClientProperty("terminateEditOnFocusLost", true);
	}

	@Override
	public TableViewModel<Replacement> getViewModel() {
		return viewModel;
	}

	private TableViewModel<Replacement> makeReplacementsViewModel(
		final Replacements replacements) {

		return new TableViewModel<Replacement>() {

			@Override
			public Object[] getArrayFromRowModel(Replacement model) {
				if (model == null) {
					return new Object[5];
				}

				return new Object[]{
					model.getSequence(),
					model.getReplacement(),
					model.isEscapeCharacters(),
					model.isInterpretAsPattern(),
					model.getType()
				};
			}

			@Override
			public void updateDataFromCoordinates(Object val, int row,
				int column) {

				switch (column) {
				case 0:
					replacements.get(row).setSequence((String) val);
					break;
				case 1:
					replacements.get(row).setReplacement((String) val);
					break;
				case 2:
					replacements.get(row).setEscapeCharacters((Boolean) val);
					break;
				case 3:
					replacements.get(row).setInterpretAsPattern((Boolean) val);
					break;
				case 4:
					replacements.setType(
						replacements.get(row),
						(ReplacementType) val
					);
					break;
				}
			}

			@Override
			public Class<?> getColumnClass(int column) {
				if (column < 2) {
					return String.class;
				} else if (column == 4) {
					return ReplacementType.class;
				}
				return Boolean.class;
			}

			@Override
			public String[] getColumnNames() {
				return COLUMN_NAMES;
			}

			@Override
			public TableData<Replacement> getData() {
				return replacements;
			}

			@Override
			public Replacement getNewRowDefaultModel() {
				return new Replacement(ReplacementType.STR2STR);
			}

			@Override
			public boolean isUserEditable(int row, int col) {
				return true;
			}
		};
	}

}
