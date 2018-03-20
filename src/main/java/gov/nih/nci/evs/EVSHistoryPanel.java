package gov.nih.nci.evs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLClass;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

//import org.jdatepicker.impl.*;
//import org.jdatepicker.util.*;
//import org.jdatepicker.*;

public class EVSHistoryPanel extends JPanel {
	private static final long serialVersionUID = -4987773065094148253L;

	private JTable evsHistoryTable;
	private JButton refresh;
	private JButton export;
	private EVSHistoryTableModel tableModel;
	
	private OWLEditorKit owlEditorKit;
	
	private OWLClass selected = null;
	
	public EVSHistoryPanel(OWLEditorKit editorKit) {
    	
        this.owlEditorKit = editorKit;
        tableModel = new EVSHistoryTableModel(owlEditorKit);
        createUI();
    }
	
	private void createUI() {
    	setLayout(new BorderLayout());
        //JButton testBtn = new JButton("Test");
        //testBtn.setEnabled(true);
        
    	//add(testBtn, BorderLayout.NORTH);
    	add(createTopComponent(), BorderLayout.NORTH);
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
		
		setVisible(true);
    }
	
	class DateLabelFormatter extends AbstractFormatter {

	    private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }

	}
	
	private JPanel createTopComponent( ) {
		JPanel filterPanel = new JPanel();
		filterPanel.setPreferredSize(new Dimension(300, 40));
		JLabel dateLabel = new JLabel("Start Date ");
		filterPanel.add(dateLabel);
		
		/*UtilDateModel model = new UtilDateModel();
		//model.setDate(20,04,2014);
		// Need this...
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		// Don't know about the formatter, but there it is...
		JDatePickerImpl startdatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		JDatePickerImpl enddatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		filterPanel.add(startdatePicker);
		
		filterPanel.add(new JLabel("End Date "));
		
		filterPanel.add(enddatePicker);*/
		
		return filterPanel;
	}
	
	private JScrollPane createCenterComponent() {
		//JPanel tablePanel = new JPanel();
		evsHistoryTable = new JTable(tableModel);
		evsHistoryTable.setShowGrid(true);
		evsHistoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		evsHistoryTable.getTableHeader().setReorderingAllowed(true);
		evsHistoryTable.setFillsViewportHeight(true);   
		evsHistoryTable.setAutoCreateRowSorter(true);
		evsHistoryTable.setRowHeight(50);
		
		JScrollPane sPane = new JScrollPane(evsHistoryTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		return sPane;
        
		//tablePanel.add(new JScrollPane(evsHistoryTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		//return tablePanel;
	}
	
	private JPanel createBottomComponent() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(300, 40));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		refresh = new JButton("Refresh");
		export = new JButton("Export");
		
		panel.add(refresh);
		panel.add(Box.createHorizontalStrut(100));
		panel.add(export);
		return panel;
	}
	
	public void populateEVSHistoryTable( ) {
		int rowCount = evsHistoryTable.getModel().getRowCount();
		int columnCount = evsHistoryTable.getModel().getColumnCount();
		for (int row = 0; row < rowCount; row++ ) {
			for (int column = 0; column < columnCount; column++ ) {
				evsHistoryTable.getModel().getValueAt(row, column);
			}
		}
	}
	
	public void setSelectedClass(OWLClass cls) {
		selected = cls;
	}
	
	public void disposeView() {
    	
    }
}
