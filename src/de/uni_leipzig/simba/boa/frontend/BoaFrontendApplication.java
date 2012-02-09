package de.uni_leipzig.simba.boa.frontend;

import com.vaadin.Application;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.frontend.data.PatternContainer;
import de.uni_leipzig.simba.boa.frontend.ui.PatternTable;
import de.uni_leipzig.simba.boa.frontend.ui.DatabaseNavigationTree;
import de.uni_leipzig.simba.boa.frontend.ui.RdfModelTree;
import de.uni_leipzig.simba.boa.frontend.ui.StatementForm;

@SuppressWarnings("serial")
public class BoaFrontendApplication extends Application implements ItemClickListener, Action.Handler, Button.ClickListener {

	private NLPediaSetup setup = new NLPediaSetup(false);
	private NLPediaLogger logger = new NLPediaLogger(BoaFrontendApplication.class);

	private Button evaluationButton = new Button("Evaluation");
	private Button databasesButton = new Button("Corpora");
	
	private DatabaseNavigationTree tree;
	private RdfModelTree rdfTree;
	
	private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();

	@Override
	public void init() {
		
		buildMainLayout();
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
	
	public void buttonClick(ClickEvent event) {
		
		final Button source = event.getButton();
		
		if (source == this.evaluationButton) {
			
			Panel p = new Panel();
			// setze linken teil auf die modelle
			horizontalSplitPanel.setFirstComponent(rdfTree);
			// in den rechten teil kommt die erkl�rung
			horizontalSplitPanel.setSecondComponent(p);
		}
		else if ( source == this.databasesButton ) {
			
			Panel p = new Panel();
			p.addComponent(new Label("Hier kann man sich die Patterns anschauen"));
			// setze linken teil auf die modelle
			horizontalSplitPanel.setFirstComponent(tree);			
			// in denrechten teil kommt die erkl�rung
			horizontalSplitPanel.setSecondComponent(p);
		}
	}
	
	public void itemClick(ItemClickEvent event) {
		
		if (event.getSource() == tree) {
			
			String itemId = (String) event.getItemId();
			
			if (itemId != null) {
				
				String database = itemId.substring(0, itemId.indexOf(":"));
				String uri		= itemId.substring(itemId.indexOf(":") + 1);
				
				HibernateFactory.changeConnection(database);
				
				PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
				PatternMapping pm = pmDao.findPatternMappingsWithoutPattern(uri).get(0);
				pm = pmDao.findPatternMapping(pm.getId());
				
				GridLayout gridLayout = new GridLayout(5,6);
				gridLayout.setSpacing(true);
				gridLayout.setMargin(false);
				gridLayout.setSizeFull();
				
				gridLayout.addComponent(new Label(""), 0, 0);
				
				Label rdfsDomainLabel = new Label("rdfs:domain");
				gridLayout.addComponent(rdfsDomainLabel, 1, 0);
				gridLayout.setComponentAlignment(rdfsDomainLabel, Alignment.MIDDLE_LEFT);
				
				Link rdfsDomainLink = new Link(pm.getProperty().getRdfsDomain(), new ExternalResource(pm.getProperty().getRdfsDomain()));
				gridLayout.addComponent(rdfsDomainLink, 2, 0);
				gridLayout.setComponentAlignment(rdfsDomainLink, Alignment.MIDDLE_LEFT);
				
				Label rdfsRangeLabel = new Label("rdfs:range");
				gridLayout.addComponent(rdfsRangeLabel, 3, 0);
				gridLayout.setComponentAlignment(rdfsRangeLabel, Alignment.MIDDLE_RIGHT);
				
				Link rdfsRangeLink = new Link(pm.getProperty().getRdfsRange(), new ExternalResource(pm.getProperty().getRdfsRange()));
				gridLayout.addComponent(rdfsRangeLink, 4, 0);
				gridLayout.setComponentAlignment(rdfsRangeLink, Alignment.MIDDLE_LEFT);
				
				try {
					
					PatternTable table = new PatternTable(this, new PatternContainer(pm));
					gridLayout.addComponent(table, 0, 1, 4, 5);
				}
				catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.horizontalSplitPanel.setSecondComponent(gridLayout);
			}
		}
		if (event.getSource() == rdfTree) {
			
			String itemId = (String) event.getItemId();
			
			if (itemId != null) {
				
				Panel p = new Panel();
				p.addComponent(new StatementForm(this, itemId));
				this.horizontalSplitPanel.setSecondComponent(p);
			}
		}
	}
	
	private void buildMainLayout() {
		
		this.setMainWindow(new Window("Boa Frontend"));
		this.setTheme("boa");
		
		this.tree = new DatabaseNavigationTree(this, null); // TODO FIXs
		this.rdfTree = new RdfModelTree(this);
		
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.addComponent(this.createToolbar());
		layout.addComponent(horizontalSplitPanel);
		layout.setExpandRatio(horizontalSplitPanel, 1);
		
		horizontalSplitPanel.setSplitPosition(400, HorizontalSplitPanel.UNITS_PIXELS);
		horizontalSplitPanel.setFirstComponent(tree);
		
		this.getMainWindow().setContent(layout);
	}
	
	private GridLayout createToolbar() {
		
		GridLayout lo = new GridLayout(30,1);
		lo.addComponent(databasesButton,0,0);
		lo.addComponent(evaluationButton,1,0);
		
		evaluationButton.addListener((ClickListener) this);
		databasesButton.addListener((ClickListener) this);

		evaluationButton.setIcon(new ThemeResource("icons/32/folder-add.png"));
		databasesButton.setIcon(new ThemeResource("icons/32/users.png"));
		
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
}
