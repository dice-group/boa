package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.evaluation.EvaluationResultDao;
import de.uni_leipzig.simba.boa.backend.entity.EvaluationResult;
import de.uni_leipzig.simba.boa.backend.rdf.Model;
import de.uni_leipzig.simba.boa.backend.rdf.store.Store;
import de.uni_leipzig.simba.boa.backend.util.DbpediaUtil;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class StatementForm extends Form {
	
	private static GridLayout layout = null; 
	private final EvaluationResultDao evalResultDao = (EvaluationResultDao) DaoFactory.getInstance().createDAO(EvaluationResultDao.class);
	
	private static final String MANUAL_REASON = "MANUAL";
	private static final String AUTOMATIC_REASON = "AUTOMATIC";
	
	private static int NUMBER_OF_ITEMS_IN_RANDOM_LIST;
	
	public StatementForm(BoaFrontendApplication app, String rdfModelId) {
		
		String id = this.hashCode() + new Date().toString();
		
		String graph = rdfModelId;
		
		Store store = new Store();
		Model model = store.createModelIfNotExists(graph);
		
		List<Statement> stmts = model.getStatements();
		
		int correctStmts = 0;
		for ( Statement st : stmts) {
			
			if ( DbpediaUtil.getInstance().askDbpediaForTriple(st) ) correctStmts++;
		}
		
		NUMBER_OF_ITEMS_IN_RANDOM_LIST = Math.min(100, stmts.size());
		List<Statement> randomStatments = new ArrayList<Statement>();
		
		// make the list random
		for ( int i = 0 ; i < NUMBER_OF_ITEMS_IN_RANDOM_LIST ; i++ ) {
			
			int j = (int)(Math.random() * (stmts.size()));
			randomStatments.add(stmts.get(j));
			stmts.remove(j);
		}
		
		List<Statement> statmentsToVerify = this.filterCorrectStatements(randomStatments, id, graph);
		
		Window subwindow = new Window("Evaluation of RDF Model " + graph);
        subwindow.setModal(false);
        VerticalLayout vlayout = (VerticalLayout) subwindow.getContent();
        vlayout.setMargin(true);
        vlayout.setSpacing(true);
        Label message = new Label(
        		"There are " + model.getStatements().size() + " statements in the RDF model, " + correctStmts + " are already in Dbpedia.<br/><hr/>" + 
				"There were " + StatementForm.NUMBER_OF_ITEMS_IN_RANDOM_LIST + " statements used for evaluation!<br/><hr/>" +
				"And " + (StatementForm.NUMBER_OF_ITEMS_IN_RANDOM_LIST - statmentsToVerify.size()) + " of those statements have been considered correct because they are already in dbpedia.org!", Label.CONTENT_XHTML);
        subwindow.addComponent(message);
        subwindow.setWidth("850px");
        subwindow.setResizable(false);
        app.getMainWindow().addWindow(subwindow);
        
		StatementForm.layout = new GridLayout(6, (statmentsToVerify.size() * 2) + 1);
		StatementForm.layout.setMargin(false, false, false, false);
		StatementForm.layout.setSpacing(true);
		StatementForm.layout.setSizeFull();
		StatementForm.layout.setColumnExpandRatio(0, .1f);
		StatementForm.layout.setColumnExpandRatio(1, .2f);
		StatementForm.layout.setColumnExpandRatio(2, .2f);
		StatementForm.layout.setColumnExpandRatio(3, .2f);
		StatementForm.layout.setColumnExpandRatio(4, .15f);
		StatementForm.layout.setColumnExpandRatio(5, .15f);
		
		for (int i = 0, j = 1; i < statmentsToVerify.size() * 2 ; i = i + 2, j++ ) {
			
			Statement st = statmentsToVerify.get(j - 1);
			
			if ( i % 2 == 0 ) StatementForm.layout.addComponent(new Label(String.valueOf(j) + "."), 0, i);
			
			Link subject	= new Link(this.createNameSpace(st.getSubject().toString()), new ExternalResource(st.getSubject().toString()));
			subject.setWidth("200px");
			Link predicate	= new Link(this.createNameSpace(st.getPredicate().toString()), new ExternalResource(st.getPredicate().toString()));
			predicate.setWidth("200px");
			Link object		= new Link(this.createNameSpace(st.getObject().toString()), new ExternalResource(st.getObject().toString()));
			object.setWidth("200px");
			
			StatementForm.layout.addComponent(subject, 1, i);
			StatementForm.layout.addComponent(predicate, 2, i);
			StatementForm.layout.addComponent(object, 3, i);

			Button trueButton = new Button("True");
			List<String> data = Arrays.asList(String.valueOf(i), id, graph, st.getSubject().toString(), st.getPredicate().toString(), st.getObject().toString());
			trueButton.setData(data);
			trueButton.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {

					Boolean correct = new Boolean(event.getButton().getCaption());
					List<String> data = (List<String>) event.getButton().getData();
					
					int row = Integer.valueOf(data.get(0));
					layout.removeRow(row);
					layout.removeRow(row);
					
					EvaluationResult result = new EvaluationResult(data.get(1));
					result.setGraph(data.get(2));
					result.setSubject(data.get(3));
					result.setPredicate(data.get(4));
					result.setObject(data.get(5));
					result.setCorrect(correct);
					result.setReason(StatementForm.MANUAL_REASON);
					evalResultDao.createAndSaveEvaluationResult(result);
				}
	        });
			
			Button falseButton = new Button("False");
			falseButton.setData(data);
			falseButton.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					Boolean correct = new Boolean(event.getButton().getCaption());
					List<String> data = (List<String>) event.getButton().getData();
					
					int row = Integer.valueOf(data.get(0));
					layout.removeRow(row);
					layout.removeRow(row + 1);
					
					EvaluationResult result = new EvaluationResult(data.get(1));
					result.setGraph(data.get(2));
					result.setSubject(data.get(3));
					result.setPredicate(data.get(4));
					result.setObject(data.get(5));
					result.setCorrect(correct);
					result.setReason(StatementForm.MANUAL_REASON);
					
					evalResultDao.createAndSaveEvaluationResult(result);
				}
	        });
			
			StatementForm.layout.addComponent(trueButton, 4, i);
			StatementForm.layout.addComponent(falseButton, 5, i);
			StatementForm.layout.addComponent(new Label(("<hr/>"), Label.CONTENT_XHTML), 0, i + 1, 5, i + 1);
		}
		
		this.setLayout(StatementForm.layout);
		this.setSizeFull();
	}

	private List<Statement> filterCorrectStatements(List<Statement> randomStatments, String id, String graph) {

		DbpediaUtil dbpediaUtil = DbpediaUtil.getInstance();
		List<Statement> statementsToVerify = new ArrayList<Statement>();
		for ( Statement st : randomStatments ) {
			
			if ( dbpediaUtil.askDbpediaForTriple(st) ) {
				
				System.out.println("TRUE" + st);
				
				EvaluationResult result = new EvaluationResult(id);
				result.setGraph(graph);
				result.setSubject(st.getSubject().toString());
				result.setPredicate(st.getPredicate().toString());
				result.setObject(st.getObject().toString());
				result.setCorrect(true);
				result.setReason(StatementForm.AUTOMATIC_REASON);
				this.evalResultDao.createAndSaveEvaluationResult(result);
			}
			else {
				
				statementsToVerify.add(st);
			}
		}
		return statementsToVerify;
	}

	private String createNameSpace(String string) {

		string = string.replaceAll("http://dbpedia.org/resource/", "dbr:");
		string = string.replaceAll("http://dbpedia.org/ontology/", "dbo:");
		string = string.replaceAll("http://nlpedia.de/", "boa:");
		
		return string;
	}
}