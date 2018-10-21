package gov.nih.nci.evs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.impl.*;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.server.http.messages.History;
import org.protege.editor.owl.server.http.messages.History.HistoryType;
import org.semanticweb.owlapi.model.OWLClass;

public class EVSHistoryPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -4987773065094148253L;

	private static final Logger log = Logger.getLogger(EVSHistoryPanel.class);
	
	private JDatePickerImpl startdatePicker;
	private JDatePickerImpl enddatePicker;
	private JTextField usernameField;
	private JTextField codeField;
	private JComboBox operationCombobox;
	private JButton executeBtn;
	
	private JTable evsHistoryTable;
	//private JButton refresh;
	private JButton export;
	private EVSHistoryTableModel tableModel;
	
	private OWLEditorKit owlEditorKit;
	private OWLClass selected = null;
	protected static final String LAST_USED_FOLDER = "";
	
	enum OPERATION {ALL, CREATE, MODIFY, SPLIT, MERGE, RETIRE};
	
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
		String username = EVSHistoryTab.currentTab().getUserId();
		usernameField.setText(username);
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
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		UtilDateModel startmodel = new UtilDateModel();
		startmodel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		startmodel.setSelected(true);
		Properties p = new Properties();
		p.put("text.today", "today");
		p.put("text.month", "month");
		p.put("text.year", "year");
		DateComponentFormatter dateFormatter = new DateComponentFormatter();
        JDatePanelImpl datePanel = new JDatePanelImpl(startmodel, p);
        startdatePicker = new JDatePickerImpl(datePanel, dateFormatter);
        Dimension dm = startdatePicker.getPreferredSize();
        datePanel.setPreferredSize(new Dimension(dm.width + 100, 220));
        startdatePicker.setPreferredSize(new Dimension(dm.width + 50, dm.height));
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
		
		UtilDateModel endmodel = new UtilDateModel();
		cal.setTime(new Date());
		endmodel.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		endmodel.setSelected(true);
        JDatePanelImpl enddatePanel = new JDatePanelImpl(endmodel, p);
		enddatePicker = new JDatePickerImpl(enddatePanel, dateFormatter);
		enddatePanel.setPreferredSize(new Dimension(dm.width + 100, 220));
		enddatePicker.setPreferredSize(new Dimension(dm.width + 50, dm.height));
		enddatePicker.setBorder(emptyBorder);
		filterPanel.add(enddatePicker, c);
		
		//c.weightx = 1.5;
		c.gridx = 5;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		executeBtn = new JButton("Execute");
		//executeBtn.setBorder(emptyBorder);
		executeBtn.setPreferredSize(new Dimension(120, 30));
		executeBtn.addActionListener(this);
		filterPanel.add(executeBtn, c);
		
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
		//refresh = new JButton("Refresh");
		export = new JButton("Export");
		
		//.addActionListener(this);
		export.addActionListener(this);
		
		//panel.add(refresh);
		//panel.add(Box.createHorizontalStrut(100));
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
		if ( e.getSource() == executeBtn ) {
			String startdate = startdatePicker.getJFormattedTextField().getText();
			String enddate = enddatePicker.getJFormattedTextField().getText();
			
			DateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
			Date startDate = null;
			Date endDate = null;
			
			if (startdate != null && !startdate.isEmpty()) {
				try {
					startDate = dateFormatter.parse(startdate);
				} catch (ParseException ex) {
					JOptionPane.showMessageDialog(this, "Please select a start date from Calendar.");	
				}
			}
			if (enddate != null && !enddate.isEmpty()) {
				try {
					endDate = dateFormatter.parse(enddate);
				} catch (ParseException ex) {
					JOptionPane.showMessageDialog(this, "Please select an end date from Calendar.");	
				}
			}
			if (startDate != null && endDate != null) {
				if (startDate.after(endDate)) {
					JOptionPane.showMessageDialog(this, "Start date can not be later than end date.");
					return;
				}
			}
			
			String username = usernameField.getText();
			String code = codeField.getText();
			String operation = operationCombobox.getSelectedItem().toString();
			if (operation.equals("ALL")) {
				operation = null;
			}
			
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			startdate = startDate == null ? "" : dateFormatter.format(startDate);
			enddate = endDate == null ? "" : dateFormatter.format(endDate);
			
			History query = new History();
			query.setQueryArgs(startdate, enddate, username, code, operation);
			
			tableModel.setHistoryList(query);
			tableModel.fireTableDataChanged();
			
		/*} else if (e.getSource() == refresh ) {
			System.out.println("##### Refresh button pressed.");
			tableModel.refreshHistoryList();
			tableModel.fireTableDataChanged();*/
		} else if (e.getSource() == export ) {
			//System.out.println("##### Export button pressed.");
			List<History> hisList = tableModel.getHistoryList();
			
			Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(getClass());   
			JFileChooser fc = new JFileChooser(prefs.getString(LAST_USED_FOLDER, new File(".").getAbsolutePath()));
			
			BufferedWriter bw = null;
			FileWriter fw = null;
			
			int select = fc.showSaveDialog(this);
			if (select == JFileChooser.APPROVE_OPTION) {
				prefs.putString(LAST_USED_FOLDER, fc.getSelectedFile().getParent());
			  
				File file = fc.getSelectedFile();
				String infile = file.getAbsolutePath();
				
				try {
					
					fw = new FileWriter(infile);
					bw = new BufferedWriter(fw);
				    
					if (hisList != null && !hisList.isEmpty()) {
						for (History history : hisList) {
							
							bw.write(history.toRecord(HistoryType.EVS) + System.lineSeparator());
						}
					}
					
				} catch (IOException ex) {
					log.error(ex.getMessage());
				}
				finally {
					try {

						if (bw != null)
							bw.close();

						if (fw != null)
							fw.close();

					} catch (IOException ex) {

						ex.printStackTrace();

					}
				}
			}
		}
	}
	
}
