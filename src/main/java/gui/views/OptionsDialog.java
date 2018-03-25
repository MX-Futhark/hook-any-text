package gui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import gui.actions.OptionsDialogActions;
import gui.utils.GUIErrorHandler;
import gui.utils.Images;
import gui.views.components.HexSelectionsTable;
import gui.views.components.ReplacementsTable;
import gui.views.components.TablePanel;
import gui.views.models.TableViewModel;
import hexcapture.HexSelection;
import hexcapture.HexSelections;
import hextostring.replacement.Replacements;
import main.MainOptions;
import main.options.Options;
import main.options.annotations.CommandLineArgument;
import main.options.annotations.CommandLineValue;
import main.options.domain.Bounds;
import main.options.domain.Domain;
import main.options.domain.Values;
import main.utils.GenericSort;
import main.utils.ReflectionUtils;
import main.utils.StringUtils;

/**
 * The dialog containing all the necessary elements to modify the options.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class OptionsDialog extends JDialog {

	private OptionsDialogActions acts = new OptionsDialogActions(this);

	private MainOptions opts;

	public static final double DOUBLE_SPINNER_STEP = 0.005;
	public static final int INTEGER_SPINNER_STEP = 1;

	public OptionsDialog(MainOptions opts) {
		setTitle("Options");
		setModal(true);
		setIconImage(Images.DEFAULT_ICON.getImage());

		this.opts = opts;

		appendBody();
		setMinimumSize(new Dimension(400, 600));

		acts.setCloseAction();
	}

	private void appendBody() {
		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(getAllTabs(), BorderLayout.NORTH);
		optionsPanel.add(getButtons(), BorderLayout.EAST);
		add(optionsPanel);
	}

	private JTabbedPane getAllTabs() {
		JTabbedPane tabs = new JTabbedPane();
		Collection<Options> options =
			GenericSort.apply(opts.getSubOptions(), null);
		try {
			for (Options option : options) {
				addOptionTabs(option, tabs);
			}
		} catch (IllegalArgumentException | IllegalAccessException
			| SecurityException | NoSuchMethodException
			| InvocationTargetException | NoSuchFieldException e) {

			new GUIErrorHandler(e);
		}
		tabs.add("Replacements", getReplacementsPanel(
			opts.getConvertOptions().getReplacements(),
			acts
		));
		tabs.add("Selections", getHexSelectionsPanel(
			opts.getHexOptions().getHexSelections(),
			acts
		));
		return tabs;
	}

	private JPanel getButtons() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		JButton applyButton = new JButton("Apply");
		acts.setApplyButtonAction(applyButton, opts);
		buttonsPanel.add(applyButton);

		JButton okButton = new JButton("OK");
		acts.setOKButtonAction(okButton, opts);
		buttonsPanel.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		acts.setCancelButtonAction(cancelButton);
		buttonsPanel.add(cancelButton);

		return buttonsPanel;
	}

	// Generates the tabs and their content automatically through reflection.
	private void addOptionTabs(Options optionObject, JTabbedPane tabs)
		throws IllegalArgumentException, IllegalAccessException,
		SecurityException, NoSuchMethodException, InvocationTargetException,
		NoSuchFieldException {

		List<Field> flagFields = new LinkedList<>();

		JPanel optionPanel = new JPanel();
		JPanel innerPanel = new JPanel(new GridLayout(0, 2));

		boolean empty = true;

		Collection<Field> optsFields = GenericSort.apply(
			ReflectionUtils.getAnnotatedFields(
				optionObject.getClass(),
				CommandLineArgument.class
			),
			Field.class.getMethod("getName")
		);

		for (Field field : optsFields) {
			if (field.getAnnotation(CommandLineArgument.class).flags()) {
				flagFields.add(field);
			} else {
				String labelContent =
					StringUtils.camelToWords(field.getName());
				JComponent component =
					getComponentFromField(field, optionObject);
				if (component == null) continue;

				component.setName(labelContent);
				acts.setOptionComponentAction(component, field, optionObject);
				innerPanel.add(new JLabel(labelContent));
				innerPanel.add(component);
				empty = false;
			}
		}

		optionPanel.add(innerPanel);

		if (!empty) {
			tabs.add(
				StringUtils.capitalize(StringUtils.camelToWords(
					optionObject.getClass().getSimpleName()
						.replace("Options", "")
				)),
				optionPanel
			);
		}
		for (Field flagField : flagFields) {
			addFlagTab(optionObject, flagField, tabs);
		}
	}

	private JPanel getReplacementsPanel(Replacements replacements,
		OptionsDialogActions acts) {

		final ReplacementsTable replacementsTable =
			new ReplacementsTable(replacements, acts);

		JButton addReplacementButton = new JButton("Add replacement");
		acts.setAddRowButtonAction(addReplacementButton, replacementsTable);
		JButton deleteSelectedButton = new JButton("Delete selected");
		acts.setDeleteSelectedButtonAction(
			deleteSelectedButton,
			replacementsTable
		);

		return new TablePanel(replacementsTable, new Component[]{
			addReplacementButton, deleteSelectedButton
		});
	}

	private JPanel getHexSelectionsPanel(HexSelections selections,
		final OptionsDialogActions acts) {

		final HexSelectionsTable selectionsTable =
			new HexSelectionsTable(selections, acts);

		// FIXME: We're duplicating the logic of the observable HexSelections
		// into the UI here... OptionsDialogActions.setTableModelActions should
		// be modified instead (but the fact that changes are taken into
		// account lazily is difficult to reconcile with the eagerly observable
		// nature of HexSelections)
		final Runnable setLastRadioOn = new Runnable() {

			@Override
			public void run() {
				TableViewModel<HexSelection> viewModel =
					selectionsTable.getViewModel();
				int column = -1;
				while (viewModel.getColumnClass(++column) != Boolean.class) {}
				acts.applyRadioAction(
					selectionsTable,
					((DefaultTableModel) selectionsTable.getModel())
						.getRowCount() - 1,
					column
				);
			}
		};

		JButton addSelectionButton = new JButton("Add selection");
		acts.setAddRowButtonAction(
			addSelectionButton,
			selectionsTable,
			setLastRadioOn
		);

		JButton deleteSelectedButton = new JButton("Delete selected");
		acts.setDeleteSelectedButtonAction(deleteSelectedButton,
			selectionsTable, 1, new Runnable() {

			@Override
			public void run() {
				DefaultTableModel model =
					(DefaultTableModel) selectionsTable.getModel();
				int activeRadioIndex = -1;
				for (int i = 0; i < model.getRowCount(); ++i) {
					for (int j = 0; j < model.getColumnCount(); ++j) {
						Object v = model.getValueAt(i, j);
						if (v != null && v.equals(Boolean.TRUE)) {
							activeRadioIndex = i;
						}
					}
				}
				if (activeRadioIndex == -1) {
					setLastRadioOn.run();
				}
			}
		});

		return new TablePanel(selectionsTable, new Component[]{
			addSelectionButton, deleteSelectedButton
		});
	}

	// Flags are managed differently: every flag corresponds to a checkbox.
	private void addFlagTab(Options optionObject, Field flagField,
		JTabbedPane tabs) throws IllegalArgumentException,
		IllegalAccessException, InvocationTargetException, NoSuchFieldException,
		SecurityException {

		JPanel flagPanel = new JPanel();
		flagPanel.setName(
			StringUtils.camelToWords(flagField.getName().replace("Flags", ""))
		);
		flagPanel.setLayout(new BoxLayout(flagPanel, BoxLayout.Y_AXIS));

		boolean empty = true;

		long setFlags = (long) ReflectionUtils
			.getGetter(optionObject.getClass(), flagField)
			.invoke(optionObject);
		List<Field> valueFields = ReflectionUtils.getAnnotatedFields(
			optionObject.getFieldValueClass(flagField),
			CommandLineValue.class
		);
		TreeMap<Long, Field> sortedValFields = new TreeMap<>();
		for (Field valueField : valueFields) {
			sortedValFields.put((Long) valueField.get(null), valueField);
		}
		for (Field valueField : sortedValFields.values()) {
			String checkboxName =
				StringUtils.screamingSnakeToWords(valueField.getName());
			final JCheckBox flagCheckBox = new JCheckBox(checkboxName);
			flagCheckBox.setName(checkboxName);
			flagCheckBox.setSelected((setFlags & valueField.getLong(null)) > 0);
			acts.setFlagCheckboxAction(
				flagCheckBox,
				flagField,
				valueField,
				optionObject
			);
			flagPanel.add(flagCheckBox);
			empty = false;
		}

		if (!empty) {
			tabs.add(
				StringUtils.capitalize(StringUtils.camelToWords(
					flagField.getName().replace("Flags", "")
				)),
				flagPanel
			);
		}
	}

	// Provides a component depending on the nature of the option field.
	private static JComponent getComponentFromField(Field valueField,
		Options optionsObj) throws IllegalArgumentException,
		IllegalAccessException, SecurityException {

		valueField.setAccessible(true);
		Object value = valueField.get(optionsObj);
		Domain<?> domain;
		try {
			domain = optionsObj.getFieldDomain(valueField);
		} catch (NoSuchFieldException e) {
			domain = null;
		}
		if ((value.getClass().equals(Double.class)
			|| value.getClass().equals(Integer.class))
			&& (domain == null || domain instanceof Bounds)) {

			return getJSpinner(value, (Bounds<?>) domain);
		} else if (domain == null || domain instanceof Values) {
			if (value.getClass().equals(Boolean.class)) {
				return getJCheckBox(value, (Values<?>) domain);
			} else if (domain != null) {
				return getJComboBox(value, (Values<?>) domain);
			}
		}
		return null;
	}

	private static JComponent getJSpinner(Object value, Bounds<?> domain) {
		JSpinner spinner = new JSpinner();
		SpinnerNumberModel model;
		double min = 0d, max = 1d;

		if (domain != null) {
			min = ((Number) domain.getMin()).doubleValue();
			max = ((Number) domain.getMax()).doubleValue();
		}

		if (value.getClass().equals(Integer.class)) {
			model = new SpinnerNumberModel((int) value, (int) min, (int) max, INTEGER_SPINNER_STEP);
		} else {
			model = new SpinnerNumberModel((double) value, min, max, DOUBLE_SPINNER_STEP);
		}

		spinner.setModel(model);
		return spinner;
	}

	private static JComponent getJComboBox(Object value, Values<?> domain) {
		JComboBox<Object> cb = new JComboBox<>(domain.getValues());
		int eltCounter = 0;
		for (Object possibleValue : domain.getValues()) {
			if (value.equals(possibleValue)) {
				cb.setSelectedIndex(eltCounter);
				break;
			}
			++eltCounter;
		}
		return cb;
	}

	private static JComponent getJCheckBox(Object value, Values<?> domain) {
		JCheckBox cb = new JCheckBox();
		cb.setSelected((boolean) value);

		// Disabled if only one choice.
		// If the domain is inconsistent with the value, the value has priority.
		if (domain != null && domain.getValues().length < 2) {
			cb.setEnabled(false);
		}

		return cb;
	}

}
