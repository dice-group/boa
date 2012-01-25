package de.uni_leipzig.simba.boa.frontend;

import java.util.Arrays;
import java.util.TreeSet;

import com.vaadin.Application;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;
import de.uni_leipzig.simba.boa.frontend.data.PatternContainer;
import de.uni_leipzig.simba.boa.frontend.ui.DatabaseNavigationTree;
import de.uni_leipzig.simba.boa.frontend.ui.PatternTable;
import de.uni_leipzig.simba.boa.frontend.ui.PatternWindow;
import de.uni_leipzig.simba.boa.frontend.ui.RdfModelTree;
import de.uni_leipzig.simba.boa.frontend.ui.TripleTable;

@SuppressWarnings("serial")
public class BoaFrontendApplication extends Application implements ItemClickListener, Action.Handler, Button.ClickListener, TextChangeListener {

	private NLPediaSetup setup = new NLPediaSetup(false);
	private NLPediaLogger logger = new NLPediaLogger(BoaFrontendApplication.class);

	private Button triplesButton = new Button("Triples");
	private Button databasesButton = new Button("Pattern Library");
	private Button sparqlButton = new Button("SPARQL");
	private Button startQuery = new Button("Query"); 
	
	private DatabaseNavigationTree tree;
	private DatabaseNavigationTree tripleTree;
	private RdfModelTree rdfSparqlTree;
	private PatternTable patternTable;
	
	private TextArea sparqlQueryString;
	private String sparqlQueryModel = "";
	
	private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
	
	public static String CURRENT_DATABASE;
	public static String CURRENT_INDEX_DIR = "";
	
	private PatternMapping currentPatternMapping;
	
