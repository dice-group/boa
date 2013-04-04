package de.uni_leipzig.simba.boa.frontend;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.apache.commons.io.FileUtils;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import de.uni_leipzig.simba.boa.backend.concurrent.PrintJvmMemoryTimerTask;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.GeneralizedPattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationIndexCreator;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationManager;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.frontend.data.AutosuggestionsManager;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;
import de.uni_leipzig.simba.boa.frontend.ui.DatabaseNavigationTree;
import de.uni_leipzig.simba.boa.frontend.ui.PatternTable;
import de.uni_leipzig.simba.boa.frontend.ui.PatternWindow;
import de.uni_leipzig.simba.boa.webservice.client.TextToRdfClient;
import edu.stanford.nlp.util.StringUtils;

@SuppressWarnings("serial")
public class BoaFrontendApplication extends Application implements ItemClickListener, Button.ClickListener, Property.ValueChangeListener {

    public static final NLPediaSetup setup = new NLPediaSetup(false);
    private NLPediaLogger logger = new NLPediaLogger(BoaFrontendApplication.class);

    private TextArea input = new TextArea();
    private TextArea output = new TextArea();
    
    private Button triplesButton = new Button("Try");
    private Button sourceButton = new Button("Source");
    private Button downloadsButton = new Button("Downloads");
    private Button databasesButton = new Button("Pattern Library");
    private Button publicationsButton = new Button("Publications");
    private Button inputToOutputButton = new Button("Extract RDF");
    
    private NativeSelect databaseSelect = new NativeSelect("");
    private ComboBox patternSearchField = new ComboBox();
    
    private DatabaseNavigationTree tree;
    private PatternTable patternTable;
    
    private VerticalLayout mainLayout = new VerticalLayout();
    private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    
    public static String CURRENT_DATABASE;
    public static String CURRENT_INDEX_DIR = "";
    
    static {
        
        Timer timer = new Timer();
        timer.schedule(new PrintJvmMemoryTimerTask(), 0, 30000);
    }

    private static IndexedContainer nlrPatternContainer; 
    private PatternMapping currentPatternMapping;
    
    private Map<String, Set<PatternMapping>> mappingsInDatabases;
    
    @Override
    public void init() {
        
        this.mappingsInDatabases = PatternMappingManager.getInstance().getPatternMappingsInDatabases();
        this.tree = new DatabaseNavigationTree(this, mappingsInDatabases);
        nlrPatternContainer = AutosuggestionsManager.getInstance().getNaturalLanguagePatternContainer(mappingsInDatabases);
        this.inputToOutputButton.addListener((ClickListener) this);
        this.databaseSelect.setNullSelectionAllowed(false);
        this.databaseSelect.setImmediate(true);
        
        List<String> keys = new ArrayList<String>(this.mappingsInDatabases.keySet());
        
        for (String databaseKey : keys) 
            if ( !databaseKey.isEmpty() && databaseKey != null ) databaseSelect.addItem(databaseKey);
                
        this.databaseSelect.select(keys.get(0));
        buildMainLayout();
    }
    
    public void buttonClick(ClickEvent event) {
        
        final Button source = event.getButton();
        
        if ( source == this.inputToOutputButton ) {
            
            this.output.setValue(this.extractRdf(this.input.getValue().toString(), this.databaseSelect.getValue().toString()));
            return;
        }
        
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();
        mainLayout.removeAllComponents();
        mainLayout.addComponent(panel);
        
        if (source == this.triplesButton) {
            
            panel.addComponent(buildTriplePage());
        }
        else 
            if ( source == this.databasesButton ) {
            
            mainLayout.removeAllComponents();
            mainLayout.addComponent(buildStartPage());
        }
        else if ( source == this.downloadsButton ) {
            
            panel.addComponent(buildDownloadsPage());
        }
        else if ( source == this.sourceButton ) {
            
            panel.addComponent(buildSourceCodePage());
        }
        else if ( source == this.publicationsButton ) {
            
            panel.addComponent(buildPublicationsPage());
        }
    }

    private Component buildPublicationsPage() {

        VerticalLayout publications = new VerticalLayout();
        publications.setMargin(true);
        
        Label publicationsHtml = null;
        try {
            
            publicationsHtml = new Label(FileUtils.readFileToString(new File(NLPediaSettings.BOA_BASE_DIRECTORY + "gui/publications.html")));
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }
        publications.addComponent(publicationsHtml);
        publicationsHtml.setContentMode(Label.CONTENT_XHTML);
        
        return publicationsHtml;
    }

