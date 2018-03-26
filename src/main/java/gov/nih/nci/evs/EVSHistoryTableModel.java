package gov.nih.nci.evs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.server.http.messages.History;
import org.semanticweb.owlapi.model.OWLOntology;

public class EVSHistoryTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	private List<History> historyList;
	private List<History> orgHistoryList;
	OWLOntology ont;
	
	public EVSHistoryTableModel(OWLEditorKit k) {
		ont = k.getOWLModelManager().getActiveOntology();
		orgHistoryList = EVSHistoryTab.currentTab().getEvsHistory();
		historyList = new ArrayList<History>(orgHistoryList);
		
		//String prefsID = getClass().toString() + EVSHistoryTab.currentTab().getRDFSLabel(complexProp).get();
		//prefs = PreferencesManager.getInstance().getApplicationPreferences(prefsID);
		
	}

	@Override
	public int getRowCount() {
		
		return historyList.size();
	}

	@Override
	public int getColumnCount() {
		
		return 6;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (historyList != null && !historyList.isEmpty() && historyList.size() > rowIndex) {
			History history = historyList.get(rowIndex);
			
			List<String> elementList = getHistoryElements(history);
			if (elementList.size() > 0) {
				return elementList.get(columnIndex);
			}		
		}
		
		return "";
	}
	
	public String getColumnName(int column) {
		String columnName = "";
		
		switch(column) {
			case 0:
				columnName = "Date";
				break;
			case 1:
				columnName = "User Name";
				break;
			case 2:
				columnName = "Code";
				break;
			case 3:
				columnName = "Name";
				break;
			case 4:
				columnName = "Operation";
				break;
			case 5:
				columnName = "Reference";
				break;
			default:
				break;
		}
		
		return columnName;
	}
	
	public void setHistoryList( History query ) {	
		historyList = EVSHistoryTab.currentTab().getEvsHistory(query);
	}
	
	public void refreshHistoryList() {
		historyList = EVSHistoryTab.currentTab().getEvsHistory();
	}
	
	public List<History> getHistoryList() {
		return historyList;
	}
	
	private List<String> getHistoryElements(History history) {
		List<String> elementList = new ArrayList<String>();
		elementList.add(history.getDate());
		elementList.add(history.getUser_name());
		elementList.add(history.getCode());
		elementList.add(history.getName());
		elementList.add(history.getOperation());
		elementList.add(history.getReference());
		return elementList;
	}

}
