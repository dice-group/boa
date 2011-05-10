package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.data.Container;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class CorporaTable extends Table {
	
	/**
	 * Natural property order for Person bean. Used in tables and forms.
	 */
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
		"confidence", "naturalLanguageRepresentation", "numberOfOccurrences"};

	/**
	 * "Human readable" captions for properties in same order as in
	 * NATURAL_COL_ORDER.
	 */
	public static final String[] COL_HEADERS_ENGLISH = new String[] {
		"confidence", "naturalLanguageRepresentation", "numberOfOccurrences" };
	
	public CorporaTable(BoaFrontendApplication app, Container dataSource) {
		
		setPageLength(Math.max(15, dataSource.size()));
		setSizeFull();
		setContainerDataSource(dataSource);

		setVisibleColumns(CorporaTable.NATURAL_COL_ORDER);
		setColumnHeaders(CorporaTable.COL_HEADERS_ENGLISH);
		
		setColumnWidth(NATURAL_COL_ORDER[1],500);
		sort(NATURAL_COL_ORDER, new boolean[]{false, true, true});
		
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);

		/*
		 * Make table selectable, react immediatedly to user events, and pass
		 * events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		addListener((ValueChangeListener) app);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);
	}
}