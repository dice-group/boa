package de.uni_leipzig.simba.boa.frontend.data;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;


@SuppressWarnings("serial")
public class DatabaseContainer extends HierarchicalContainer{

	public static final Object DATABASE_ID			  = "database_name";
	public static final Object URI					  = "uri";
	public static final Object DISPLAY_NAME           = "database-name";
	public static final Object SORT_STRING            = "sort-property";
	public static final Object PATTERN_MAPPING_ID	  = "pm_id";
	
	public DatabaseContainer(Map<String, Set<PatternMapping>> databases) {
		
		Item item = null;
		
		this.addContainerProperty(URI, String.class, null);
		this.addContainerProperty(DATABASE_ID, String.class, null);
		this.addContainerProperty(DISPLAY_NAME, String.class, null);
		this.addContainerProperty(PATTERN_MAPPING_ID, String.class, null);
		
		for ( String database : databases.keySet() ) {
		    
		    // the database has the URIs as children
            item = this.addItem(database);
            item.getItemProperty(DISPLAY_NAME).setValue(database.substring(database.lastIndexOf("/") + 1));
            this.setChildrenAllowed(database, true);

            for ( PatternMapping mapping : databases.get(database) ) {
				
				String itemID = database + ":" + mapping.getProperty().getUri();
				
				item = this.addItem(itemID);
				item.getItemProperty(DISPLAY_NAME).setValue(mapping.getProperty().getUri().replace("http://dbpedia.org/ontology/", "dbpedia-owl:"));
				item.getItemProperty(URI).setValue(mapping.getProperty().getUri());
				item.getItemProperty(DATABASE_ID).setValue(database + "/patternmappings/");
//				item.getItemProperty(PATTERN_MAPPING_ID).setValue(mapping.getId());
				
				// add the parent of the newly added item (URI) and prohibit children
				this.setParent(itemID, database);
				this.setChildrenAllowed(itemID, false);
			}
		}
		// sort the tree from a to z
		this.sort(new Object[]{ SORT_STRING }, new boolean[]{ true });
	}
	
//	public static HierarchicalContainer getTestDatabaseContainer() {
//		
//		HierarchicalContainer container = new HierarchicalContainer(){};
//		
//		Item item = null;
//		
//		container.addContainerProperty(URI, String.class, null);
//		container.addContainerProperty(DATABASE_ID, String.class, null);
//		container.addContainerProperty(DATABASE_DISPLAY_NAME, String.class, null);
//		
//		for (String database : DATABASE_IDS) {
//
////			HibernateFactory.changeConnection(database);
//			
//			// the database has the URIs as children
//			item = container.addItem(database);
//			item.getItemProperty(DatabaseContainer.DATABASE_DISPLAY_NAME).setValue(database);
//			container.setChildrenAllowed(database, true);
//			
//			String[] patternMappings = new String[]{"http://dbpedia.org/ontology/capital"};
//			
//			for (String uri : patternMappings) {
//				
//				String itemID = database + ":" + uri;
//				
//				item = container.addItem(itemID);
//				item.getItemProperty(DATABASE_DISPLAY_NAME).setValue(uri.replace("http://dbpedia.org/ontology/", "dbpedia-owl:"));
//				item.getItemProperty(URI).setValue(uri);
//				item.getItemProperty(DATABASE_ID).setValue(database);
//				
//				// add the parent of the newly added item (URI) and prohibit children
//				container.setParent(itemID, database);
//				container.setChildrenAllowed(itemID, false);
//			}
//		}
//		
//		return container;
//	}
	
//	private Set<String> getUrisForDatabases(String databaseId) {
//		
//		HibernateFactory.changeConnection(databaseId);
////		PropertyDao propertyDao = (PropertyDao) DaoFactory.getInstance().createDAO(PropertyDao.class);
//		
//
//		Set<String> patternMappings = new HashSet<String>();
//		for (PatternMapping mapping : pmDao.findPatternMappingsWithPatterns() ) { //propertyDao.findAllProperties() ) {
//			
//			System.out.println(mapping.getId() + " " + mapping);
//			uris.add(mapping.getProperty().getUri());
//		}
//		return uris;
//	}
}
