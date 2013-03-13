package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.text.WordUtils;

import com.github.gerbsen.format.OutputFormatter;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.comparator.FeatureNameComparator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;


public class PatternWindow extends Window {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7585663579160693199L;
	
	private Pattern pattern;
	private PatternMapping patternMapping;
	
	public PatternWindow(BoaFrontendApplication boa, Pattern pattern, PatternMapping pm) {
		super("Details for pattern: \""+pattern.getNaturalLanguageRepresentation()+"\"");
		
		this.pattern = pattern;
		this.patternMapping = pm;
		
		this.setModal(true);
		this.setWidth("1000px");
		this.setHeight("510px");
		this.setResizable(false);
		
        VerticalLayout l2 = new VerticalLayout();
        l2.addComponent(this.buildTab2Content());
        
        VerticalLayout l3 = new VerticalLayout();
        l3.setMargin(true);
        l3.addComponent(new Label(this.buildTab3Content(), Label.CONTENT_XHTML));
        
        VerticalLayout l4 = new VerticalLayout();
        l4.setMargin(true);
        l4.addComponent(new Label(this.buildTab4Content(), Label.CONTENT_XHTML));
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(buildTab1Content(), "General");
		tabSheet.addTab(l2, "Learned from");
		tabSheet.addTab(l3, "Sentences");
		tabSheet.addTab(l4, "Query index");
		
		this.addComponent(tabSheet);
	}
	
