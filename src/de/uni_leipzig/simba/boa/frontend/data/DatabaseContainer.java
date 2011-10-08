package de.uni_leipzig.simba.boa.frontend.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.PropertyDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;


@SuppressWarnings("serial")
public class DatabaseContainer extends HierarchicalContainer{

	public static final Object DATABASE_ID	= "database_name";
	public static final Object URI			= "uri";
	public static final Object DISPLAY_NAME	= "name";
	
	public static final String[] DATABASE_IDS = new String[]{"en_wiki_exp","en_news_exp"};//NLPediaSettings.getInstance().getSetting("frontend.databases").split(",");
	
	private PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	
	public DatabaseContainer() {
		
		Item item = null;
		
		this.addContainerProperty(URI, String.class, null);
		this.addContainerProperty(DATABASE_ID, String.class, null);
		this.addContainerProperty(DISPLAY_NAME, String.class, null);
		
		for (String database : DATABASE_IDS) {

			HibernateFactory.changeConnection(database);
			
			// the database has the URIs as children
			item = this.addItem(database);
			item.getItemProperty(DISPLAY_NAME).setValue(database);
			this.setChildrenAllowed(database, true);
			
			for (String uri : pmDao.findPatternMappingsWithPatterns()) {
				
				String itemID = database + ":" + uri;
				
				item = this.addItem(itemID);
				item.getItemProperty(DISPLAY_NAME).setValue(uri.replace("http://dbpedia.org/ontology/", "dbpedia-owl:"));
				item.getItemProperty(URI).setValue(uri);
				item.getItemProperty(DATABASE_ID).setValue(database);
				
				// add the parent of the newly added item (URI) and prohibit children
				this.setParent(itemID, database);
				this.setChildrenAllowed(itemID, false);
			}
		}
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
