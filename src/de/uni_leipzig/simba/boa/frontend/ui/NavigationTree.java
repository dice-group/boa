package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.ui.Tree;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;

@SuppressWarnings("serial")
public class NavigationTree extends Tree {
	
	public NavigationTree(BoaFrontendApplication app) {
		
		this.setContainerDataSource(new DatabaseContainer());
		
		this.addListener((Property.ValueChangeListener) app);

        // Add actions (context menu)
		this.addActionHandler((Action.Handler) app);

        // Cause valueChange immediately when the user selects
		this.setImmediate(true);
		
		this.expandItem(DatabaseContainer.DATABASE_IDS[0]);
		
		/*
		 * We want items to be selectable but do not want the user to be able to
		 * de-select an item.
		 */
		setSelectable(true);
		setNullSelectionAllowed(false);
	}
}