	private Layout buildTab1Content(){
	
		HorizontalLayout hLayout = new HorizontalLayout();
		hLayout.setSizeFull();
		
		VerticalLayout leftVerticalLayout = new VerticalLayout();
		leftVerticalLayout.setSizeFull();
		leftVerticalLayout.setMargin(true);

		// score
		HorizontalLayout hLayout2 = new HorizontalLayout();
		Label confLabel = new Label("<b>Score: </b>", Label.CONTENT_XHTML);
		Label confidence = new Label(OutputFormatter.format(this.pattern.getScore(), "##.###"));
		confLabel.setWidth(null);
		confidence.setWidth(null);
		hLayout2.addComponent(confLabel);
		hLayout2.addComponent(confidence);
		hLayout2.setComponentAlignment(confLabel, Alignment.MIDDLE_LEFT);
		hLayout2.setComponentAlignment(confidence, Alignment.MIDDLE_RIGHT);
		hLayout2.setSpacing(true);
		hLayout2.setSizeFull();
		
        leftVerticalLayout.addComponent(hLayout2);
		
        List<Feature> features = new ArrayList<Feature>(this.pattern.getFeatures().keySet());
        Collections.sort(features, new FeatureNameComparator());
        
		for (Feature feature : features) {

		    // similarity
	        HorizontalLayout hLayoutFeature = new HorizontalLayout();
	        Label featureLabel = new Label("<b>" + WordUtils.capitalize(feature.getName().replace("_", " ").toLowerCase()) + ": </b>", Label.CONTENT_XHTML);
	        Label featureValue = new Label(String.valueOf(OutputFormatter.format(pattern.getFeatures().get(feature), "##.###")));
	        featureLabel.setWidth(null);
	        featureValue.setWidth(null);
	        hLayoutFeature.addComponent(featureLabel);
	        hLayoutFeature.addComponent(featureValue);
	        hLayoutFeature.setComponentAlignment(featureLabel, Alignment.MIDDLE_LEFT);
	        hLayoutFeature.setComponentAlignment(featureValue, Alignment.MIDDLE_RIGHT);
	        hLayoutFeature.setExpandRatio(featureLabel, 2.5f);
	        hLayoutFeature.setSpacing(true);
	        hLayoutFeature.setSizeFull();
	        
	        leftVerticalLayout.addComponent(hLayoutFeature);
		}
		
		VerticalLayout rightVerticalLayout = new VerticalLayout();
		rightVerticalLayout.setMargin(true);
		rightVerticalLayout.setSizeFull();
		
		// natural language representation
		HorizontalLayout hLayout12 = new HorizontalLayout();
		Label nlrLabel = new Label("<b>Natural language representation: </b>", Label.CONTENT_XHTML);
		Label nlr = new Label(this.pattern.getNaturalLanguageRepresentation());
		hLayout12.addComponent(nlrLabel);
		hLayout12.addComponent(nlr);
		hLayout12.setComponentAlignment(nlrLabel, Alignment.MIDDLE_LEFT);
		hLayout12.setComponentAlignment(nlr, Alignment.MIDDLE_RIGHT);
		hLayout12.setSpacing(true);
		hLayout12.setSizeFull();

		// generalisation
		HorizontalLayout hLayout13 = new HorizontalLayout();
		Label reverbGeneralisationLabel = new Label("<b>ReVerb generalisation: </b>", Label.CONTENT_XHTML);
		Label reverbGeneralisation = new Label(String.valueOf((this.pattern.getGeneralizedPattern() == null || this.pattern.getGeneralizedPattern().isEmpty()) ? "not available" : this.pattern.getGeneralizedPattern()));
		hLayout13.addComponent(reverbGeneralisationLabel);
		hLayout13.addComponent(reverbGeneralisation);
		hLayout13.setComponentAlignment(reverbGeneralisationLabel, Alignment.MIDDLE_LEFT);
		hLayout13.setComponentAlignment(reverbGeneralisation, Alignment.MIDDLE_RIGHT);
		hLayout13.setSpacing(true);
		hLayout13.setSizeFull();
		
		// pos
		HorizontalLayout hLayout14 = new HorizontalLayout();
		Label posLabel = new Label("<b>Part of speech: </b>", Label.CONTENT_XHTML);
		Label pos = new Label(String.valueOf(this.pattern.getPosTaggedString()));
		hLayout14.addComponent(posLabel);
		hLayout14.addComponent(pos);
		hLayout14.setComponentAlignment(posLabel, Alignment.MIDDLE_LEFT);
		hLayout14.setComponentAlignment(pos, Alignment.MIDDLE_RIGHT);
		hLayout14.setSpacing(true);
		hLayout14.setSizeFull();
		
		HorizontalLayout spacer = new HorizontalLayout();
		Label emptyLabel = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.addComponent(emptyLabel);
		spacer.setSizeFull();
		
		// uri
        HorizontalLayout hLayout141 = new HorizontalLayout();
        Label uriLabel = new Label("<b>Uri: </b>", Label.CONTENT_XHTML);
        Label uri = new Label(String.valueOf(this.patternMapping.getProperty().getUri()));
        hLayout141.addComponent(uriLabel);
        hLayout141.addComponent(uri);
        hLayout141.setComponentAlignment(uriLabel, Alignment.MIDDLE_LEFT);
        hLayout141.setComponentAlignment(uri, Alignment.MIDDLE_RIGHT);
        hLayout141.setSpacing(true);
        hLayout141.setSizeFull();
		
		// domain
		HorizontalLayout hLayout15 = new HorizontalLayout();
		Label domainLabel = new Label("<b>?D? means: </b>", Label.CONTENT_XHTML);
		Label domain = new Label(String.valueOf(this.patternMapping.getProperty().getRdfsDomain()));
		hLayout15.addComponent(domainLabel);
		hLayout15.addComponent(domain);
		hLayout15.setComponentAlignment(domainLabel, Alignment.MIDDLE_LEFT);
		hLayout15.setComponentAlignment(domain, Alignment.MIDDLE_RIGHT);
		hLayout15.setSpacing(true);
		hLayout15.setSizeFull();
		
		// range
		HorizontalLayout hLayout16 = new HorizontalLayout();
		Label rangeLabel = new Label("<b>?R? means: </b>", Label.CONTENT_XHTML);
		Label range = new Label(String.valueOf(this.patternMapping.getProperty().getRdfsRange()));
		hLayout16.addComponent(rangeLabel);
		hLayout16.addComponent(range);
		hLayout16.setComponentAlignment(rangeLabel, Alignment.MIDDLE_LEFT);
		hLayout16.setComponentAlignment(range, Alignment.MIDDLE_RIGHT);
		hLayout16.setSpacing(true);
		hLayout16.setSizeFull();
		
		// humanFeedback
		HorizontalLayout hLayout17 = new HorizontalLayout();
		Label humanFeedbackLabel = new Label("<b>Rate this pattern: </b>", Label.CONTENT_XHTML);
		CheckBox cb = new CheckBox();
        cb.setImmediate(true);
        hLayout17.addComponent(humanFeedbackLabel);
        hLayout17.addComponent(cb);
        hLayout17.setComponentAlignment(humanFeedbackLabel, Alignment.MIDDLE_LEFT);
        hLayout17.setComponentAlignment(cb, Alignment.MIDDLE_LEFT);
        hLayout17.setSpacing(true);
        hLayout17.setSizeFull();
		
		rightVerticalLayout.addComponent(hLayout12);
		rightVerticalLayout.addComponent(hLayout13);
		rightVerticalLayout.addComponent(hLayout14);
		rightVerticalLayout.addComponent(hLayout141);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(hLayout15);
		rightVerticalLayout.addComponent(hLayout16);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(hLayout17);
		
		hLayout.addComponent(leftVerticalLayout);
		hLayout.setComponentAlignment(leftVerticalLayout, Alignment.MIDDLE_LEFT);
		hLayout.addComponent(rightVerticalLayout);
		hLayout.setComponentAlignment(rightVerticalLayout, Alignment.TOP_CENTER);
		hLayout.setExpandRatio(leftVerticalLayout, 2.5f);
		hLayout.setExpandRatio(rightVerticalLayout, 2.5f);
		
		return hLayout;
	}
	
