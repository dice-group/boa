package de.uni_leipzig.simba.boa.frontend.ui;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.PatternMappingContainer;


@SuppressWarnings("serial")
public class PatternMappingPanel extends Panel {

	public PatternMappingPanel(BoaFrontendApplication boa, String databaseName) {
		
		PatternMappingContainer patternMappingContainer = this.getPatternMappingContainer(databaseName);
		
		NativeSelect uriDropDown = new NativeSelect();
		uriDropDown.addListener(boa);
		for ( PatternMapping mapping : patternMappingContainer.getItemIds() ) {
			
			uriDropDown.addItem(mapping.getUri());
		}
		
		
	}

	private PatternMappingContainer getPatternMappingContainer(String databaseName) {

		return PatternMappingContainer.createWithTestData();
	}
}
