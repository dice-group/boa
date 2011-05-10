package de.uni_leipzig.simba.boa.frontend.ui;

import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;

import com.vaadin.ui.NativeSelect;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;
import de.uni_leipzig.simba.boa.frontend.data.PatternMappingContainer;

@SuppressWarnings("serial")
public class UriNativeSelect extends NativeSelect {

	public UriNativeSelect(BoaFrontendApplication app, PatternMappingContainer mappings) {
		
		for ( PatternMapping mapping : mappings.getItemIds() ) {
			
			this.addItem(mapping.getUri());
		}
	}
}
