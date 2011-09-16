package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.Iterator;

import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Tree;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;

@SuppressWarnings("serial")
public class DatabaseNavigationTree extends Tree {
	
	public DatabaseNavigationTree(BoaFrontendApplication app) {
		
		this.setContainerDataSource(new DatabaseContainer());
		
		this.addListener((ItemClickListener) app);

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
	
	public String getFirstUri(){
		
		Iterator i = this.items.getItemIds().iterator();
		while (i.hasNext() ) {
			
			String uri = (String) i.next();
			if (uri.contains(":") ) return uri.substring(uri.indexOf(":") + 1 );
		}
		return "NOTHING";
	}
}
