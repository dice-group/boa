package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.PatternContainer;


@SuppressWarnings("serial")
public class PatternPanel extends Panel {
	
	public PatternPanel(BoaFrontendApplication boaFrontendApplication, String database, String uri) {
		
		PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		PatternMapping pm = pmDao.findPatternMappingsWithoutPattern(uri).get(0);
		pm = pmDao.findPatternMapping(pm.getId());
		
		GridLayout gridLayout = new GridLayout(4,2);
		gridLayout.setSpacing(true);
		gridLayout.setMargin(false);
		gridLayout.setSizeFull();
		gridLayout.addComponent(new Label("rdfs:domain:"), 0, 0);
		gridLayout.addComponent(new Link(pm.getRdfsDomain(), new ExternalResource(pm.getRdfsDomain())), 1, 0);
		gridLayout.addComponent(new Label("rdfs:range:"), 2, 0);
		gridLayout.addComponent(new Link(pm.getRdfsRange(), new ExternalResource(pm.getRdfsRange())), 3, 0);
		
		this.setSizeFull();
		
		try {
			
			CorporaTable table = new CorporaTable(boaFrontendApplication, new PatternContainer(pm));
			gridLayout.addComponent(table, 0, 1, 3, 1);
		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.addComponent(gridLayout);
	}
}
