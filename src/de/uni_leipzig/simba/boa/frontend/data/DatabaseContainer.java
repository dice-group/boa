package de.uni_leipzig.simba.boa.frontend.data;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


@SuppressWarnings("serial")
public class DatabaseContainer extends HierarchicalContainer{

	public static final Object DATABASE_PROPERTY_NAME = "database_name";
	public static final Object URI_NAME = "database_name";
	
	public static final String[] DATABASE_IDS = new String[]{"en_wiki_per_test","en_wiki_loc", "en_wiki_per", "en_wiki_org", "en_news_loc", "en_news_per", "en_news_org"};
	
	public DatabaseContainer() {
		
		Item item = null;
		
		for (int i = 0; i < DATABASE_IDS.length; i++) {
			
			item = this.addItem(DATABASE_IDS[i]);
			this.setChildrenAllowed(DATABASE_IDS[i], true);
			
			List<String> uris = getUrisForDatabases(DATABASE_IDS[i]);
			
			for (int j = 1; j < uris.size(); j++) {
				
				String uriId = DATABASE_IDS[i] + ":" + uris.get(j);
				item = this.addItem(uriId);
				this.setParent(uriId, DATABASE_IDS[i]);
				this.setChildrenAllowed(uriId, false);
			}
		}
	}
	
	private List<String> getUrisForDatabases(String databaseId) {
		
		HibernateFactory.changeConnection(databaseId);
		PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		List<String> uris = new ArrayList<String>();
		for (PatternMapping pm : pmDao.findPatternMappingsWithoutPattern(null)) {
			uris.add(pm.getUri());
		}
		return uris;
	}
}
