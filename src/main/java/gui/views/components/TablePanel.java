package gui.views.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Panel containing a table at the top and other components at the bottom
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class TablePanel extends JPanel {

	public TablePanel(JTable table, Component[] southContent) {
		super(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new FlowLayout());
		for (Component c : southContent) {
			southPanel.add(c);
		}
		add(southPanel, BorderLayout.SOUTH);
	}

}
