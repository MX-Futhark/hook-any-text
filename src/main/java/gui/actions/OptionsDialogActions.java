package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import gui.utils.GUIErrorHandler;
import gui.views.OptionsDialog;
import gui.views.components.ReplacementsTable;
import hexcapture.HexOptions;
import hexcapture.HexPipeCompleter;
import hextostring.replacement.Replacement;
import hextostring.replacement.ReplacementType;
import hextostring.replacement.Replacements;
import main.MainOptions;
import main.options.Options;
import main.utils.ReflectionUtils;

/**
 * Contains all the actions for the view OptionsDialog.
 *
 * @author Maxime PIA
 */
public class OptionsDialogActions {


	// Equivalent of runnable that can throw exceptions.
	private interface Command {
		void execute() throws Exception;
	}

	// Undoable command pattern.
	private interface ModificationAction {
		void apply() throws Exception;
		void cancel() throws Exception;
	}

	private static final Queue<ModificationAction> optionModifications =
		new LinkedList<>();

	private static final HexPipeCompleter ccw =
		new HexPipeCompleter();
	private static boolean hexOptionsModified = false;


	private OptionsDialog optionsDialog;

	public OptionsDialogActions(OptionsDialog optionsDialog) {
		this.optionsDialog = optionsDialog;
	}


