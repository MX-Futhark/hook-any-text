package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.utils.GUIErrorHandler;
import gui.views.OptionsDialog;
import hexcapture.HexOptions;
import hexcapture.HexPipeCompleter;
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
			for (ModificationAction modif : optionModifications) {
				modif.cancel();
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
