package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.comparator.FeatureNameComparator;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.impl.Feature;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class PatternTable extends Table {
    
    private Map<Integer,Pattern> patterns = new HashMap<Integer,Pattern>();
	
	public PatternTable(BoaFrontendApplication app, List<Pattern> patterns) {
		setSizeFull();
		setColumnCollapsingAllowed(true);
		
		List<Feature> featureList = new ArrayList<Feature>(patterns.get(0).getFeatures().keySet());
		Collections.sort(featureList, new FeatureNameComparator());
		
		this.addContainerProperty("Score",                            Double.class, null);
		this.addContainerProperty("Occurrence",                       Integer.class, null);
		this.addContainerProperty("Natural Language Representation",  String.class, null);
		this.addContainerProperty("Generalized",                      String.class, null);
		this.addContainerProperty("Part Of Speech",                   String.class, null);
		for ( Feature feature : featureList ) {
		    
		    String name = WordUtils.capitalize(feature.getName().replace("_", " ").toLowerCase());
		    
		    this.addContainerProperty(name, Double.class, null);
		    this.setColumnCollapsed(name, true);
		}
		
		int i = 0;
        for (Pattern pattern : patterns) {
            
            List<Object> entries = new ArrayList<Object>();
            entries.add(pattern.getScore());
            entries.add(pattern.getNumberOfOccurrences());
            entries.add(pattern.getNaturalLanguageRepresentation());
            entries.add(pattern.getGeneralizedPattern());
            entries.add(pattern.getPosTaggedString());
            
            for (Feature feature : featureList) entries.add(pattern.getFeatures().get(feature));
            this.patterns.put(new Integer(i), pattern);
            this.addItem(entries.toArray(), i++);
        }
        
        setSortContainerPropertyId("SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM");
        setSortAscending(false);
        sort();
        setColumnReorderingAllowed(true);
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