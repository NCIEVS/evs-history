package gov.nih.nci.evs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLClass;

import org.apache.log4j.Logger;
import org.jdatepicker.*;

public class EVSHistoryPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -4987773065094148253L;

	private static final Logger log = Logger.getLogger(EVSHistoryPanel.class);
	
	private JDatePicker startdatePicker;
	private JDatePicker enddatePicker;
	private JTextField usernameField;
	private JTextField codeField;
	private JComboBox operationCombobox;
	private JButton goBtn;
	
	private JTable evsHistoryTable;
	private JButton refresh;
	private JButton export;
	private EVSHistoryTableModel tableModel;
	
	private OWLEditorKit owlEditorKit;
	private OWLClass selected = null;
	
	enum OPERATION {ALL, CREATE, UPDATE, DELETE, SPLIT, MERGE, RETIRE, COPY};
	
	public EVSHistoryPanel(OWLEditorKit editorKit) {
    	
        this.owlEditorKit = editorKit;
        tableModel = new EVSHistoryTableModel(owlEditorKit);
        createUI();
    }
	
	private void createUI() {
    	setLayout(new BorderLayout());
        
    	add(createTopComponent(), BorderLayout.NORTH);
		add(createCenterComponent(), BorderLayout.CENTER);
		add(createBottomComponent(), BorderLayout.SOUTH);
		
		setVisible(true);
    }
	
	private JPanel createTopComponent( ) {
		JPanel filterPanel = new JPanel();
		filterPanel.setPreferredSize(new Dimension(300, 40));
		
		JLabel startDateLabel = new JLabel("Start Date ");
		EmptyBorder emptyBorder = new EmptyBorder(5, 5, 5, 5);
		startDateLabel.setBorder(emptyBorder);
		filterPanel.add(startDateLabel);
		
		startdatePicker = new JDatePicker();
		filterPanel.add(startdatePicker);
		
		JLabel endDateLabel = new JLabel("End Date ");
		EmptyBorder emptyBorder2 = new EmptyBorder(5, 50, 5, 5);
		endDateLabel.setBorder(emptyBorder2);
		filterPanel.add(endDateLabel);
		
		enddatePicker = new JDatePicker();	
		filterPanel.add(enddatePicker);
		
		JLabel userNameLabel = new JLabel("Username ");
		userNameLabel.setBorder(emptyBorder2);
		usernameField = new JTextField(20);
		filterPanel.add(userNameLabel);
		filterPanel.add(usernameField);
		
		JLabel codeLabel = new JLabel("Code ");
		codeLabel.setBorder(emptyBorder2);
		codeField = new JTextField(15);
		filterPanel.add(codeLabel);
		filterPanel.add(codeField);
		
		JLabel operationLabel = new JLabel("Operation ");
		operationLabel.setBorder(emptyBorder2);
		operationCombobox = new JComboBox(OPERATION.values());
		filterPanel.add(operationLabel);
		filterPanel.add(operationCombobox);
		
		goBtn = new JButton("Go");
		EmptyBorder emptyBorder3 = new EmptyBorder(5, 50, 5, 50);
		goBtn.setBorder(emptyBorder3);
		goBtn.addActionListener(this);
		filterPanel.add(goBtn);
		
		return filterPanel;
	}
	
	private JScrollPane createCenterComponent() {
		
		evsHistoryTable = new JTable(tableModel);
		evsHistoryTable.setShowGrid(true);
		evsHistoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		evsHistoryTable.getTableHeader().setReorderingAllowed(true);
		evsHistoryTable.setFillsViewportHeight(true); 
		evsHistoryTable.setAutoCreateRowSorter(true);
		evsHistoryTable.setRowHeight(35);
		
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

	@Override
	public void actionPerformed(ActionEvent e) {
		String startdate = startdatePicker.getFormattedTextField().getText();
		String enddate = enddatePicker.getFormattedTextField().getText();
		
		if (!validateDate(startdate, enddate, "MMM dd, yyyy")) {
			JOptionPane.showMessageDialog(this, "Start date can not be later than end date.");
			return;
		}
		
		String username = usernameField.getText();
		String code = codeField.getText();
		String operation = operationCombobox.getSelectedItem().toString();
		
		try {
			tableModel.setHistoryList(startdate, enddate, username, code, operation);
			tableModel.fireTableDataChanged();  
		} catch (ParseException ex) {
			log.error(ex.getMessage(), ex);
		}
	}
	
	private boolean validateDate( String startdate, String enddate, String datePattern ) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
		Date startdt = null;
		Date enddt = null;
		try {
			startdt = dateFormatter.parse(startdate);
			enddt = dateFormatter.parse(enddate);
		} catch (ParseException ex) {
			JOptionPane.showMessageDialog(this, "Please select a date from Calendar.");
			return false;
		}
		if (startdt.after(enddt)) {
			return false;
		}
		return true;
	}
}
