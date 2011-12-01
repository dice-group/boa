package de.uni_leipzig.simba.boa.frontend.data;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


@SuppressWarnings("serial")
public class DatabaseContainer extends HierarchicalContainer{

	public static final Object DATABASE_ID			= "database_name";
	public static final Object URI					= "uri";
	public static final Object DISPLAY_NAME			= "name";
	public static final Object PATTERN_MAPPING_ID	= "pm_id";
	
	public static final String[] DATABASE_IDS = new String[]{"en_wiki_exp","de_wiki_exp","en_news_exp","de_news_exp"};//NLPediaSettings.getInstance().getSetting("frontend.databases").split(",");
	
	private PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	
	public DatabaseContainer() {
		
		Item item = null;
		
		this.addContainerProperty(URI, String.class, null);
		this.addContainerProperty(DATABASE_ID, String.class, null);
		this.addContainerProperty(DISPLAY_NAME, String.class, null);
		this.addContainerProperty(PATTERN_MAPPING_ID, String.class, null);
		
		for (String database : DATABASE_IDS) {

			HibernateFactory.changeConnection(database);
			
			// the database has the URIs as children
			item = this.addItem(database);
			item.getItemProperty(DISPLAY_NAME).setValue(database);
			this.setChildrenAllowed(database, true);
			
//			System.out.println(pmDao.findPatternMappingsWithPatterns()); 
			
			for (String uri : pmDao.findPatternMappingsWithPatterns() ) {
				
				String itemID = database + ":" + uri;
				
				item = this.addItem(itemID);
				item.getItemProperty(DISPLAY_NAME).setValue(uri.replace("http://dbpedia.org/ontology/", "dbpedia-owl:"));
				item.getItemProperty(URI).setValue(uri);
				item.getItemProperty(DATABASE_ID).setValue(database);
//				item.getItemProperty(PATTERN_MAPPING_ID).setValue(mapping.getId());
				
				// add the parent of the newly added item (URI) and prohibit children
				this.setParent(itemID, database);
				this.setChildrenAllowed(itemID, false);
			}
		}
	}
	
	public static HierarchicalContainer getTestDatabaseContainer() {
		
		HierarchicalContainer container = new HierarchicalContainer(){};
		
		Item item = null;
		
		container.addContainerProperty(URI, String.class, null);
		container.addContainerProperty(DATABASE_ID, String.class, null);
		container.addContainerProperty(DISPLAY_NAME, String.class, null);
		
		for (String database : DATABASE_IDS) {

			HibernateFactory.changeConnection(database);
			
			// the database has the URIs as children
			item = container.addItem(database);
			item.getItemProperty(DatabaseContainer.DISPLAY_NAME).setValue(database);
			container.setChildrenAllowed(database, true);
			
			String[] patternMappings = new String[]{"http://dbpedia.org/ontology/capital"};
			
			for (String uri : patternMappings) {
				
				String itemID = database + ":" + uri;
				
				item = container.addItem(itemID);
				item.getItemProperty(DISPLAY_NAME).setValue(uri.replace("http://dbpedia.org/ontology/", "dbpedia-owl:"));
				item.getItemProperty(URI).setValue(uri);
				item.getItemProperty(DATABASE_ID).setValue(database);
				
				// add the parent of the newly added item (URI) and prohibit children
				container.setParent(itemID, database);
				container.setChildrenAllowed(itemID, false);
			}
		}
		
		return container;
	}
	
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
