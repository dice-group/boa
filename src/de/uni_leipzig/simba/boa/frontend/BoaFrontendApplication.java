package de.uni_leipzig.simba.boa.frontend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;
import de.uni_leipzig.simba.boa.frontend.data.PatternContainer;
import de.uni_leipzig.simba.boa.frontend.ui.DatabaseNavigationTree;
import de.uni_leipzig.simba.boa.frontend.ui.PatternTable;
import de.uni_leipzig.simba.boa.frontend.ui.PatternWindow;
import de.uni_leipzig.simba.boa.frontend.ui.TripleTable;

@SuppressWarnings("serial")
public class BoaFrontendApplication extends Application implements ItemClickListener, Button.ClickListener, Property.ValueChangeListener {

    private NLPediaSetup setup = new NLPediaSetup(false);
    private NLPediaLogger logger = new NLPediaLogger(BoaFrontendApplication.class);

    private Button triplesButton = new Button("Triples");
    private Button sourceButton = new Button("Source");
    private Button downloadsButton = new Button("Downloads");
    private Button databasesButton = new Button("Pattern Library");
    private ComboBox patternSearchField = new ComboBox();
    
    private DatabaseNavigationTree tree;
    private DatabaseNavigationTree tripleTree;
    private PatternTable patternTable;
    
    private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    
    public static String CURRENT_DATABASE;
    public static String CURRENT_INDEX_DIR = "";

    private IndexedContainer nlrPatternContainer; 
    private PatternMapping currentPatternMapping;
    private PatternMappingManager patternMappingManager = new PatternMappingManager();
    private List<PatternMapping> mappings;
    
    @Override
    public void init() {
        
        mappings = this.patternMappingManager.getPatternMappings(CURRENT_DATABASE + "/patternmappings/");
        nlrPatternContainer = this.getNaturalLanguagePatternContainer();
        this.tree = new DatabaseNavigationTree(this, mappings);
        this.tripleTree = new DatabaseNavigationTree(this, mappings);

        buildMainLayout();
    }
    
    public void buttonClick(ClickEvent event) {
        
        final Button source = event.getButton();
        
        if (source == this.triplesButton) {
            
//            TripleTable table = new TripleTable(this, tripleTree.getFirstUri());
            horizontalSplitPanel.setFirstComponent(tripleTree);
            
            VerticalLayout triples = new VerticalLayout();
            triples.setSizeFull();
            triples.setMargin(true);
            triples.addComponent(new Label("Triples"));
            
            horizontalSplitPanel.setSecondComponent(triples);
        }
        else if ( source == this.databasesButton ) {
            
            horizontalSplitPanel.setSecondComponent(buildStartPage());
        }
        else if ( source == this.downloadsButton ) {
            
            VerticalLayout downloads = new VerticalLayout();
            downloads.setSizeFull();
            downloads.setMargin(true);
            downloads.addComponent(new Label("Downloads"));
            
            horizontalSplitPanel.setSecondComponent(downloads);
        }
        else if ( source == this.sourceButton ) {
            
            VerticalLayout sourceCode = new VerticalLayout();
            sourceCode.setSizeFull();
            sourceCode.setMargin(true);
            sourceCode.addComponent(new Label("Source Code"));
            
            horizontalSplitPanel.setSecondComponent(sourceCode);
        }
    }

