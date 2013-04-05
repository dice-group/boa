package de.uni_leipzig.simba.boa.frontend.ui;

import java.util.Map;
import java.util.Set;

import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Tree;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.DatabaseContainer;

@SuppressWarnings("serial")
public class DatabaseNavigationTree extends Tree {
	
	public DatabaseNavigationTree(BoaFrontendApplication app, Map<String, Set<PatternMapping>> databases) {
		
		this.setContainerDataSource(new DatabaseContainer(databases));
//		this.setContainerDataSource(DatabaseContainer.getTestDatabaseContainer());
		
		this.addListener((ItemClickListener) app);

        // Add actions (context menu)
//		this.addActionHandler((Action.Handler) app);

        // Cause valueChange immediately when the user selects
		this.setImmediate(true);
		
		this.expandItem(NLPediaSettings.getSetting("patternMappingDatabases").split(";")[0]);

		// Set tree to show the 'uri_name' property as caption for items
        this.setItemCaptionPropertyId(DatabaseContainer.DISPLAY_NAME);
        this.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		
		/*
		 * We want items to be selectable but do not want the user to be able to
		 * de-select an item.
		 */
		setSelectable(true);
		setNullSelectionAllowed(false);
//		setSizeFull();
	}
	
//	public String getFirstUri(){
//		
//		Iterator i = this.items.getItemIds().iterator();
//		while (i.hasNext() ) {
//			
//			String uri = (String) i.next();
//			if (uri.contains(":") ) return uri.substring(uri.indexOf(":") + 1 );
//		}
//		return "NOTHING";
//	}
}
