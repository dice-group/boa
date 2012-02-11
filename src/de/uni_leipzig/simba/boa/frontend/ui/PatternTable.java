package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class PatternTable extends Table {
    
    private Map<Integer,Pattern> patterns = new HashMap<Integer,Pattern>();
	
	public PatternTable(BoaFrontendApplication app, List<Pattern> patterns) {
		setSizeFull();
		
		setSortAscending(false);
		sort();
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);
		
		this.addContainerProperty("SCORE",        String.class, null);
		this.addContainerProperty("OCCURRENCE",   Integer.class, null);
		this.addContainerProperty("GENERALIZED",  String.class, null);
		this.addContainerProperty("NLR",          String.class, null);
		this.addContainerProperty("POS",          String.class, null);
		for (Map.Entry<Feature,Double> feature : patterns.get(0).getFeatures().entrySet()) {
		    
		    this.addContainerProperty(feature.getKey().getName(), Double.class, null);
		    this.setColumnCollapsed(feature.getKey().getName(), true);
		}
		
		int i = 0;
        for (Pattern pattern : patterns) {
            
            List<Object> entries = new ArrayList<Object>();
            entries.add(pattern.getScore());
            entries.add(pattern.getNumberOfOccurrences());
            entries.add(pattern.getGeneralizedPattern());
            entries.add(pattern.getNaturalLanguageRepresentation());
            entries.add(pattern.getPosTaggedString());
            for (Map.Entry<Feature,Double> feature : pattern.getFeatures().entrySet()) {
                
                entries.add(feature.getValue());
            }
            this.patterns.put(new Integer(i), pattern);
            this.addItem(entries.toArray(), i++);
        }
        
		setSelectable(true);
		setImmediate(true);
		addListener((ItemClickListener) app);
		setNullSelectionAllowed(false);
	}
	
	public Pattern getSelectedPattern(Integer index) {
	    
	    return this.patterns.get(index);
	}
	
	@Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
        
		try {
			
			// Format by property type
	        if (property.getType() == Double.class) {
	        	
	        	return OutputFormatter.format((Double) property.getValue(), "####.##");
	        }

	        return super.formatPropertyValue(rowId, colId, property);
		}
		catch (IllegalArgumentException iae) {
			
			return "-1";
		}
    }
}