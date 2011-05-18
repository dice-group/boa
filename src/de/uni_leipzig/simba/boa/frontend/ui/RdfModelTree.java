package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.data.Item;
import com.vaadin.ui.Tree;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;


@SuppressWarnings("serial")
public class RdfModelTree extends Tree {

	public static final String[] RDF_MODEL_IDS = new String[]{"en_wiki_loc", "en_wiki_per", "en_wiki_org", "en_news_loc", "en_news_per", "en_news_org"};
//	public static final String[] RDF_MODEL_IDS = new String[]{"en_news_loc"};
	
	public RdfModelTree(BoaFrontendApplication app) {
		
		this.addListener(app);
		
		Item item = null;
		
		for (int i = 0; i < RDF_MODEL_IDS.length; i++) {
			
			item = this.addItem(RDF_MODEL_IDS[i]);
			this.setChildrenAllowed(RDF_MODEL_IDS[i], false);
		}
	}
}
