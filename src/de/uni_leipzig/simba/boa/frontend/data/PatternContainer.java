package de.uni_leipzig.simba.boa.frontend.data;

import java.io.Serializable;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

import com.vaadin.data.util.BeanItemContainer;


@SuppressWarnings("serial")
public class PatternContainer extends BeanItemContainer<Pattern> implements Serializable {

	public PatternContainer(PatternMapping pm) throws InstantiationException, IllegalAccessException {
		super(Pattern.class);
		
		if ( pm != null ){
			
			for ( Pattern p : pm.getPatterns()) {
				
				if ( /*p.isUseForPatternEvaluation() &&*/ p.getNumberOfOccurrences() >= 3 ) {
					
					if ( p.getSpecificity() > 0 || p.getSupport() > 0 || p.getTypicity() > 0 ) {
						
						this.addItem(p);
					}
				}
			}
		}
	}
	
	public static PatternContainer createWithTestData() {
		
		PatternContainer pc = null;
		try {
			
			pc = new PatternContainer(null);
			
			Pattern pattern1 = new Pattern();
			pattern1.setConfidence(0.5);
			pattern1.setNaturalLanguageRepresentation("x is a y");
			
			Pattern pattern2 = new Pattern();
			pattern2.setConfidence(0.3);
			pattern2.setNaturalLanguageRepresentation("x is not a y");
			
			pc.addItem(pattern1);
			pc.addItem(pattern2);
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pc;
	}
}