    private Component buildDownloadsPage() {

        VerticalLayout downloads = new VerticalLayout();
        downloads.setSizeFull();
//        downloads.setMargin(true);
        
        HorizontalLayout patternIndexDownloads = new HorizontalLayout();
        
        Label indexHtml = null;
        try {
            
            indexHtml = new Label(FileUtils.readFileToString(new File(NLPediaSettings.BOA_BASE_DIRECTORY + "gui/index.html")));
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }
        patternIndexDownloads.addComponent(indexHtml);
        indexHtml.setContentMode(Label.CONTENT_XHTML);
        
        // ####################################################

        return indexHtml;
    }

    private Component buildTriplePage() {

//        String urlString = "http://live.dbpedia.org/sparql?" +
//                "default-graph-uri=http%3A%2F%2Fboa.aksw.org&" +
//                "qtxt=SELECT%20%3Fcompany1Label%20%3Fcompany2Label%20%0A" +
//                "WHERE%20%7B%20%0A%20%20%20%20%20%3Fcompany1%20%3Chttp%3A%2F%2Fboa.aksw.org%2Fontology%2Fsubsidiary%3E%20%3Fcompany2%20.%20%0A%20%20%20%20%20%3F" +
//                "company1%20rdfs%3Alabel%20%3Fcompany1label%20.%20%0A%20%20%20%20%20%3Fcompany2%20rdfs%3Alabel%20%3Fcompany2Label%20.%20%0A%7D%0ALIMIT%20100%0A";
        
//        String urlString = "http://139.18.2.164:8000/test/";
        String urlString = "http://139.18.2.164/sparqlfront";
        
        VerticalLayout triples = new VerticalLayout();
        triples.setSizeFull();
        triples.setMargin(true);
        Label preformattedText = new Label(
                "<b><h1>Query BOA's knowledge base:</h1></b><br/>" +
                "You can query BOA's knowledge base with SPARQL <a style='font-size: 1.4em;' href='"+urlString+"'>here</a> or you can download the generated triples in the 'Downloads' sections.<br/><br/>");
        preformattedText.setContentMode(Label.CONTENT_XHTML);
        triples.addComponent(preformattedText);

        Label preformattedTextWebService = new Label(
                "<b><h1>BOA's text to RDF Web-Service:</h1></b><br/>" +
                "Input text here and extract RDF or try the WebService! For more information please refer to the <a href='http://code.google.com/p/boa/wiki/WebService' >web service wiki page</a>! <br/><br/>");
        preformattedTextWebService.setContentMode(Label.CONTENT_XHTML);
        triples.addComponent(preformattedTextWebService);
        
        VerticalLayout layout = new VerticalLayout();
        
        this.input = new TextArea(null, "The versificator is a fictional device employed by Ingsoc in the novel ``Nineteen Eighty-Four'' by George Orwell.");
        this.input.setRows(10);
        this.input.setWidth("100%");
        this.input.addListener(this);
        this.input.setImmediate(true);
        
        this.output = new TextArea(null, "");
        this.output.setRows(10);
        this.output.setWidth("100%");
        this.output.addListener(this);
        this.output.setImmediate(true);
        
        layout.addComponent(input);
        
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setSizeFull();
        hlayout.setHeight("50px");
        hlayout.addComponent(databaseSelect);
        hlayout.addComponent(inputToOutputButton);
        hlayout.setComponentAlignment(databaseSelect, Alignment.TOP_CENTER);
        hlayout.setComponentAlignment(inputToOutputButton, Alignment.MIDDLE_CENTER);
        
        layout.addComponent(hlayout);
        layout.addComponent(output);
        
        triples.addComponent(layout);
        
        return triples;
    }
    
    private Component buildSourceCodePage() {

        String urlString = "http://code.google.com/p/boa";
        
        VerticalLayout sourceCode = new VerticalLayout();
        sourceCode.setWidth("60%");
        sourceCode.setMargin(true);
        Label preformattedText = new Label(
                "<b><h1>Get involved!</h1></b><br/>" +
                "You can download the BOA framework and all necessarry files from the Google Code project <a class=\"downloadText\" href='"+urlString+"'>page</a>. " +
                		"Everyone is welcome to join the project (just send me a mail) and/or join the mailing list. If you have any questions just ask " +
                		"me or have a look at the Wiki in the Google project site.<br/><br/>" +
                		"You can clone the mercurial repository with the following command: <br/><br/>" +
                		"<b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;hg clone https://code.google.com/p/boa/</i></b>");
        preformattedText.setContentMode(Label.CONTENT_XHTML);
        sourceCode.addComponent(preformattedText);
        
        return sourceCode;
    }

