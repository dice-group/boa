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
				
				if ( /*p.isUseForPatternEvaluation() &&*/ p.getNumberOfOccurrences() >= 3 && p.getConfidenceForIteration(1) > 0 ) {
					
					this.addItem(p);
				}
			}
		}
	}
}
