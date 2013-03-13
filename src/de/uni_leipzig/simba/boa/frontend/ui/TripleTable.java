package de.uni_leipzig.simba.boa.frontend.ui;

import com.github.gerbsen.format.OutputFormatter;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class TripleTable extends Table {
	
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
		 "id",	"subject.label",	"property.label"	,"object.label",	"confidence"};

	public static final String[] COL_HEADERS_ENGLISH = new String[] {
		 "id",	"Subject",	"Predicate"	,"Object",	"Score" };
	
	public TripleTable(BoaFrontendApplication app, String uri) {

//		BeanItemContainer<Triple> container = new BeanItemContainer<Triple>(Triple.class);
//		container.addNestedContainerProperty("subject.label");
//		container.addNestedContainerProperty("property.label");
//		container.addNestedContainerProperty("object.label");
//		
//		TripleDao tripleDao = (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
//		
//		for ( Triple triple : tripleDao.findNewTriplesForUri(uri)) {
//			
//			container.addBean(triple);
//			System.out.println(triple);
//		} 
		
//		System.out.println(tripleDao.findNewTriplesForUri(uri).size());
		
		setSizeFull();
//		setContainerDataSource(container);
		
		setVisibleColumns(TripleTable.NATURAL_COL_ORDER);
		setColumnHeaders(TripleTable.COL_HEADERS_ENGLISH);
		
		setSortContainerPropertyId(NATURAL_COL_ORDER[1]);
		setSortAscending(false);
		sort();
		
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
	        	
	        	return OutputFormatter.format((Double) property.getValue(), "#.##");
	        }

	        return super.formatPropertyValue(rowId, colId, property);
		}
		catch (IllegalArgumentException iae) {
			
			return "-1";
		}
    }
}