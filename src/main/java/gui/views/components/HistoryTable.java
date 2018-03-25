package gui.views.components;

import java.awt.Component;
import java.util.Enumeration;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import main.utils.StringUtils;

/**
 * The table containing previous inputs and outputs.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class HistoryTable extends JTable {

	private static final String[] COLUMN_NAMES = {"Input", "Output"};

	public HistoryTable() {
		super(new DefaultTableModel(COLUMN_NAMES, 0) {
			@Override
			public boolean isCellEditable(int row, int column){
				return false;
			}
		});

		DefaultTableCellRenderer topRenderer = new DefaultTableCellRenderer();
		topRenderer.setVerticalAlignment(SwingConstants.TOP);
		Enumeration<TableColumn> cols = this.getColumnModel().getColumns();
		while (cols.hasMoreElements()) {
			cols.nextElement().setCellRenderer(topRenderer);
		}
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer,
		int row, int column) {

		Component c = super.prepareRenderer(renderer, row, column);
		if (c instanceof JComponent) {
			JComponent jc = (JComponent) c;
			jc.setToolTipText(
				breakTooltipContent(getValueAt(row, column).toString(), 100)
			);
		}
		return c;
	}

	private String breakTooltipContent(String content, int maxLineLength) {
		return StringUtils.plainTextToHTML(
			StringUtils.breakNoSpaceText(content, maxLineLength)
		);
	}

}
