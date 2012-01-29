package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

import de.danielgerber.format.OutputFormatter;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class PatternTable extends Table {
	
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
		 "score",	    "numberOfOccurrences", "generalizedPattern",	"naturalLanguageRepresentation", "posTaggedString"};

	public static final String[] COL_HEADERS_ENGLISH = new String[] {
		 "SCR",			"OCC",					"GEN",					"NLR", 							 "POS" };
	
	public PatternTable(BoaFrontendApplication app, Container dataSource) {
		
		setSizeFull();
		setContainerDataSource(dataSource);
		
		setVisibleColumns(PatternTable.NATURAL_COL_ORDER);
		setColumnHeaders(PatternTable.COL_HEADERS_ENGLISH);
		
		setSortContainerPropertyId(NATURAL_COL_ORDER[0]);
		setSortAscending(false);
		sort();
		
		setColumnWidth(NATURAL_COL_ORDER[0],60);
		setColumnWidth(NATURAL_COL_ORDER[1],60);
		setColumnWidth(NATURAL_COL_ORDER[2],60);
		
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);

		/*
		 * Make table selectable, react immediatedly to user events, and pass
		 * events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		addListener((ItemClickListener) app);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);
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