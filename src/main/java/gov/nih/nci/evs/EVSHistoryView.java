package gov.nih.nci.evs;

import java.awt.BorderLayout;

import org.protege.editor.owl.ui.view.cls.OWLClassAnnotationsViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

public class EVSHistoryView extends OWLClassAnnotationsViewComponent {
	private static final long serialVersionUID = -1662692188945501597L;

	private EVSHistoryPanel historyPanel;	

	@Override
	public void initialiseClassView() throws Exception {
		historyPanel = new EVSHistoryPanel(getOWLEditorKit());
		setLayout(new BorderLayout());
		historyPanel.populateEVSHistoryTable();
		add(historyPanel);
	}
	

	@Override
	protected OWLClass updateView(OWLClass selectedClass) {
		historyPanel.setSelectedClass(selectedClass);		
        return selectedClass;
	}
	
	@Override
	public void disposeView() {
		historyPanel.disposeView();
	}

}