	private TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);

	@Override
	public void init() {
		
		System.out.println("Total memory available in MB: " + Runtime.getRuntime().totalMemory() / NLPediaSettings.MEGABYTE);;
		buildMainLayout();
	}
	
	public void buttonClick(ClickEvent event) {
		
		final Button source = event.getButton();
		
		if (source == this.triplesButton) {
			
			TripleTable table = new TripleTable(this, tripleTree.getFirstUri());
			// setze linken teil auf die modelle
			horizontalSplitPanel.setFirstComponent(tripleTree);
			// in den rechten teil kommt die erkl�rung
			horizontalSplitPanel.setSecondComponent(table);
			horizontalSplitPanel.setSplitPosition(280, HorizontalSplitPanel.UNITS_PIXELS);
		}
		else if ( source == this.databasesButton ) {
			
			this.buildHomeLayout();
		}
		else if ( source == this.sparqlButton ) {
			
			Panel p = new Panel();
			p.addComponent(new Label("Hier kann man SPARQL Queries starten!"));
			// setze linken teil auf die modelle
			horizontalSplitPanel.setFirstComponent(rdfSparqlTree);			
			// in denrechten teil kommt die erkl�rung
			horizontalSplitPanel.setSecondComponent(p);
			horizontalSplitPanel.setSplitPosition(130, HorizontalSplitPanel.UNITS_PIXELS);
		}
	}

	public void itemClick(ItemClickEvent event) {
		
		if (event.getSource() == tree) {
			
			String itemId = (String) event.getItemId();
			
			if (itemId != null) {
				
				String database = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.DATABASE_ID).getValue();
				String uri = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.URI).getValue();
//				String id = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.PATTERN_MAPPING_ID).getValue();
				
				BoaFrontendApplication.CURRENT_DATABASE		= database;
				BoaFrontendApplication.CURRENT_INDEX_DIR	= NLPediaSettings.getInstance().getSetting(BoaFrontendApplication.CURRENT_DATABASE);
				HibernateFactory.changeConnection(BoaFrontendApplication.CURRENT_DATABASE);
				
				PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
				
				long start = System.currentTimeMillis();
				NLPediaSettings.getInstance().printMemoryUsage();
				this.currentPatternMapping = pmDao.findPatternMappingByUri(uri);
				System.out.println("pmDao.findPatternMappingByUri() took: " + (System.currentTimeMillis() - start) + "ms.");
				NLPediaSettings.getInstance().printMemoryUsage();
				
//				this.currentPatternMapping = new PatternMapping("http://dbpedia.org/ontology/capital", "capital", "http://dbpedia.org/ontology/PopulatedPlace", "http://dbpedia.org/ontology/City");

				GridLayout gridLayout = new GridLayout(4,4);
				gridLayout.setSpacing(true);
				gridLayout.setMargin(true);
				gridLayout.setSizeFull();
				
				Label uriLabel = new Label("URI:");
				Link uriLink = new Link(this.currentPatternMapping.getProperty().getUri(), new ExternalResource(this.currentPatternMapping.getProperty().getUri()));
				gridLayout.addComponent(uriLabel,0,0);
				gridLayout.addComponent(uriLink,1,0);
				
				Label rdfsDomainLabel = new Label("rdfs:domain (?D?)");
				String domain = this.currentPatternMapping.getProperty().getRdfsDomain() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsDomain();
				Link rdfsDomainLink = new Link(domain, new ExternalResource(domain));
				gridLayout.addComponent(rdfsDomainLabel,0,1);
				gridLayout.addComponent(rdfsDomainLink,1,1);
				
				Label rdfsRangeLabel = new Label("rdfs:range (?R?)");
				String range = this.currentPatternMapping.getProperty().getRdfsRange() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsRange();
				Link rdfsRangeLink = new Link(range, new ExternalResource(range));
				gridLayout.addComponent(rdfsRangeLabel,0,2);
				gridLayout.addComponent(rdfsRangeLink,1,2);
				
				Label rdfsLabel = new Label("rdfs:label");
				Label rdfsLabelString = new Label(this.currentPatternMapping.getProperty().getLabel());
				gridLayout.addComponent(rdfsLabel,0,3);
				gridLayout.addComponent(rdfsLabelString,1,3);
				
				VerticalSplitPanel vPanel = new VerticalSplitPanel();
				vPanel.setFirstComponent(gridLayout);
				vPanel.setSplitPosition(25);
				
				try {

					start = System.currentTimeMillis();
					NLPediaSettings.getInstance().printMemoryUsage();
					this.patternTable = new PatternTable(this, new PatternContainer(this.currentPatternMapping));
					System.out.println("Loading PatternContainer took: " + (System.currentTimeMillis() - start) + "ms.");
					NLPediaSettings.getInstance().printMemoryUsage();
					
//					this.patternTable = new PatternTable(this, PatternContainer.createTestPatternContainer());
					vPanel.setSecondComponent(this.patternTable);
				}
				catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.horizontalSplitPanel.setSecondComponent(vPanel);
			}
		}
		else if (event.getSource() == rdfSparqlTree) {
			
			this.sparqlQueryModel = (String) event.getItemId();
			
			VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();

			VerticalLayout hLayout = new VerticalLayout();
			
			this.sparqlQueryString = new TextArea("SPARQL-Querie", "SELECT * \nWHERE {\n\t ?s ?p ?o .\n}");
	        sparqlQueryString.setRows(10);
	        sparqlQueryString.setColumns(50);
	        sparqlQueryString.addListener((TextChangeListener) this);
	        sparqlQueryString.setImmediate(true);
	        
	        startQuery.addListener(this); // react to clicks
	        
	        hLayout.addComponent(sparqlQueryString);
	        hLayout.addComponent(startQuery);
	        hLayout.setSpacing(true);
	        hLayout.setMargin(true);
	        
	        verticalSplitPanel.addComponent(hLayout);
	        verticalSplitPanel.setSplitPosition(230, VerticalSplitPanel.UNITS_PIXELS);
	        verticalSplitPanel.setMargin(true);
	        verticalSplitPanel.setSizeFull();
	        
	        this.horizontalSplitPanel.setSecondComponent(verticalSplitPanel);
		}
		else if (event.getSource() == tripleTree) {
			
			String itemId = (String) event.getItemId();
			
			if (itemId != null) {
				
				String uri				= itemId.substring(itemId.indexOf(":") + 1);
				TripleTable table 		= new TripleTable(this, uri);
				// setze linken teil auf die modelle
				horizontalSplitPanel.setFirstComponent(tripleTree);
				// in den rechten teil kommt die erkl�rung
				horizontalSplitPanel.setSecondComponent(table);
			}
		}
		else if (event.getSource() == this.patternTable ) {
			
			Pattern pattern = (Pattern) event.getItemId();
			long start = System.currentTimeMillis();
			NLPediaSettings.getInstance().printMemoryUsage();
	        this.getMainWindow().addWindow(new PatternWindow(this, pattern, this.currentPatternMapping));
	        System.out.println("Loading Pattern Window took: " + (System.currentTimeMillis() - start) + "ms.");
	        NLPediaSettings.getInstance().printMemoryUsage();
		}
	}
	
	private TreeSet<String> createDummySentences() {

		return new TreeSet<String>(Arrays.asList("Humboldt University of Berlin is the oldest university in Germany's capital city, Berlin.", 
				"As well as all of this, Germany's capital city Berlin is one of the biggest and liveliest in Europe, with the country's politicians mixing with office workers, artists and young people who love to party!"));
	}

	private void buildMainLayout() {
		
		this.setMainWindow(new Window("Boa Frontend"));
		this.setTheme("boa");
		
		long start = System.currentTimeMillis();
		NLPediaSettings.getInstance().printMemoryUsage();
		this.tree = new DatabaseNavigationTree(this);
		System.out.println("building database nav tree took: " + (System.currentTimeMillis() - start) + "ms.");
		NLPediaSettings.getInstance().printMemoryUsage();
		
		this.tripleTree = new DatabaseNavigationTree(this);
		this.rdfSparqlTree = new RdfModelTree(this);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.addComponent(this.createToolbar());
		layout.addComponent(horizontalSplitPanel);
		layout.setExpandRatio(horizontalSplitPanel, 1);
		
		horizontalSplitPanel.setSplitPosition(280, HorizontalSplitPanel.UNITS_PIXELS);
		horizontalSplitPanel.setFirstComponent(tree);
		
		this.buildHomeLayout();
		
		this.getMainWindow().setContent(layout);
	}
	
	private GridLayout createToolbar() {
		
		GridLayout lo = new GridLayout(30,1);
		databasesButton.setWidth("70px");
		lo.addComponent(databasesButton,0,0);
		lo.addComponent(triplesButton,1,0);
		lo.addComponent(sparqlButton,2,0);
		
		triplesButton.addListener((ClickListener) this);
		sparqlButton.addListener((ClickListener) this);
		databasesButton.addListener((ClickListener) this);
		
		triplesButton.setIcon(new ThemeResource("icons/32/folder-add.png"));
		sparqlButton.setIcon(new ThemeResource("icons/32/document-edit.png"));
		databasesButton.setIcon(new ThemeResource("icons/32/globe.png"));
		
		lo.setMargin(true);
		lo.setSpacing(false);
		lo.setStyleName("toolbar");
		lo.setWidth("100%");
		
		this.horizontalSplitPanel.setSizeFull();

//		Embedded em = new Embedded("", new ThemeResource("images/logo.png"));
//		lo.addComponent(em);
//		lo.setComponentAlignment(em, Alignment.MIDDLE_RIGHT);
//		lo.setExpandRatio(em, 1);

		return lo;
	}
	
	private void buildHomeLayout() {

		Label preformattedText = new Label(
				"<b><h1>BOA - a framework for BOostrapping the datA web.</h1></b><br/>" +
				"The idea behind BOA is to use the Data Web as background knowledge for the extraction of " +
				"natural language patterns that represent predicates found on the Data Web. These patterns " +
				"are used to extract instance knowledge from natural language text. This knowledge is " +
				"finally fed back into the Data Web, therewith closing the loop. <br/><br/>" +
                "This is a first GUI version of BOA:\n" +
				"<ol><li>Select a corpus on the left side</li>" +
				"<li>Select a property in the tree</li>" +
				"<li>View the patterns</li>" +
				"<li>Click on a pattern to see the details!</li></ol>" +
				"<b>The BOA Architecture:</b>");
        preformattedText.setContentMode(Label.CONTENT_XHTML);
		
        
		Panel p =  new Panel();
		Embedded e = new Embedded("", new ThemeResource("images/BOA_Architecture.png"));
		e.setWidth(700, Sizeable.UNITS_PIXELS);
		p.setSizeFull();
		p.addComponent(preformattedText);
		p.addComponent(e);
		horizontalSplitPanel.setFirstComponent(tree);
        horizontalSplitPanel.setSecondComponent(p);
	}

	@Override
	public void textChange(TextChangeEvent event) {

		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Action[] getActions(Object target, Object sender) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleAction(Action action, Object sender, Object target) {

		// TODO Auto-generated method stub
		
	}
}
