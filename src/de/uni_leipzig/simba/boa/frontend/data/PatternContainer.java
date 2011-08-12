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
				
				if ( p.getNumberOfOccurrences() >= 3 && p.getConfidenceForIteration(1) > 0 ) {
					
					// here we can switch between different confidence functions and therewith overwrite the value from the database
					double confidence = p.getSimilarity() + p.getTypicity() + p.getSupport() + p.getSpecificity(); 
					p.setConfidence(confidence);
					
					this.addItem(p);
				}
			}
		}
	}
}
