package de.uni_leipzig.simba.boa.frontend.data;

import java.io.Serializable;

import com.vaadin.data.util.BeanItemContainer;

import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;


@SuppressWarnings("serial")
public class PatternContainer extends BeanItemContainer<Pattern> implements Serializable {

	public PatternContainer(PatternMapping pm) throws InstantiationException, IllegalAccessException {
		super(Pattern.class);
		
		if ( pm != null ){
			
			for ( Pattern p : pm.getPatterns()) {
				
				if ( p.isUseForPatternEvaluation() ) {
					//here we can switch between different confidence functions and therewith overwrite the value from the database
//					double confidence = p.getSimilarity() + p.getTypicity() + p.getSupport() + p.getSpecificity(); 
//					p.setConfidence(confidence);
					this.addItem(p);
				}
			}
		}
	}
	
	
	public static BeanItemContainer<Pattern> createTestPatternContainer() throws InstantiationException, IllegalAccessException {
		
		
		Pattern capital1 = new Pattern( "?D? 's capital city ?R?");
		capital1.setId(1);
		capital1.setConfidence(0.96);
//		capital1.setSimilarity(0.77);
//		capital1.setTypicity(22.76);
//		capital1.setSupport(45.12);
//		capital1.setSpecificity(7.33);
//		capital1.setTfIdf(4.33);
		capital1.setNumberOfOccurrences(45);
		capital1.addLearnedFrom("Germany-;-Berlin");
		capital1.addLearnedFrom("France-;-Paris");
		capital1.addLearnedFrom("France-;-Paris");
		capital1.addLearnedFrom("Germany-;-Berlin");
		capital1.addLearnedFrom("Germany-;-Berlin");
		capital1.addLearnedFrom("Germany-;-Berlin");
		capital1.addLearnedFrom("Germany-;-Berlin");
		capital1.addLearnedFrom("England-;-London");
		capital1.addLearnedFrom("England-;-London");
		capital1.addLearnedFrom("Russia-;-Moskow");
		capital1.addLearnedFrom("Russia-;-Moskow");
		capital1.setPosTaggedString("POS NN NN");
		
		BeanItemContainer<Pattern> container = new BeanItemContainer<Pattern>(Pattern.class);
		container.addItem(capital1);
		
		return container;
	}
}