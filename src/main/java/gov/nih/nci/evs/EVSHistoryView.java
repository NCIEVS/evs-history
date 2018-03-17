package gov.nih.nci.evs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.protege.editor.owl.ui.table.BasicOWLTable;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

public class EVSHistoryView extends AbstractOWLViewComponent {
	private static final long serialVersionUID = -1662692188945501597L;

	private JTable evsHistoryTable;
	private JButton refresh;
	private JButton export;
	private EVSHistoryTableModel tableModel;	

	@Override
	protected void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout());
		add(createTopComponent(), BorderLayout.NORTH);
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
	}

	private JComponent createTopComponent( ) {
		JPanel filterPanel = new JPanel();
		//TODO
		return filterPanel;
	}
	
	private JComponent createCenterComponent() {
		JPanel tablePanel = new JPanel();
		evsHistoryTable = new JTable();
		
		tablePanel.add(new JScrollPane(evsHistoryTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		tableModel = new EVSHistoryTableModel();
		BasicOWLTable results = new BasicOWLTable(tableModel) {
			private static final long serialVersionUID = 9143285439978520141L;

			@Override
			protected boolean isHeaderVisible() {
				return true;
			}
		};
		OWLCellRenderer renderer = new OWLCellRenderer(getOWLEditorKit());
		renderer.setWrap(false);
		results.setDefaultRenderer(Object.class, renderer);
		JScrollPane scrollableResults = new JScrollPane(results);
		tablePanel.add(scrollableResults);
		return tablePanel;
	}
	
	private JComponent createBottomComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		refresh = new JButton("Refresh");
		export = new JButton("Export");
		
		panel.add(refresh);
		panel.add(export);
		return panel;
	}
	
	@Override
	protected void disposeOWLView() {
		/*changesPanel.dispose();
        reviewButtonsPanel.dispose();*/
	}

}