    public void itemClick(ItemClickEvent event) {
        
        if (event.getSource() == tree) {
            
            String itemId = (String) event.getItemId();
            
            if (itemId != null) {
                
                String database = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.DATABASE_ID).getValue();
                String uri = (String) this.tree.getItem(itemId).getItemProperty(DatabaseContainer.URI).getValue();
                
                this.currentPatternMapping = PatternMappingManager.getInstance().getPatternMapping(uri, database);
                
                HorizontalLayout propertyInfos = new HorizontalLayout();
                propertyInfos.setSizeFull();
                propertyInfos.setMargin(false);
                
                GridLayout uriAndLabel = new GridLayout(2,2);
                uriAndLabel.setSizeFull();
                uriAndLabel.setSpacing(true);
                uriAndLabel.setMargin(true);
                
                Label uriLabel = new Label("URI:");
                Link uriLink = new Link(this.currentPatternMapping.getProperty().getUri().replace("http://dbpedia.org/ontology/", "dbpedia-owl:"), new ExternalResource(this.currentPatternMapping.getProperty().getUri()));
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
                String domain = this.currentPatternMapping.getProperty().getRdfsDomain() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsDomain().replace("http://dbpedia.org/ontology/", "dbpedia-owl:");
                Link rdfsDomainLink = new Link(domain, new ExternalResource(this.currentPatternMapping.getProperty().getRdfsDomain()));
                domainAndRange.addComponent(rdfsDomainLabel, 0, 0);
                domainAndRange.addComponent(rdfsDomainLink, 1, 0);
                domainAndRange.setComponentAlignment(rdfsDomainLabel, Alignment.MIDDLE_LEFT);
                domainAndRange.setComponentAlignment(rdfsDomainLink, Alignment.MIDDLE_LEFT);

                Label rdfsRangeLabel = new Label("rdfs:range (?R?)");
                String range = this.currentPatternMapping.getProperty().getRdfsRange() == null ? "not defined" : this.currentPatternMapping.getProperty().getRdfsRange().replace("http://dbpedia.org/ontology/", "dbpedia-owl:");
                Link rdfsRangeLink = new Link(range, new ExternalResource(this.currentPatternMapping.getProperty().getRdfsRange()));
                domainAndRange.addComponent(rdfsRangeLabel, 0, 1);
                domainAndRange.addComponent(rdfsRangeLink, 1, 1);
                domainAndRange.setComponentAlignment(rdfsRangeLabel, Alignment.MIDDLE_LEFT);
                domainAndRange.setComponentAlignment(rdfsRangeLink, Alignment.MIDDLE_LEFT);
                domainAndRange.setColumnExpandRatio(2, 4);
                
                propertyInfos.addComponent(uriAndLabel);
                propertyInfos.setComponentAlignment(uriAndLabel, Alignment.MIDDLE_LEFT);
                propertyInfos.addComponent(domainAndRange);
                propertyInfos.setComponentAlignment(domainAndRange, Alignment.MIDDLE_LEFT);
                
                VerticalSplitPanel vPanel = new VerticalSplitPanel();
                vPanel.setFirstComponent(propertyInfos);
                vPanel.setSplitPosition(21);
                vPanel.setLocked(true);
                
                this.patternTable = new PatternTable(this, new ArrayList<GeneralizedPattern>(this.currentPatternMapping.getGeneralizedPatterns()));
                vPanel.setSecondComponent(this.patternTable);
                this.horizontalSplitPanel.setSecondComponent(vPanel);
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
//        this.getMainWindow().showNotification("Please be patient ...");
        this.setTheme("boa");
        
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("90%");
        layout.setHeight("90%");
        HorizontalLayout topGrid = this.createToolbar();
        topGrid.setHeight("75%");
        topGrid.setWidth("97%");
        layout.setSpacing(true);
        layout.setMargin(true);
        layout.addComponent(topGrid);
        layout.addComponent(mainLayout);
        mainLayout.setHeight("100%");
        mainLayout.setWidth("95%");
        mainLayout.addComponent(horizontalSplitPanel);
        
        horizontalSplitPanel.setSplitPosition(235, HorizontalSplitPanel.UNITS_PIXELS);
        horizontalSplitPanel.setFirstComponent(tree);
        buildStartPage();
        horizontalSplitPanel.setWidth("97%");
        horizontalSplitPanel.setLocked(true);
        
        layout.setExpandRatio(topGrid, 1f);
        layout.setExpandRatio(mainLayout, 5f);
        
        layout.setComponentAlignment(mainLayout, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(topGrid, Alignment.TOP_CENTER);
        
        this.getMainWindow().getContent().addComponent(layout);
        VerticalLayout content = (VerticalLayout) this.getMainWindow().getContent();
        content.setSizeFull();
        content.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
    }
    
    private HorizontalLayout createToolbar() {
        
        HorizontalLayout topGrid = new HorizontalLayout();
        
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setHeight("100%");
        buttons.setWidth("100%");
        buttons.setSpacing(false);
        buttons.addComponent(databasesButton);
//        buttons.addComponent(triplesButton);
        buttons.addComponent(sourceButton);
        buttons.addComponent(downloadsButton);
        buttons.addComponent(publicationsButton);
        buttons.setComponentAlignment(databasesButton, Alignment.MIDDLE_LEFT);
//        buttons.setComponentAlignment(triplesButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(sourceButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(downloadsButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(publicationsButton, Alignment.MIDDLE_LEFT);
        
        patternSearchField = new ComboBox(null, nlrPatternContainer);
        patternSearchField.setWidth("100%");
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
        
        topGrid.addComponent(buttons);
        topGrid.addComponent(search);
        topGrid.setExpandRatio(buttons, 3);
        topGrid.setExpandRatio(search, 2);
        topGrid.setStyleName("toolbar");
        topGrid.setSizeFull();
        topGrid.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
        
        triplesButton.addListener((ClickListener) this);
        databasesButton.addListener((ClickListener) this);
        sourceButton.addListener((ClickListener) this);
        downloadsButton.addListener((ClickListener) this);
        publicationsButton.addListener((ClickListener) this);
        patternSearchField.addListener((Property.ValueChangeListener) this);
        
        return topGrid;
    }
    
    private HorizontalSplitPanel buildStartPage() {

        Label frontpageHtml = null;
        
        try {
            
            frontpageHtml = new Label(FileUtils.readFileToString(new File(NLPediaSettings.BOA_BASE_DIRECTORY + "gui/frontpage.html")));
            frontpageHtml.setContentMode(Label.CONTENT_XHTML);
        }
        catch (IOException e) {
            
            e.printStackTrace();
        }
        
        VerticalLayout p =  new VerticalLayout();
        p.setMargin(true);
        p.addComponent(frontpageHtml);
        
        horizontalSplitPanel.setFirstComponent(tree);
        horizontalSplitPanel.setSecondComponent(p);
        
        return horizontalSplitPanel;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {

        if ( nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "PATTERN") != null ) {
            
            String nlr          = (String) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "PATTERN").getValue();
            String propertyUri  = (String) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "MAPPING").getValue();
            String database     = (String) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "DATABASE").getValue();
            
            for (PatternMapping mapping : this.mappingsInDatabases.get(database)) {
                
                if ( mapping.getProperty().getUri().equals(propertyUri) ) {
                    
                    for ( Pattern pattern : mapping.getPatterns() ) {
                        
                        if ( pattern.getNaturalLanguageRepresentation().equals(nlr) ) {
                            
                            getMainWindow().addWindow(new PatternWindow(this, pattern, mapping));
                            return;
                        }
                    }
                }
            }
        }
    }
    
    private String extractRdf(String text, String key) {
        
        double patternScoreThreshold = 0.0;
        int contextLookAheadThreshold = 3;
        boolean dbpediaLinksOnly = false;
        
        logger.info("Trying to extract triples with patternThreshold(" + patternScoreThreshold + ") and contextLookAheadThreshold(" + contextLookAheadThreshold + ") for text: " + text);
        
        NLPediaSettings.setSetting("pattern.score.threshold.create.knowledge", String.valueOf(patternScoreThreshold));
        NLPediaSettings.setSetting("contextLookAhead", String.valueOf(contextLookAheadThreshold));
        NLPediaSettings.setSetting("triple.score.threshold.create.knowledge", "0.0");
        NLPediaSettings.setSetting("number.of.create.knowledge.threads", "1");
        NLPediaSettings.setSetting("knowledgeCreationThreadPoolSize", "1");

        TextToRdfClient client = new TextToRdfClient();
        String result = client.extractTriples(text, "", patternScoreThreshold, contextLookAheadThreshold, dbpediaLinksOnly);
        return result == null || result.isEmpty() ? "Could not extract any data!" : result;
    }
}