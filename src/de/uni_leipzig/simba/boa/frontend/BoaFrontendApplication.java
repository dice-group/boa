package de.uni_leipzig.simba.boa.frontend;

import java.net.MalformedURLException;
import java.net.URL;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.serialization.PatternMappingManager;
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

    private Button triplesButton = new Button("Query");
    private Button sourceButton = new Button("Source");
    private Button downloadsButton = new Button("Downloads");
    private Button databasesButton = new Button("Pattern Library");
    private Button publicationsButton = new Button("Publications");
    private ComboBox patternSearchField = new ComboBox();
    
    private DatabaseNavigationTree tree;
    private PatternTable patternTable;
    
    private VerticalLayout mainLayout = new VerticalLayout();
    private HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();
    
    public static String CURRENT_DATABASE;
    public static String CURRENT_INDEX_DIR = "";

    private IndexedContainer nlrPatternContainer; 
    private PatternMapping currentPatternMapping;
    private List<PatternMapping> mappings;
    
    @Override
    public void init() {
        
        this.mappings = new ArrayList<PatternMapping>();
        for( PatternMapping mapping : PatternMappingManager.getInstance().getPatternMappings()) {
            
            if (mapping.getPatterns().size() > 0 ) this.mappings.add(mapping);
        }
        nlrPatternContainer = this.getNaturalLanguagePatternContainer();
        this.tree = new DatabaseNavigationTree(this, mappings);

        buildMainLayout();
    }
    
    public void buttonClick(ClickEvent event) {
        
        final Button source = event.getButton();
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setSizeFull();
        mainLayout.removeAllComponents();
        mainLayout.addComponent(panel);
        
        if (source == this.triplesButton) {
            
            panel.addComponent(buildTriplePage());
        }
        else if ( source == this.databasesButton ) {
            
            panel.addComponent(buildStartPage());
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
        
        Label bootstrapping = new Label(
                "<b><h1><a class='downloadText' href='http://svn.aksw.org/papers/2012/ESWC_BOA-ML-PR/public.pdf'>BOA - Bootstrapping Linked Data:</a></h1></b><br/> " +
                "Most knowledge sources on the Data Web were extracted from structured " +
                "or semi-structured data. Thus, they encompass solely a small fraction " +
                "of the information available on the document-oriented Web. In this " +
                "paper, we present BOA, an iterative bootstrapping strategy for extracting " +
                "RDF from unstructured data. The idea behind BOA is to use the Data " +
                "Web as background knowledge for the extraction of natural language " +
                "patterns that represent predicates found on the Data Web. These patterns " +
                "are used to extract instance knowledge from natural language text. " +
                "This knowledge is finally fed back into the Data Web, therewith closing " +
                "the loop. We evaluate our approach on two data sets using DBpedia " +
                "as background knowledge. Our results show that we can extract several " +
                "thousand new facts in one iteration with very high accuracy. Moreover, " +
                "we provide the first repository of natural language representations " +
                "of predicates found on the Data Web.<br/><br/>");
        bootstrapping.setContentMode(Label.CONTENT_XHTML);
        Label bootstrappingBibtex = new Label(
                "<i>@INPROCEEDINGS{Gerber2011, <br/>" +
                "&nbsp;&nbsp;author = {{Daniel Gerber and Axel-Cyrille Ngonga Ngomo}},<br/>" +
                "&nbsp;&nbsp;title = {Bootstrapping the Linked Data Web},<br/>" +
                "&nbsp;&nbsp;booktitle = {1st Workshop on Web Scale Knowledge Extraction @ ISWC 2011},<br/>" +
                "&nbsp;&nbsp;year = {2011}<br/>" +
                "}</i>"
        		);
        bootstrappingBibtex.setContentMode(Label.CONTENT_XHTML);
        
        publications.addComponent(bootstrapping);
        publications.addComponent(bootstrappingBibtex);
        
        Label questionAnswering = new Label(
                "<b><h1><a class='downloadText' href=''>Template-based question answering over RDF data:</a></h1></b><br/> " +
                "As an increasing amount of RDF data is published as Linked Data, " +
                "intuitive ways of accessing this data become more and more important. " +
                "Question answering approaches have been proposed as a good compromise " +
                "between intuitiveness and expressivity. Most question answering systems " +
                "translate questions into triples which are matched against the RDF data " +
                "to retrieve an answer, typically relying on some similarity metric. However, " +
                "in many cases, triples do not represent a faithful representation of the semantic " +
                "structure of the natural language question, with the result that more expressive " +
                "queries can not be answered. To circumvent this problem, we present a novel " +
                "approach that relies on a parse of the question to produce a SPARQL template " +
                "that directly mirrors the internal structure of the question. This template " +
                "is then instantiated using statistical entity identification and predicate " +
                "detection. We show that this approach is competitive and discuss cases of " +
                "questions that can be answered with our approach but not with competing approaches.<br/><br/>");
        questionAnswering.setContentMode(Label.CONTENT_XHTML);
        Label questionAnsweringBibtex = new Label(
                "<i>@INPROCEEDINGS{UNG12,<br/>" +
                "&nbsp;&nbsp;author = {{Christina Unger, Lorenz Bühmann, Jens Lehmann, Axel-Cyrille Ngonga Ngomo, Daniel Gerber and Philipp Cimiano}},<br/>" +
                "&nbsp;&nbsp;title = {SPARQL Template Based Question Answering},<br/>" +
                "&nbsp;&nbsp;booktitle = {Proceedings of the 21st International World Wide Web Conference, WWW2012, Lyon (France), April 16-20, 2012},<br/>" +
                "&nbsp;&nbsp;year = {2012}<br/>"+
                "}</i>"
                );
        questionAnsweringBibtex.setContentMode(Label.CONTENT_XHTML);
        
        publications.addComponent(questionAnswering);
        publications.addComponent(questionAnsweringBibtex);
        
        return publications;
    }

    private Component buildDownloadsPage() {

        VerticalLayout downloads = new VerticalLayout();
        downloads.setSizeFull();
//        downloads.setMargin(true);
        
        HorizontalLayout patternIndexDownloads = new HorizontalLayout();
        
        Label patternIndexLabel = new Label(
                "<b><h1>Download the BOA Pattern Index:</h1></b><br/>" +
                "You can download the Lucene Index of the patterns and pattern mappings displayed in this demo! " +
                "The schema of this index and how to query the BOA Solr instance is explained <a href='http://code.google.com/p/boa/wiki/PatternIndex'> here</a>.");
        patternIndexLabel.setContentMode(Label.CONTENT_XHTML);

        Label enDefaultWikiIndex = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/enwiki-default.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>enwiki-default.tar.gz</span></div>" + 
                "</a>"
                );
        enDefaultWikiIndex.setContentMode(Label.CONTENT_XHTML);
        enDefaultWikiIndex.setWidth("150px");
        
        Label enDetailsWikiIndex = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/enwiki-details.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>enwiki-details.tar.gz</span></div>" + 
                "</a>"
                );
        enDetailsWikiIndex.setContentMode(Label.CONTENT_XHTML);
        enDetailsWikiIndex.setWidth("150px");
        
        Label deDefaultWikiIndex = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/dewiki-default.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>dewiki-default.tar.gz</span></div>" + 
                "</a>"
                );
        deDefaultWikiIndex.setContentMode(Label.CONTENT_XHTML);
        deDefaultWikiIndex.setWidth("150px");
        
        Label deDetailsWikiIndex = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/dewiki-details.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>dewiki-details.tar.gz</span></div>" + 
                "</a>"
                );
        deDetailsWikiIndex.setContentMode(Label.CONTENT_XHTML);
        deDetailsWikiIndex.setWidth("150px");
        
        patternIndexDownloads.addComponent(enDefaultWikiIndex);
        patternIndexDownloads.addComponent(enDetailsWikiIndex);
        patternIndexDownloads.addComponent(deDefaultWikiIndex);
        patternIndexDownloads.addComponent(deDetailsWikiIndex);
        patternIndexDownloads.setSpacing(true);
        patternIndexDownloads.setMargin(true);
        
        downloads.addComponent(patternIndexLabel);
        downloads.addComponent(patternIndexDownloads);
        
        // ####################################################

        Label tripleLabel = new Label(
                "<b><h1>Download the BOA Knowledge:</h1></b><br/>" +
                "You can download all triples BOA has produced in the first iteration or query it in the 'Query' section!");
        tripleLabel.setContentMode(Label.CONTENT_XHTML);
        
        HorizontalLayout tripleDownloads = new HorizontalLayout();
        
        Label enwikiTriple = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/enwiki.nt.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>enwiki.nt.tar.gz</span></div>" + 
                "</a>"
                );
        enwikiTriple.setContentMode(Label.CONTENT_XHTML);
        enwikiTriple.setWidth("150px");
        
        Label dewikiIndex = new Label(
                "<a class=\"v-link\" href=\"http://docs.aksw.org/boa/dewiki.nt.tar.gz\">" +
                "   <div><img width='100px' src=\"VAADIN/themes/boa/icons/tar.png\"/></div>" +
                "   <div class=\"downloadText\"><span>dewiki.nt.tar.gz</span></div>" + 
                "</a>"
                );
        dewikiIndex.setContentMode(Label.CONTENT_XHTML);
        dewikiIndex.setWidth("150px");
        
        tripleDownloads.addComponent(enwikiTriple);
        tripleDownloads.addComponent(dewikiIndex);
        tripleDownloads.setSpacing(true);
        tripleDownloads.setMargin(true);
        
        downloads.addComponent(tripleLabel);
        downloads.addComponent(tripleDownloads);
        
        return downloads;
    }

    private Component buildTriplePage() {

        String urlString = "http://live.dbpedia.org/sparql?" +
                "default-graph-uri=http%3A%2F%2Fboa.aksw.org&" +
                "qtxt=SELECT%20%3Fcompany1Label%20%3Fcompany2Label%20%0A" +
                "WHERE%20%7B%20%0A%20%20%20%20%20%3Fcompany1%20%3Chttp%3A%2F%2Fboa.aksw.org%2Fontology%2Fsubsidiary%3E%20%3Fcompany2%20.%20%0A%20%20%20%20%20%3F" +
                "company1%20rdfs%3Alabel%20%3Fcompany1label%20.%20%0A%20%20%20%20%20%3Fcompany2%20rdfs%3Alabel%20%3Fcompany2Label%20.%20%0A%7D%0ALIMIT%20100%0A";
        
        VerticalLayout triples = new VerticalLayout();
        triples.setSizeFull();
        triples.setMargin(true);
        Label preformattedText = new Label(
                "<b><h1>Query BOA's knowledge base:</h1></b><br/>" +
                "You can query BOA's knowledge base with SPARQL (see <a href='"+urlString+"'>SPARQL Endpoint</a>) or you can download the generated triples in the 'Downloads' sections.");
        preformattedText.setContentMode(Label.CONTENT_XHTML);
        triples.addComponent(preformattedText);
        
        try {
            
            URL url = new URL(urlString);
            Embedded browser = new Embedded("", new ExternalResource(url));
            browser.setType(Embedded.TYPE_BROWSER);
            browser.setSizeFull();
            triples.addComponent(browser);
            triples.setExpandRatio(browser, 2f);
        }
        catch (MalformedURLException e) {
            
            e.printStackTrace();
        }
        
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
        
        horizontalSplitPanel.setSplitPosition(215, HorizontalSplitPanel.UNITS_PIXELS);
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
        buttons.addComponent(triplesButton);
        buttons.addComponent(sourceButton);
        buttons.addComponent(downloadsButton);
        buttons.addComponent(publicationsButton);
        buttons.setComponentAlignment(databasesButton, Alignment.MIDDLE_LEFT);
        buttons.setComponentAlignment(triplesButton, Alignment.MIDDLE_LEFT);
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
    
    private HorizontalSplitPanel buildStartPage() {

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
        
        horizontalSplitPanel.setFirstComponent(tree);
        horizontalSplitPanel.setSecondComponent(p);
        
        return horizontalSplitPanel;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {

        Pattern pattern = (Pattern) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "PATTERN").getValue();
        PatternMapping mapping = (PatternMapping) nlrPatternContainer.getContainerProperty(event.getProperty().toString(), "MAPPING").getValue();
        getMainWindow().addWindow(new PatternWindow(this, pattern, mapping));
    }
}