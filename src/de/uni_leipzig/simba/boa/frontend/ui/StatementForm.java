package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;
import com.vaadin.data.Container;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

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
	
	public StatementForm(BoaFrontendApplication app, String rdfModelId) {
		
		String id = this.hashCode() + new Date().toString();
		
		String graph = rdfModelId;
		
		Store store = new Store();
		Model model = store.createModelIfNotExists(graph);
		
		List<Statement> stmts = model.getStatements();
		List<Statement> randomStatments = new ArrayList<Statement>();
		
		// make the list random
		for ( int i = 0 ; i < 100 ; i++ ) {
			
			if ( i < stmts.size() ) {
				
				int j = (int)(Math.random() * (stmts.size()));
				randomStatments.add(stmts.get(j));
				stmts.remove(j);
			}
			else break;
		}
		
		randomStatments = this.filterCorrectStatements(randomStatments, id, graph);
		
		StatementForm.layout = new GridLayout(6, (randomStatments.size() * 2) + 1);
		StatementForm.layout.setMargin(false, false, false, false);
		StatementForm.layout.setSpacing(true);
		StatementForm.layout.setSizeFull();
		StatementForm.layout.setColumnExpandRatio(0, .1f);
		StatementForm.layout.setColumnExpandRatio(1, .2f);
		StatementForm.layout.setColumnExpandRatio(2, .2f);
		StatementForm.layout.setColumnExpandRatio(3, .2f);
		StatementForm.layout.setColumnExpandRatio(4, .15f);
		StatementForm.layout.setColumnExpandRatio(5, .15f);
		
		for (int i = 0, j = 1; i < randomStatments.size() * 2 ; i = i + 2, j++ ) {
			
			Statement st = randomStatments.get(j - 1);
			
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
				
				EvaluationResult result = new EvaluationResult(id);
				result.setGraph(graph);
				result.setSubject(st.getSubject().toString());
				result.setPredicate(st.getPredicate().toString());
				result.setObject(st.getObject().toString());
				result.setCorrect(true);
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