	private Table buildTab2Content(){
		
	    Table table = new Table();
	    table.setSizeFull();
	        
	    /* Define the names and data types of columns.
	     * The "default value" parameter is meaningless here. */
	    table.addContainerProperty("Subject",      String.class,  null);
	    table.addContainerProperty("Object",       String.class,  null);
	    table.addContainerProperty("Occurrence",   Integer.class, null);

		int i = 0, total = 0;
		for (Entry<String, Integer> entry : this.pattern.getLearnedFrom().entrySet() ) {
			
		    String[] parts = entry.getKey().split("-;-");
		    table.addItem(new Object[] {
		            this.pattern.isDomainFirst() ? parts[0] : parts[1], 
		            this.pattern.isDomainFirst() ? parts[1] : parts[0], entry.getValue()}, new Integer(i++));
		    total += entry.getValue();
		}
		
		// Set the footers
		table.setFooterVisible(true);
		table.setColumnFooter("Subject", "Total");
		table.setColumnFooter("Occurrence", String.valueOf(total));
		
		return table;
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildTab3Content(){
	    
	    List<String> sentences = PatternUtil.getLuceneDocuments(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH, new ArrayList<Integer>(this.pattern.getFoundInSentences()));
        
        StringBuilder builder = new StringBuilder();
        builder.append("<h2>Sentences which lead to pattern: \""+ this.pattern.getNaturalLanguageRepresentation()+"\":</h2>");
        
        Iterator<String> iter = sentences.iterator(); 
        int i = 0;
        while ( iter.hasNext() && i++ < 10 ) {

            String sentence = iter.next();
            String sentenceLowerCase = sentence.toLowerCase();
            
            String longestSubject = "";
            String longestObject  = "";
            
            for (String pair : this.pattern.getLearnedFrom().keySet()) {
                
                String[] pairParts = pair.split(java.util.regex.Pattern.quote("-;-"));
                if ( pairParts[0].length() > longestSubject.length() && sentenceLowerCase.contains(pairParts[0])) longestSubject = pairParts[0];
                if ( pairParts[1].length() > longestSubject.length() && sentenceLowerCase.contains(pairParts[1])) longestObject  = pairParts[1];
            }
            
            System.out.println(i + "-normal: " + sentence);
            
            // we need to find the correct string normal case sentence, otherwise we would 
            // replace the normal case entities with lowercase entities
            int subjectStart = sentence.toLowerCase().indexOf(longestSubject);
//            sentence = sentence.replace(
//                    sentence.substring(subjectStart, subjectStart + longestSubject.length()),
//                    "<span style=\"color: #EB1A1A;\">" + sentence.substring(subjectStart, subjectStart + longestSubject.length()) +"</span>");
            
            System.out.println(i + "-subject: " + sentence);
            
            int objectStart  = sentence.toLowerCase().indexOf(longestObject);
//            sentence = sentence.replace(
//                    sentence.substring(objectStart, objectStart + longestObject.length()), 
//                    "<span style=\"color: #EB1A1A;\">" + sentence.substring(objectStart, objectStart + longestObject.length()) +"</span>");
            
            System.out.println(i + "-object: " + sentence);   
            
//            sentence = sentence.replaceAll("(?i)" + this.pattern.getNaturalLanguageRepresentationWithoutVariables(), 
//                    "<span style=\"color: #61A30B;\">"+this.pattern.getNaturalLanguageRepresentationWithoutVariables()+"</span>");
            
            sentence = sentence.replace(this.pattern.getNaturalLanguageRepresentationWithoutVariables(), 
                  "<span style=\"color: #61A30B;\">"+this.pattern.getNaturalLanguageRepresentationWithoutVariables()+"</span>");
            
            System.out.println(i + "-pattern: " + sentence);
            
            builder.append("<b>("+i + ")</b> " + sentence + "<br/>");
            if (iter.hasNext()) builder.append("<hr/>");
        }
        return builder.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	private String buildTab4Content(){
		
		Set<String> sentences = PatternUtil.exactQueryIndex(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH, this.pattern, 10);
	    
		StringBuilder builder = new StringBuilder();
		builder.append("<h2>Search for label \""+ this.pattern.getNaturalLanguageRepresentation()+"\" in the index:</h2>");
		
		Iterator<String> iter = sentences.iterator(); 
		int i = 0;
		while ( iter.hasNext() && i++ < 10 ) {

			String sentence = iter.next();
			sentence = sentence.replaceAll("(?i)" + this.pattern.getNaturalLanguageRepresentationWithoutVariables(), 
			        "<span style=\"color: #61A30B;\">"+this.pattern.getNaturalLanguageRepresentationWithoutVariables()+"</span>");
			
			builder.append("<b>("+i + ")</b> " + sentence + "<br/>");
			if (iter.hasNext()) builder.append("<hr/>");
		}
		return builder.toString();
	}
}