    public void itemClick(ItemClickEvent event) {
        
        if (event.getSource() == tree) {
            
            String itemId = (String) event.getItemId();
            
            if (itemId != null) {
                
                String database = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.DATABASE_ID).getValue();
                String uri = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.URI).getValue();
                
                this.currentPatternMapping = this.patternMappingManager.getPatternMapping(uri, database);
                
                HorizontalLayout propertyInfos = new HorizontalLayout();
                propertyInfos.setSizeFull();
                
                GridLayout uriAndLabel = new GridLayout(2,2);
                uriAndLabel.setSizeFull();
                uriAndLabel.setSpacing(true);
                uriAndLabel.setMargin(true);
                
                Label uriLabel = new Label("URI:");
                Link uriLink = new Link(this.currentPatternMapping.getProperty().getUri(), new ExternalResource("http://dbpedia.org/ontology/" + this.currentPatternMapping.getProperty().getUri()));
                uriAndLabel.addComponent(uriLabel, 0, 0);
                uriAndLabel.addComponent(uriLink, 1, 0);
                
                Label rdfsLabel = new Label("rdfs:label");
                Label rdfsLabelString = new Label(this.currentPatternMapping.getProperty().getLabel());
                uriAndLabel.addComponent(rdfsLabel, 0, 1);
                uriAndLabel.addComponent(rdfsLabelString, 1, 1);
                
                GridLayout domainAndRange = new GridLayout(2,2);
                domainAndRange.setSizeFull();
                domainAndRange.setSpacing(true);
                domainAndRange.setMargin(true);
                
                Label rdfsDomainLabel = new Label("rdfs:domain (?D?)");
                String domain = this.currentPatternMapping.getProperty().getRdfsDomain() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsDomain();
                Link rdfsDomainLink = new Link(domain, new ExternalResource(domain));
                domainAndRange.addComponent(rdfsDomainLabel, 0, 0);
                domainAndRange.addComponent(rdfsDomainLink, 1, 0);

                Label rdfsRangeLabel = new Label("rdfs:range (?R?)");
                String range = this.currentPatternMapping.getProperty().getRdfsRange() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsRange();
                Link rdfsRangeLink = new Link(range, new ExternalResource(range));
                domainAndRange.addComponent(rdfsRangeLabel, 0, 1);
                domainAndRange.addComponent(rdfsRangeLink, 1, 1);
                
                propertyInfos.addComponent(uriAndLabel);
                propertyInfos.addComponent(domainAndRange);
                
                VerticalSplitPanel vPanel = new VerticalSplitPanel();
                vPanel.setFirstComponent(propertyInfos);
                vPanel.setSplitPosition(20);
                vPanel.setLocked(true);
                
                this.patternTable = new PatternTable(this, new ArrayList<Pattern>(this.currentPatternMapping.getPatterns()));
                vPanel.setSecondComponent(this.patternTable);
                this.horizontalSplitPanel.setSecondComponent(vPanel);
            }
        }
        else if (event.getSource() == tripleTree) {
            
            String itemId = (String) event.getItemId();
            
            if (itemId != null) {
                
                String uri              = itemId.substring(itemId.indexOf(":") + 1);
                TripleTable table       = new TripleTable(this, uri);
                // setze linken teil auf die modelle
                horizontalSplitPanel.setFirstComponent(tripleTree);
                // in den rechten teil kommt die erkl�rung
                horizontalSplitPanel.setSecondComponent(table);
            }
        }
        else if (event.getSource() == this.patternTable ) {
            
            Pattern pattern = this.patternTable.getSelectedPattern((Integer) event.getItemId());
            this.getMainWindow().addWindow(new PatternWindow(this, pattern, this.currentPatternMapping));
        }
    }
    
    private void buildMainLayout() {
        
        Window main = new Window("Boa Frontend");
        main.setContent(new VerticalLayout());
        this.setMainWindow(main);
        this.setTheme("boa");
        
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("90%");
        layout.setHeight("90%");
        GridLayout topGrid = this.createToolbar();
        topGrid.setHeight("75%");
        topGrid.setWidth("97%");
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(topGrid);
        layout.addComponent(horizontalSplitPanel);
        
        horizontalSplitPanel.setSplitPosition(150, HorizontalSplitPanel.UNITS_PIXELS);
        horizontalSplitPanel.setFirstComponent(tree);
        horizontalSplitPanel.setSecondComponent(buildStartPage());
        horizontalSplitPanel.setWidth("97%");
        horizontalSplitPanel.setLocked(true);
        
        layout.setExpandRatio(topGrid, 1f);
        layout.setExpandRatio(horizontalSplitPanel, 5f);
        
        layout.setComponentAlignment(horizontalSplitPanel, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(topGrid, Alignment.TOP_CENTER);
        
        this.getMainWindow().getContent().addComponent(layout);
        VerticalLayout content = (VerticalLayout) this.getMainWindow().getContent();
        content.setSizeFull();
        content.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
    }
    
    private GridLayout createToolbar() {
        
        GridLayout topGrid = new GridLayout(2,1);
        
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setHeight("100%");
        buttons.setWidth("100%");
        buttons.addComponent(databasesButton);
        buttons.addComponent(triplesButton);
        buttons.addComponent(sourceButton);
        buttons.addComponent(downloadsButton);
        buttons.setComponentAlignment(databasesButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(triplesButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(sourceButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(downloadsButton, Alignment.MIDDLE_LEFT);
        
        patternSearchField = new ComboBox(null, nlrPatternContainer);
        patternSearchField.setWidth("60%");
        patternSearchField.setStyleName("patternSearch");
        patternSearchField.setValue("Search for a pattern...");
        patternSearchField.setItemCaptionPropertyId("NLR");
        patternSearchField.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        patternSearchField.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
        patternSearchField.setNullSelectionAllowed(false);
        patternSearchField.setImmediate(true);
        
        VerticalLayout search = new VerticalLayout();
        search.setWidth("100%");
        search.setHeight("100%");
        search.setMargin(true);
        search.addComponent(patternSearchField);
        search.setComponentAlignment(patternSearchField, Alignment.MIDDLE_RIGHT);
        
        topGrid.addComponent(buttons, 0, 0);
        topGrid.addComponent(search, 1, 0);
        topGrid.setStyleName("toolbar");
        topGrid.setSizeFull();
        topGrid.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
        
        triplesButton.addListener((ClickListener) this);
        databasesButton.addListener((ClickListener) this);
        sourceButton.addListener((ClickListener) this);
        downloadsButton.addListener((ClickListener) this);
        patternSearchField.addListener((Property.ValueChangeListener) this);
        
        return topGrid;
    }
    
    public IndexedContainer getNaturalLanguagePatternContainer() {
        
        IndexedContainer naturalLanguagePatternContainer = new IndexedContainer();
        naturalLanguagePatternContainer.addContainerProperty("NLR", String.class, "");
        naturalLanguagePatternContainer.addContainerProperty("PATTERN", Pattern.class, "");
        naturalLanguagePatternContainer.addContainerProperty("MAPPING", PatternMapping.class, "");

        for (PatternMapping mapping : this.mappings ) {
            for ( Pattern pattern : mapping.getPatterns() ) {
                
                Item item = naturalLanguagePatternContainer.addItem(mapping.getProperty().getUri() + " " + pattern.getNaturalLanguageRepresentation());
                item.getItemProperty("NLR").setValue(pattern.getNaturalLanguageRepresentation());
                item.getItemProperty("PATTERN").setValue(pattern);
                item.getItemProperty("MAPPING").setValue(mapping);
            }
        }
        return naturalLanguagePatternContainer;
    }
    
    
    private ComponentContainer buildStartPage() {

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
        
        VerticalLayout p =  new VerticalLayout();
        p.setMargin(true);
        
        Embedded e = new Embedded("", new ThemeResource("images/BOA_Architecture.png"));
        e.setWidth("700px");
        e.setHeight("424px");
        
        p.addComponent(preformattedText);
        p.addComponent(e);
        return p;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {

        Pattern pattern = (Pattern) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "PATTERN").getValue();
        PatternMapping mapping = (PatternMapping) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "MAPPING").getValue();
        this.patternSearchField.setValue(null);
        getMainWindow().addWindow(new PatternWindow(this, pattern, mapping));
    }
}