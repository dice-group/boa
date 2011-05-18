package de.uni_leipzig.simba.boa.frontend.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class PatternTable extends Table {
	
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
		 "withoutLogConfidence", "withLogConfidence", "naturalLanguageRepresentation", "numberOfOccurrences"};

	public static final String[] COL_HEADERS_ENGLISH = new String[] {
		 "Conf", "LogConf", "naturalLanguageRepresentation", "#" };
	
	public PatternTable(BoaFrontendApplication app, Container dataSource) {
		
		setPageLength(Math.max(15, dataSource.size()));
		setSizeFull();
		setContainerDataSource(dataSource);
		
		setVisibleColumns(PatternTable.NATURAL_COL_ORDER);
		setColumnHeaders(PatternTable.COL_HEADERS_ENGLISH);
		
		setColumnWidth(NATURAL_COL_ORDER[0],50);
		setColumnWidth(NATURAL_COL_ORDER[1],100);
		setColumnWidth(NATURAL_COL_ORDER[2],100);
		setColumnWidth(NATURAL_COL_ORDER[4],50);
		sort(NATURAL_COL_ORDER, new boolean[]{true,false,false, true, true});
		
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
        // Format by property type
        if (property.getType() == Double.class) {
        	
        	DecimalFormat df = new DecimalFormat("#.###");
            return df.format((Double)property.getValue());
        }

        return super.formatPropertyValue(rowId, colId, property);
    }
}