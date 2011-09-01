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

	public static final Object DATABASE_PROPERTY_NAME = "database_name";
	public static final Object URI_NAME = "database_name";
	
	public static final String[] DATABASE_IDS = NLPediaSettings.getInstance().getSetting("frontend.databases").split(",");
	
	public DatabaseContainer() {
		
		Item item = null;
		
		PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		for (int i = 0; i < DATABASE_IDS.length; i++) {

			HibernateFactory.changeConnection(DATABASE_IDS[i]);
			
			item = this.addItem(DATABASE_IDS[i]);
			this.setChildrenAllowed(DATABASE_IDS[i], true);
			List<String> uris = pmDao.findPatternMappingsWithPatterns();// getUrisForDatabases(DATABASE_IDS[i]);
			Iterator<String> urisIterator = uris.iterator();
			for (int j = 0; j < uris.size(); j++) {
				
				String uriId = DATABASE_IDS[i] + ":" + urisIterator.next();
				System.out.println(uriId);
				item = this.addItem(uriId);
				this.setParent(uriId, DATABASE_IDS[i]);
				this.setChildrenAllowed(uriId, false);
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
