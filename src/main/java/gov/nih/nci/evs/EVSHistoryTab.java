package gov.nih.nci.evs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.protege.editor.owl.client.ClientSession;
import org.protege.editor.owl.client.LocalHttpClient;
import org.protege.editor.owl.client.SessionRecorder;
import org.protege.editor.owl.client.api.exception.AuthorizationException;
import org.protege.editor.owl.client.api.exception.ClientRequestException;
//import org.protege.editor.owl.client.event.ClientSessionChangeEvent;
//import org.protege.editor.owl.client.event.ClientSessionListener;
//import org.protege.editor.owl.client.event.CommitOperationEvent;
//import org.protege.editor.owl.client.event.CommitOperationListener;
//import org.protege.editor.owl.client.event.ClientSessionChangeEvent.EventCategory;
//import org.protege.editor.owl.model.history.HistoryManager;
//import org.protege.editor.owl.model.history.UndoManagerListener;
import org.protege.editor.owl.server.http.messages.History;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

//import edu.stanford.protege.search.lucene.tab.engine.IndexDirMapper;
//import edu.stanford.protege.search.lucene.tab.engine.SearchTabManager;
//import gov.nih.nci.evs.event.HistoryTabChangeEvent;
//import gov.nih.nci.evs.event.HistoryTabChangeListener;

public class EVSHistoryTab extends OWLWorkspaceViewsTab /*implements ClientSessionListener, UndoManagerListener, CommitOperationListener*/ {
	private static final long serialVersionUID = -9108654344910669629L;
	private static final Logger log = Logger.getLogger(EVSHistoryTab.class);
	
	private ClientSession clientSession = null;
	private SessionRecorder history;
	private static EVSHistoryTab tab;	
	
	public static EVSHistoryTab currentTab() {
		return tab;
	}
	
	public EVSHistoryTab() {
		setToolTipText("Custom Editor for EVS History");
		tab = this;
	}
	
	//private static ArrayList<HistoryTabChangeListener> event_listeners = new ArrayList<HistoryTabChangeListener>();
	
	/*public static void addListener(HistoryTabChangeListener l) {
		event_listeners.add(l);
	}
	
	public static void removeListener(HistoryTabChangeListener l) {
		event_listeners.remove(l);
	}
	
	public void fireChange(HistoryTabChangeEvent ev) {		
		for (HistoryTabChangeListener l : event_listeners) {
			l.handleChange(ev);
		}		
	}*/	
	
	@Override
	public void initialise() {    	
		super.initialise();
		log.info("EVS History Tab initialized");
		
		clientSession = ClientSession.getInstance(getOWLEditorKit());
		history = SessionRecorder.getInstance(getOWLEditorKit());
		
		//addListeners();
		
		//getOWLEditorKit().getOWLWorkspace().setClassSearcher(new NCIClassSearcher(getOWLEditorKit()));
		
		/*((SearchTabManager) getOWLEditorKit().getSearchManager()).getSearchContext().setIndexDirMapper(
				new IndexDirMapper() {

					@Override
					public String getIndexDirId(OWLOntology ont) {
						return clientSession.getActiveProject().get();
						
					}
					
				});*/
	}
	
	/*public void addListeners() {
		clientSession.addListener(this);
		clientSession.addCommitOperationListener(this);
		history.addUndoManagerListener(this);
	}

	public void handleChange(ClientSessionChangeEvent event) {
		
		if (event.hasCategory(EventCategory.OPEN_PROJECT)) {
			//ontology = getOWLModelManager().getActiveOntology();
			//initProperties();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireUpViews();					
				}
			});
			
		}
	}*/

	public List<History> getEvsHistory() {
    	try {
			return ((LocalHttpClient) clientSession.getActiveClient()).getEVSHistory(clientSession.getActiveProject());
		} catch (ClientRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    		
    }
	
	public List<History> getEvsHistory(History history) {
    	try {
			return ((LocalHttpClient) clientSession.getActiveClient()).getEVSHistory(history, clientSession.getActiveProject());
		} catch (ClientRequestException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return new ArrayList<History>();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    		
    }

	public String getUserId() {
		return clientSession.getActiveClient().getUserInfo().getId();
	}
	
	/*@Override
	public void operationPerformed(CommitOperationEvent event) {
		history.reset();	
		
	}

	@Override
	public void stateChanged(HistoryManager source) {
		// TODO Auto-generated method stub
		
	}*/
	
	/*public void putHistory(String c, String n, String op, String ref) {
    	try {
			((LocalHttpClient) clientSession.getActiveClient()).putEVSHistory(c, n, op, ref, clientSession.getActiveProject());
		} catch (ClientRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }*/
}
