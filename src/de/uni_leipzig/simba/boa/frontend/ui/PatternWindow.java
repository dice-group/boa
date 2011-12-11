package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Slider;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;


public class PatternWindow extends Window {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7585663579160693199L;
	
	// Icons for the table
    private static final ThemeResource icon1 = new ThemeResource("icons/32/document.png");
    private static final ThemeResource icon2 = new ThemeResource("icons/32/document-txt.png");
    private static final ThemeResource icon3 = new ThemeResource("icons/32/document-txt.png");
	
	private Pattern pattern;
	private PatternMapping patternMapping;
	
	public PatternWindow(BoaFrontendApplication boa, Pattern pattern, PatternMapping pm) {
		super("Details for pattern: \""+pattern.getNaturalLanguageRepresentation()+"\"");
		
		this.pattern = pattern;
		this.patternMapping = pm;
		
		this.setModal(true);
		this.setWidth("1000px");
//		this.setHeight("700px");	
		this.setResizable(false);
		
        // Tab 2 content
        VerticalLayout l2 = new VerticalLayout();
        l2.setMargin(true);
        l2.addComponent(new Label(this.buildTab2Content(), Label.CONTENT_XHTML));
        // Tab 3 content
        VerticalLayout l3 = new VerticalLayout();
        l3.setMargin(true);
        l3.addComponent(new Label(this.buildTab3Content(), Label.CONTENT_XHTML));
		
		TabSheet tabSheet = new TabSheet();
		tabSheet.addTab(buildTab1Content(), "General", icon1);
		tabSheet.addTab(l2, "Learned from", icon2);
		tabSheet.addTab(l3, "Query index", icon3);
		
		this.addComponent(tabSheet);
	}
	