	/**
	 * Sets the close action for the dialog.
	 */
	public void setCloseAction() {
		optionsDialog.setDefaultCloseOperation(
			WindowConstants.DO_NOTHING_ON_CLOSE
		);
		optionsDialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (optionModifications.size() == 0 ||
					JOptionPane.showConfirmDialog(
						optionsDialog,
						"Do you wish to discard the changes?",
						"Closing options dialog",
						JOptionPane.YES_NO_OPTION
					) == JOptionPane.YES_OPTION) {

					closeWindow();
				}
			}
		});
	}

	/**
	 * Sets the action for a checkbox representing the state of a flag.
	 *
	 * @param flagCheckBox
	 * 			The checkbox.
	 * @param flagField
	 * 			The field containing the flags.
	 * @param valueField
	 * 			The field representing the flag.
	 * @param opts
	 * 			The option object containing the field containing the flags.
	 */
	public void setFlagCheckboxAction(final JCheckBox flagCheckBox,
		final Field flagField, final Field valueField, final Options opts) {

		final Method flagSetter =
			ReflectionUtils.getFlagSetter(opts.getClass(), flagField);
		final Method flagGetter =
			ReflectionUtils.getGetter(opts.getClass(), flagField);

		OptionChangeListener flagCheckboxListener = new OptionChangeListener(
			flagCheckBox, opts, new Command() {
				@Override
				public void execute() throws Exception {
					flagSetter.invoke(
						opts,
						valueField.getLong(null),
						flagCheckBox.isSelected()
					);
				}
			}, new Command() {
				@Override
				public void execute() throws Exception {
					flagCheckBox.setSelected(
						((long) flagGetter.invoke(opts)
							& valueField.getLong(null)) > 0
					);
				}
			}
		);

		flagCheckBox.addActionListener(flagCheckboxListener);
	}

	/**
	 * Sets the action for a component representing an option.
	 *
	 * @param elt
	 * 			The component.
	 * @param optionField
	 * 			The field representing an option.
	 * @param opts
	 * 			The option object containing the field representing an option.
	 */
	@SuppressWarnings("rawtypes")
	public void setOptionComponentAction(JComponent elt, Field optionField,
		Options opts) {

		OptionChangeListener genericListener =
			new OptionChangeListener(elt, opts, optionField);
		if (elt instanceof JCheckBox) {
			((JCheckBox) elt).addActionListener(genericListener);
		} else if (elt instanceof JComboBox) {
			((JComboBox) elt).addActionListener(genericListener);
		} else if (elt instanceof JSpinner) {
			((JSpinner) elt).addChangeListener(genericListener);
		} else {
			throw new IllegalArgumentException(
				"Invalid option modifier component: " + elt
			);
		}
	}

	/**
	 * Sets the action for the "apply" button.
	 *
	 * @param applyButton
	 * 			The button.
	 * @param opts
	 * 			The option object affected by the modifications.
	 */
	public void setApplyButtonAction(JButton applyButton,
		final MainOptions opts) {

		applyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ev) {
				applyAllOptionModifications(opts);
			}
		});
	}

	/**
	 * Sets the action for the "ok" button.
	 *
	 * @param okButton
	 * 			The button.
	 * @param opts
	 * 			The option object affected by the modifications.
	 */
	public void setOKButtonAction(JButton okButton, final MainOptions opts) {

		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ev) {
				applyAllOptionModifications(opts);
				optionsDialog.setVisible(false);
			}
		});
	}

	/**
	 * Sets the action for the "cancel" button.
	 *
	 * @param cancelButton
	 * 			The button.
	 * @param opts
	 * 			The option object affected by the modifications.
	 */
	public void setCancelButtonAction(JButton cancelButton) {

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent ev) {
				closeWindow();
			}
		});
	}

	/**
	 * Sets the action for the "add replacement" button.
	 *
	 * @param addReplacementButton
	 * 			The button.
	 * @param tableModel
	 * 			The model of the table containing the replacements.
	 */
	public void setAddReplacementButtonAction(JButton addReplacementButton,
		final DefaultTableModel tableModel) {

		addReplacementButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				tableModel.addRow(
					new Object[]{new Replacement(ReplacementType.STR2STR)}
				);
			}
		});
	}

	/**
	 * Sets the action for the "delete selection" button.
	 *
	 * @param addReplacementButton
	 * 			The button.
	 * @param tableModel
	 * 			The model of the table containing the replacements.
	 */
	public void setDeleteSelectionButtonAction(JButton deleteSelectionButton,
		final JTable table) {

		deleteSelectionButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedInterval = table.getSelectedRows();
				int firstRowIndex = selectedInterval[0];
				int intervalSize = selectedInterval.length;
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				for (int i = 0; i < intervalSize; ++i) {
					model.removeRow(firstRowIndex);
				}
			}
		});
	}

	private static Object[] replacementToArray(Replacement r) {
		return new Object[]{
			r.getSequence(),
			r.getReplacement(),
			r.isEscapeCharacters(),
			r.isInterpretAsPattern(),
			r.getType()
		};
	}

	/**
	 * Sets the actions associated to the model of the replacement table.
	 *
	 * @param table
	 * 			The table containing the replacements.
	 * @param columnNames
	 * 			The titles of the columns.
	 * @param replacements
	 * 			The replacements managed by the table.
	 */
	@SuppressWarnings("serial")
	public void setReplacementTableModelActions(final ReplacementsTable table,
		String[] columnNames, final Replacements replacements) {

		table.setModel(new DefaultTableModel(columnNames, 0) {

			private List<Object[]> data = new ArrayList<>();
			private int columnCount =
				replacementToArray(new Replacement(null)).length;

			public DefaultTableModel init() {
				for (Replacement replacement : replacements.getAll()) {
					Object[] newRow = replacementToArray(replacement);
					data.add(newRow);
					visualAddRow(newRow);
				}
				return this;
			}

			@Override
			public void insertRow(final int index, final Object[] rowData) {
				final Replacement r = (Replacement) rowData[0];
				Object[] newRow = replacementToArray(r);
				data.add(index, newRow);
				visualInsertRow(index, newRow);

				optionModifications.add(new ModificationAction() {
					@Override
					public void cancel() throws Exception {
						data.remove(index);
						visualRemoveRow(index);
					}

					@Override
					public void apply() throws Exception {
						replacements.add(r);
					}
				});
			}

			@Override
			public void addRow(Object[] rowData) {
				insertRow(getRowCount(), rowData);
			}

			@Override
			public void removeRow(final int row) {
				final Object[] deletedReplacement = data.remove(row);
				super.removeRow(row);

				optionModifications.add(new ModificationAction() {
					@Override
					public void cancel() throws Exception {
						data.add(row, deletedReplacement);
						visualInsertRow(row, deletedReplacement);
					}

					@Override
					public void apply() throws Exception {
						replacements.remove(row);
					}
				});
			}

			@Override
			public void setValueAt(final Object val, final int row,
				final int column) {

				if (column >= columnCount) return;

				final Object previousVal = data.get(row)[column];
				data.get(row)[column] = val;
				visualSetValueAt(val, row, column);

				optionModifications.add(new ModificationAction() {
					@Override
					public void cancel() throws Exception {
						data.get(row)[column] = previousVal;
						visualSetValueAt(previousVal, row, column);
					}

					@Override
					public void apply() throws Exception {
						switch (column) {
						case 0 : replacements.get(row)
							.setSequence((String) val);
						break;
						case 1 : replacements.get(row)
							.setReplacement((String) val);
						break;
						case 2 : replacements.get(row)
							.setEscapeCharacters((Boolean) val);
						break;
						case 3 : replacements.get(row)
							.setInterpretAsPattern((Boolean) val);
						break;
						case 4 : replacements.setType(
							replacements.get(row),
							(ReplacementType) val
						);
						break;
						}
					}
				});
			}

			@Override
		    public Object getValueAt(int row, int col) {
				return col < columnCount ? data.get(row)[col] : null;
		    }

			@Override
		    public Class<?> getColumnClass(int col) {
		        if (col < 2) {
		            return String.class;
		        } else if (col == 4) {
		        	return ReplacementType.class;
		        }
		        return Boolean.class;
		    }

			@Override
			public boolean isCellEditable(int row, int col) {
				return col < columnCount;
			}

			public void visualAddRow(Object[] content) {
				super.addRow(content);
			}

			public void visualInsertRow(int row, Object[] content) {
				super.insertRow(row, content);
			}

			public void visualRemoveRow(int row) {
				super.removeRow(row);
			}

			public void visualSetValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
			}

		}.init());
	}

	/**
	 * Sets the action for the upwards reordering button of the replacement
	 * table.
	 *
	 * @param table
	 * 			The table containing the replacements.
	 * @param columnIndex
	 * 			The index of the column containing the button.
	 */
	public void addReplacementTableUpCellAction(final ReplacementsTable table,
		final int columnIndex) {

		addReplacementTableReorderCellAction(table, columnIndex, true);
	}

	/**
	 * Sets the action for the downwards reordering button of the replacement
	 * table.
	 *
	 * @param table
	 * 			The table containing the replacements.
	 * @param columnIndex
	 * 			The index of the column containing the button.
	 */
	public void addReplacementTableDownCellAction(final ReplacementsTable table,
		final int columnIndex) {

		addReplacementTableReorderCellAction(table, columnIndex, false);
	}

	private void addReplacementTableReorderCellAction(
		final ReplacementsTable table, final int columnIndex,
		final boolean up) {

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				if ((up && row == 0)
					|| (!up && row == table.getRowCount() - 1)) {

					return;
				}
				int neighbor = row + (up ? -1 : 1);

				int col = table.columnAtPoint(evt.getPoint());
				if (col == columnIndex) {
					DefaultTableModel model =
						(DefaultTableModel) table.getModel();
					for (int i = 0; i < model.getColumnCount(); ++i) {
						Object value = model.getValueAt(neighbor, i);
						model.setValueAt(model.getValueAt(row, i), neighbor, i);
						model.setValueAt(value, row, i);
					}
				}
			}
		});
	}

	private void applyAllOptionModifications(MainOptions opts) {
		try {
			for (ModificationAction modif : optionModifications) {
				modif.apply();
			}
			if (hexOptionsModified) {
				ccw.updateConfig(opts.getHexOptions());
			}
			optionModifications.clear();
			hexOptionsModified = false;
		} catch (Exception e) {
			new GUIErrorHandler(e);
		}
	}

	private void cancelAllModifications() {
		try {
			ListIterator<ModificationAction> iom =
				new ArrayList<ModificationAction>(optionModifications)
					.listIterator(optionModifications.size());
			while (iom.hasPrevious()) {
				iom.previous().cancel();
			}
			optionModifications.clear();
		} catch (Exception e) {
			new GUIErrorHandler(e);
		}
	}

	private void closeWindow() {
		cancelAllModifications();
		optionsDialog.setVisible(false);
	}

	// Generic listener for components associated an option.
	private class OptionChangeListener implements ActionListener,
		ChangeListener {

		private boolean acceptEvent = true;

		private JComponent elt;
		private Options opts;

		private ModificationAction optionModification;

		private OptionChangeListener(final JComponent elt,
			final Options opts, final Field optionField,
			final Command applyCommand,
			final Command cancelCommand) {

			this.elt = elt;
			this.opts = opts;

			this.optionModification = new ModificationAction() {
				@Override
				public void cancel() throws Exception {

					(cancelCommand != null
						? cancelCommand
						: getDefaultCancelCommand(
							ReflectionUtils.getGUIComponentValueSetter(elt),
							ReflectionUtils.getGetter(
								opts.getClass(),
								optionField
							)
						)
					).execute();
				}

				@Override
				public void apply() throws Exception {
					(applyCommand != null
						? applyCommand
						: getDefaultApplyCommand(
							ReflectionUtils.getGUIComponentValueGetter(elt),
							ReflectionUtils.getSetter(
								opts.getClass(),
								optionField
							)
						)
					).execute();
				}
			};
		}

		public OptionChangeListener(final JComponent elt,
			final Options opts, final Field optionField) {

			this(elt, opts, optionField, null, null);
		}

		public OptionChangeListener(final JComponent elt,
			final Options opt, final Command applyCommand,
			final Command cancelCommand) {

			this(elt, opt, null, applyCommand, cancelCommand);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			actionPerformed(null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!acceptEvent) return;
			optionModifications.add(this.optionModification);
		}

		private Command getDefaultApplyCommand(
			final Method valGetter, final Method optSetter) {

			return new Command() {
				@Override
				public void execute() throws IllegalAccessException,
					IllegalArgumentException, InvocationTargetException {

					optSetter.invoke(opts, valGetter.invoke(elt));
					if (opts instanceof HexOptions) {
						hexOptionsModified = true;
					}
				}
			};
		}

		private Command getDefaultCancelCommand(
			final Method valSetter, final Method optGetter) {

			return new Command() {
				@Override
				public void execute() throws IllegalAccessException,
					IllegalArgumentException, InvocationTargetException {

					acceptEvent = false;
					valSetter.invoke(elt, optGetter.invoke(opts));
					acceptEvent = true;
				}
			};
		}

	}

}
