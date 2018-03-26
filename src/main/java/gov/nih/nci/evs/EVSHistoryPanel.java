package gov.nih.nci.evs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.protege.editor.owl.server.http.messages.History;
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
		filterPanel.setLayout(new GridBagLayout());
		filterPanel.setPreferredSize(new Dimension(300, 80));
		filterPanel.setBorder(new EmptyBorder(0, 50, 0, 50));
		
		EmptyBorder emptyBorder = new EmptyBorder(0, 5, 0, 5);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.5;
		c.gridx = 0;
		c.gridy = 0;
		//c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_END;
		
		JLabel userNameLabel = new JLabel("Username ");
		userNameLabel.setBorder(emptyBorder);
		filterPanel.add(userNameLabel, c);
		
		//c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		usernameField = new JTextField(20);
		usernameField.setBorder(emptyBorder);
		filterPanel.add(usernameField, c);
		
		//c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel startDateLabel = new JLabel("Start Date ");
		startDateLabel.setBorder(emptyBorder);
		filterPanel.add(startDateLabel, c);
		
		//c.weightx = 0.5;
		c.gridx = 3;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		startdatePicker = new JDatePicker();
		startdatePicker.setBorder(emptyBorder);
		filterPanel.add(startdatePicker, c);
		
		//c.weightx = 0.5;
		c.gridx = 4;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel operationLabel = new JLabel("Operation ");
		operationLabel.setBorder(emptyBorder);
		filterPanel.add(operationLabel, c);
		
		//c.weightx = 0.5;
		c.gridx = 5;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		operationCombobox = new JComboBox(OPERATION.values());
		operationCombobox.setBorder(emptyBorder);
		filterPanel.add(operationCombobox, c);
		
		//c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel codeLabel = new JLabel("Code ");
		codeLabel.setBorder(emptyBorder);
		filterPanel.add(codeLabel, c);
		
		//c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		codeField = new JTextField(20);
		codeField.setBorder(emptyBorder);
		filterPanel.add(codeField, c);
		
		//c.weightx = 0.5;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		JLabel endDateLabel = new JLabel("End Date ");
		endDateLabel.setBorder(emptyBorder);
		filterPanel.add(endDateLabel, c);
		
		//c.weightx = 1.5;
		c.gridx = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		enddatePicker = new JDatePicker();	
		enddatePicker.setBorder(emptyBorder);
		filterPanel.add(enddatePicker, c);
		
		//c.weightx = 1.5;
		c.gridx = 4;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		goBtn = new JButton("Go");
		goBtn.setBorder(emptyBorder);
		goBtn.setPreferredSize(new Dimension(60, 30));
		goBtn.addActionListener(this);
		filterPanel.add(goBtn, c);
		
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
		
		refresh.addActionListener(this);
		export.addActionListener(this);
		
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
		if ( e.getSource() == goBtn ) {
			String startdate = startdatePicker.getFormattedTextField().getText();
			String enddate = enddatePicker.getFormattedTextField().getText();
			
			if (startdate != null && enddate != null && !startdate.isEmpty() && !enddate.isEmpty()) {
				if (!validateDate(startdate, enddate, "MMM dd, yyyy")) {
					JOptionPane.showMessageDialog(this, "Start date can not be later than end date.");
					return;
				}
			}
			
			String username = usernameField.getText();
			String code = codeField.getText();
			String operation = operationCombobox.getSelectedItem().toString();
			
			History query = new History();
			query.setQueryArgs(startdate, enddate, username, code, operation);
			
			tableModel.setHistoryList(query);
			tableModel.fireTableDataChanged();
			
		} else if (e.getSource() == refresh ) {
			System.out.println("##### Refresh button pressed.");
			tableModel.refreshHistoryList();
			tableModel.fireTableDataChanged();
		} else if (e.getSource() == export ) {
			System.out.println("##### Export button pressed.");
			List<History> hisList = tableModel.getHistoryList();
			
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