	private Layout buildTab1Content(){
	
		HorizontalLayout hLayout = new HorizontalLayout();
		
		VerticalLayout leftVerticalLayout = new VerticalLayout();
		leftVerticalLayout.setMargin(true);

		// id
		HorizontalLayout hLayout1 = new HorizontalLayout();
		Label idLabel = new Label("<b>ID: </b>", Label.CONTENT_XHTML);
		Label id = new Label(String.valueOf(this.pattern.getId()));
		hLayout1.addComponent(idLabel);
		hLayout1.addComponent(id);
		hLayout1.setComponentAlignment(idLabel, Alignment.MIDDLE_LEFT);
		hLayout1.setComponentAlignment(id, Alignment.MIDDLE_RIGHT);
		hLayout1.setSpacing(true);
		hLayout1.setWidth("350px");

		// score
		HorizontalLayout hLayout2 = new HorizontalLayout();
		Label confLabel = new Label("<b>Score: </b>", Label.CONTENT_XHTML);
		Label confidence = new Label(OutputFormatter.format(this.pattern.getConfidence(), "##.###"));
		hLayout2.addComponent(confLabel);
		hLayout2.addComponent(confidence);
		hLayout2.setComponentAlignment(confLabel, Alignment.MIDDLE_LEFT);
		hLayout2.setComponentAlignment(confidence, Alignment.MIDDLE_RIGHT);
		hLayout2.setSpacing(true);
		hLayout2.setWidth("350px");

		// similarity
		HorizontalLayout hLayout3 = new HorizontalLayout();
		Label simLabel = new Label("<b>Similarity: </b>", Label.CONTENT_XHTML);
		Label similarity = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.WORDNET_DISTANCE), "##.###")));
		hLayout3.addComponent(simLabel);
		hLayout3.addComponent(similarity);
		hLayout3.setComponentAlignment(simLabel, Alignment.MIDDLE_LEFT);
		hLayout3.setComponentAlignment(similarity, Alignment.MIDDLE_RIGHT);
		hLayout3.setSpacing(true);
		hLayout3.setWidth("350px");
		
		// support
		HorizontalLayout hLayout4 = new HorizontalLayout();
		Label supportLabel = new Label("<b>Support_Max: </b>", Label.CONTENT_XHTML);
		Label support = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM), "##.###")));
		hLayout4.addComponent(supportLabel);
		hLayout4.addComponent(support);
		hLayout4.setComponentAlignment(supportLabel, Alignment.MIDDLE_LEFT);
		hLayout4.setComponentAlignment(support, Alignment.MIDDLE_RIGHT);
		hLayout4.setSpacing(true);
		hLayout4.setWidth("350px");
		
		// support
		HorizontalLayout hLayout41 = new HorizontalLayout();
		Label supportLabel1 = new Label("<b>Support_Pair: </b>", Label.CONTENT_XHTML);
		Label support1 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM), "##.###")));
		hLayout41.addComponent(supportLabel1);
		hLayout41.addComponent(support1);
		hLayout41.setComponentAlignment(supportLabel1, Alignment.MIDDLE_LEFT);
		hLayout41.setComponentAlignment(support1, Alignment.MIDDLE_RIGHT);
		hLayout41.setSpacing(true);
		hLayout41.setWidth("350px");
		
		// support
		HorizontalLayout hLayout42 = new HorizontalLayout();
		Label supportLabel2 = new Label("<b>Support: </b>", Label.CONTENT_XHTML);
		Label support2 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM)*this.pattern.getFeatures().get(Feature.SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM), "##.###")));
		hLayout42.addComponent(supportLabel2);
		hLayout42.addComponent(support2);
		hLayout42.setComponentAlignment(supportLabel2, Alignment.MIDDLE_LEFT);
		hLayout42.setComponentAlignment(support2, Alignment.MIDDLE_RIGHT);
		hLayout42.setSpacing(true);
		hLayout42.setWidth("350px");

		// typicity
		HorizontalLayout hLayout5 = new HorizontalLayout();
		Label typicityLabel = new Label("<b>Typicity-Domain: </b>", Label.CONTENT_XHTML);
		Label typicity = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TYPICITY_CORRECT_DOMAIN_NUMBER), "##.###")));
		hLayout5.addComponent(typicityLabel);
		hLayout5.addComponent(typicity);
		hLayout5.setComponentAlignment(typicityLabel, Alignment.MIDDLE_LEFT);
		hLayout5.setComponentAlignment(typicity, Alignment.MIDDLE_RIGHT);
		hLayout5.setSpacing(true);
		hLayout5.setWidth("350px");
		
		// typicity
		HorizontalLayout hLayout51 = new HorizontalLayout();
		Label typicityLabel1 = new Label("<b>Typicity-Range: </b>", Label.CONTENT_XHTML);
		Label typicity1 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TYPICITY_CORRECT_RANGE_NUMBER), "##.###")));
		hLayout51.addComponent(typicityLabel1);
		hLayout51.addComponent(typicity1);
		hLayout51.setComponentAlignment(typicityLabel1, Alignment.MIDDLE_LEFT);
		hLayout51.setComponentAlignment(typicity1, Alignment.MIDDLE_RIGHT);
		hLayout51.setSpacing(true);
		hLayout51.setWidth("350px");
		
		// typicity
		HorizontalLayout hLayout52 = new HorizontalLayout();
		Label typicityLabel2 = new Label("<b>Typicity-Sentences: </b>", Label.CONTENT_XHTML);
		Label typicity2 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TYPICITY_SENTENCES), "##.###")));
		hLayout52.addComponent(typicityLabel2);
		hLayout52.addComponent(typicity2);
		hLayout52.setComponentAlignment(typicityLabel2, Alignment.MIDDLE_LEFT);
		hLayout52.setComponentAlignment(typicity2, Alignment.MIDDLE_RIGHT);
		hLayout52.setSpacing(true);
		hLayout52.setWidth("350px");
		
		// typicity
		HorizontalLayout hLayout53 = new HorizontalLayout();
		Label typicityLabel3 = new Label("<b>Typicity: </b>", Label.CONTENT_XHTML);
		Label typicity3 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TYPICITY), "##.###")));
		hLayout53.addComponent(typicityLabel3);
		hLayout53.addComponent(typicity3);
		hLayout53.setComponentAlignment(typicityLabel3, Alignment.MIDDLE_LEFT);
		hLayout53.setComponentAlignment(typicity3, Alignment.MIDDLE_RIGHT);
		hLayout53.setSpacing(true);
		hLayout53.setWidth("350px");

		// specificity
		HorizontalLayout hLayout6 = new HorizontalLayout();
		Label specificityLabel = new Label("<b>Specificity: </b>", Label.CONTENT_XHTML);
		Label specificity = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.SPECIFICITY), "##.###")));
		hLayout6.addComponent(specificityLabel);
		hLayout6.addComponent(specificity);
		hLayout6.setComponentAlignment(specificityLabel, Alignment.MIDDLE_LEFT);
		hLayout6.setComponentAlignment(specificity, Alignment.MIDDLE_RIGHT);
		hLayout6.setSpacing(true);
		hLayout6.setWidth("350px");

		// reverb
		HorizontalLayout hLayout7 = new HorizontalLayout();
		Label reverbLabel = new Label("<b>Reverb: </b>", Label.CONTENT_XHTML);
		Label reverb = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.REVERB), "##.###")));
		hLayout7.addComponent(reverbLabel);
		hLayout7.addComponent(reverb);
		hLayout7.setComponentAlignment(reverbLabel, Alignment.MIDDLE_LEFT);
		hLayout7.setComponentAlignment(reverb, Alignment.MIDDLE_RIGHT);
		hLayout7.setSpacing(true);
		hLayout7.setWidth("350px");
		
		// tf-idf
		HorizontalLayout hLayout8 = new HorizontalLayout();
		Label tfIdfLabel = new Label("<b>idf: </b>", Label.CONTENT_XHTML);
		Label tfIdf = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TF_IDF_IDF), "##.###")));
		hLayout8.addComponent(tfIdfLabel);
		hLayout8.addComponent(tfIdf);
		hLayout8.setComponentAlignment(tfIdfLabel, Alignment.MIDDLE_LEFT);
		hLayout8.setComponentAlignment(tfIdf, Alignment.MIDDLE_RIGHT);
		hLayout8.setSpacing(true);
		hLayout8.setWidth("350px");
		
		// tf-idf
		HorizontalLayout hLayout81 = new HorizontalLayout();
		Label tfIdfLabel1 = new Label("<b>tf: </b>", Label.CONTENT_XHTML);
		Label tfIdf1 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TF_IDF_TF), "##.###")));
		hLayout81.addComponent(tfIdfLabel1);
		hLayout81.addComponent(tfIdf1);
		hLayout81.setComponentAlignment(tfIdfLabel1, Alignment.MIDDLE_LEFT);
		hLayout81.setComponentAlignment(tfIdf1, Alignment.MIDDLE_RIGHT);
		hLayout81.setSpacing(true);
		hLayout81.setWidth("350px");
		
		// tf-idf
		HorizontalLayout hLayout82 = new HorizontalLayout();
		Label tfIdfLabel2 = new Label("<b>tf-idf: </b>", Label.CONTENT_XHTML);
		Label tfIdf2 = new Label(String.valueOf(OutputFormatter.format(this.pattern.getFeatures().get(Feature.TF_IDF_TFIDF), "##.###")));
		hLayout82.addComponent(tfIdfLabel2);
		hLayout82.addComponent(tfIdf2);
		hLayout82.setComponentAlignment(tfIdfLabel2, Alignment.MIDDLE_LEFT);
		hLayout82.setComponentAlignment(tfIdf2, Alignment.MIDDLE_RIGHT);
		hLayout82.setSpacing(true);
		hLayout82.setWidth("350px");
		
		// occurrence
		HorizontalLayout hLayout9 = new HorizontalLayout();
		Label occurrenceLabel = new Label("<b>Number of occurrence: </b>", Label.CONTENT_XHTML);
		Label occurrence = new Label(String.valueOf(this.pattern.getNumberOfOccurrences()));
		hLayout9.addComponent(occurrenceLabel);
		hLayout9.addComponent(occurrence);
		hLayout9.setComponentAlignment(occurrenceLabel, Alignment.MIDDLE_LEFT);
		hLayout9.setComponentAlignment(occurrence, Alignment.MIDDLE_RIGHT);
		hLayout9.setSpacing(true);
		hLayout9.setWidth("350px");
		
		// max
		HorizontalLayout hLayout10 = new HorizontalLayout();
		Label maxLabel = new Label("<b>Maximum pair appeared: </b>", Label.CONTENT_XHTML);
		Label max = new Label(String.valueOf((int)(this.pattern.getMaxLearnedFrom())));
		hLayout10.addComponent(maxLabel);
		hLayout10.addComponent(max);
		hLayout10.setComponentAlignment(maxLabel, Alignment.MIDDLE_LEFT);
		hLayout10.setComponentAlignment(max, Alignment.MIDDLE_RIGHT);
		hLayout10.setSpacing(true);
		hLayout10.setWidth("350px");
		
		// pair
		HorizontalLayout hLayout11 = new HorizontalLayout();
		Label pairLabel = new Label("<b>Number of pairs: </b>", Label.CONTENT_XHTML);
		Label pair = new Label(String.valueOf(this.pattern.getLearnedFromPairs()));
		hLayout11.addComponent(pairLabel);
		hLayout11.addComponent(pair);
		hLayout11.setComponentAlignment(pairLabel, Alignment.MIDDLE_LEFT);
		hLayout11.setComponentAlignment(pair, Alignment.MIDDLE_RIGHT);
		hLayout11.setSpacing(true);
		hLayout11.setWidth("350px");
		
		leftVerticalLayout.addComponent(hLayout1);
		leftVerticalLayout.addComponent(hLayout2);
		leftVerticalLayout.addComponent(hLayout3);
		leftVerticalLayout.addComponent(hLayout4);
		leftVerticalLayout.addComponent(hLayout41);
		leftVerticalLayout.addComponent(hLayout42);
		leftVerticalLayout.addComponent(hLayout5);
		leftVerticalLayout.addComponent(hLayout51);
		leftVerticalLayout.addComponent(hLayout52);
		leftVerticalLayout.addComponent(hLayout53);
		leftVerticalLayout.addComponent(hLayout6);
		leftVerticalLayout.addComponent(hLayout7);
		leftVerticalLayout.addComponent(hLayout8);
		leftVerticalLayout.addComponent(hLayout81);
		leftVerticalLayout.addComponent(hLayout82);
		leftVerticalLayout.addComponent(hLayout9);
		leftVerticalLayout.addComponent(hLayout10);
		leftVerticalLayout.addComponent(hLayout11);
		
		VerticalLayout rightVerticalLayout = new VerticalLayout();
		rightVerticalLayout.setMargin(true);
		
		// natural language representation
		HorizontalLayout hLayout12 = new HorizontalLayout();
		Label nlrLabel = new Label("<b>Natural language representation: </b>", Label.CONTENT_XHTML);
		Label nlr = new Label(this.pattern.getNaturalLanguageRepresentation());
		hLayout12.addComponent(nlrLabel);
		hLayout12.addComponent(nlr);
		hLayout12.setComponentAlignment(nlrLabel, Alignment.MIDDLE_LEFT);
		hLayout12.setComponentAlignment(nlr, Alignment.MIDDLE_RIGHT);
		hLayout12.setSpacing(true);
		hLayout12.setWidth("520px");

		// generalisation
		HorizontalLayout hLayout13 = new HorizontalLayout();
		Label reverbGeneralisationLabel = new Label("<b>ReVerb generalisation: </b>", Label.CONTENT_XHTML);
		Label reverbGeneralisation = new Label(String.valueOf((this.pattern.getGeneralizedPattern() == null || this.pattern.getGeneralizedPattern().isEmpty()) ? "not available" : this.pattern.getGeneralizedPattern()));
		hLayout13.addComponent(reverbGeneralisationLabel);
		hLayout13.addComponent(reverbGeneralisation);
		hLayout13.setComponentAlignment(reverbGeneralisationLabel, Alignment.MIDDLE_LEFT);
		hLayout13.setComponentAlignment(reverbGeneralisation, Alignment.MIDDLE_RIGHT);
		hLayout13.setSpacing(true);
		hLayout13.setWidth("520px");
		
		// pos
		HorizontalLayout hLayout14 = new HorizontalLayout();
		Label posLabel = new Label("<b>Part of speech: </b>", Label.CONTENT_XHTML);
		Label pos = new Label(String.valueOf(this.pattern.getPosTaggedString()));
		hLayout14.addComponent(posLabel);
		hLayout14.addComponent(pos);
		hLayout14.setComponentAlignment(posLabel, Alignment.MIDDLE_LEFT);
		hLayout14.setComponentAlignment(pos, Alignment.MIDDLE_RIGHT);
		hLayout14.setSpacing(true);
		hLayout14.setWidth("520px");
		
		HorizontalLayout spacer = new HorizontalLayout();
		Label emptyLabel = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer.addComponent(emptyLabel);
		
		// domain
		HorizontalLayout hLayout15 = new HorizontalLayout();
		Label domainLabel = new Label("<b>?D? means: </b>", Label.CONTENT_XHTML);
		Label domain = new Label(String.valueOf(this.patternMapping.getProperty().getRdfsDomain()));
		hLayout15.addComponent(domainLabel);
		hLayout15.addComponent(domain);
		hLayout15.setComponentAlignment(domainLabel, Alignment.MIDDLE_LEFT);
		hLayout15.setComponentAlignment(domain, Alignment.MIDDLE_RIGHT);
		hLayout15.setSpacing(true);
		hLayout15.setWidth("520px");
		
		// range
		HorizontalLayout hLayout16 = new HorizontalLayout();
		Label rangeLabel = new Label("<b>?R? means: </b>", Label.CONTENT_XHTML);
		Label range = new Label(String.valueOf(this.patternMapping.getProperty().getRdfsRange()));
		hLayout16.addComponent(rangeLabel);
		hLayout16.addComponent(range);
		hLayout16.setComponentAlignment(rangeLabel, Alignment.MIDDLE_LEFT);
		hLayout16.setComponentAlignment(range, Alignment.MIDDLE_RIGHT);
		hLayout16.setSpacing(true);
		hLayout16.setWidth("520px");
		
		// humanFeedback
		HorizontalLayout hLayout17 = new HorizontalLayout();
		Label humanFeedbackLabel = new Label("<b>Rate this pattern: </b>", Label.CONTENT_XHTML);
		Slider slider = new Slider("Select a value between 0 (poor) and 10 (good)!");
        slider.setWidth("100%");
        slider.setMin(0);
        slider.setMax(10);
        slider.setImmediate(true);
        hLayout17.addComponent(humanFeedbackLabel);
        hLayout17.addComponent(slider);
        hLayout17.setComponentAlignment(humanFeedbackLabel, Alignment.MIDDLE_LEFT);
        hLayout17.setComponentAlignment(slider, Alignment.MIDDLE_RIGHT);
        hLayout17.setSpacing(true);
        hLayout17.setWidth("520px");
		
		rightVerticalLayout.addComponent(hLayout12);
		rightVerticalLayout.addComponent(hLayout13);
		rightVerticalLayout.addComponent(hLayout14);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(hLayout15);
		rightVerticalLayout.addComponent(hLayout16);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(spacer);
		rightVerticalLayout.addComponent(hLayout17);
		
		hLayout.addComponent(leftVerticalLayout);
		hLayout.setComponentAlignment(leftVerticalLayout, Alignment.MIDDLE_LEFT);
		hLayout.addComponent(rightVerticalLayout);
		hLayout.setComponentAlignment(rightVerticalLayout, Alignment.TOP_RIGHT);
		
		return hLayout;
	}
	
	private String buildTab2Content(){
		
//		StringBuilder builder = new StringBuilder();
		
		for (Entry<String, Integer> entry : this.pattern.getLearnedFrom().entrySet() ) {
			//TODO
			
		}
		
		String pattern = this.pattern.getNaturalLanguageRepresentationWithoutVariables();
		Iterator<String> iter = PatternUtil.getLuceneDocuments(BoaFrontendApplication.CURRENT_INDEX_DIR, this.pattern.retrieveLuceneDocIdsAsList()).iterator();
		
		StringBuilder builder = new StringBuilder();
		builder.append("<h2>Pattern \"" + this.pattern.getNaturalLanguageRepresentation() + "\" learned from:</h2>");

		int i = 1;
		while ( iter.hasNext() && i < 11 ) {
			
			String sentence = iter.next();
			
			// replace the pattern between the entities
			sentence = sentence.replaceFirst(pattern, "<span style=\"color: red;\">"+pattern+"</span>");
			
			// replace the entities
			for (String learnedPair : this.pattern.getLearnedFrom().keySet() ) {
				
				String[] pair =  learnedPair.split("-;-");
				sentence = sentence.replaceAll(pair[0], "<span style=\"color: green;\">"+pair[0]+"</span>");
				sentence = sentence.replaceAll(pair[1], "<span style=\"color: green;\">"+pair[1]+"</span>");
			}
			builder.append("<b>("+i++ + ")</b> " + sentence + "<br/>");
			if (iter.hasNext()) builder.append("<hr/>");
		}
		return builder.toString();
	}
	
	private String buildTab3Content(){
		
		Set<String> sentences = PatternUtil.exactQueryIndex(BoaFrontendApplication.CURRENT_INDEX_DIR, this.pattern, 10);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<h2>Search for label \""+ this.pattern.getNaturalLanguageRepresentation()+"\" in the index:</h2>");
		
		Iterator<String> iter = sentences.iterator(); 
		int i = 0;
		while ( iter.hasNext() && i++ < 100 ) {

			String sentence = iter.next();
			sentence = sentence.replaceFirst(this.pattern.getNaturalLanguageRepresentationWithoutVariables(), "<span style=\"color: red;\">"+this.pattern.getNaturalLanguageRepresentationWithoutVariables()+"</span>");
			builder.append("<b>("+i + ")</b> " + sentence + "<br/>");
			if (iter.hasNext()) builder.append("<hr/>");
		}
		return builder.toString();
	}
	
	private TreeSet<String> createDummySentences() {

		return new TreeSet<String>(Arrays.asList("Humboldt University of Berlin is the oldest university in Germany's capital city, Berlin.", 
				"As well as all of this, Germany's capital city Berlin is one of the biggest and liveliest in Europe, with the country's politicians mixing with office workers, artists and young people who love to party!"));
	}
}
