package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums;

import java.io.Serializable;


public enum Language implements Serializable {

	UNSUPPORTED("n/a"),
	ENGLISH("en"),
	GERMAN("de"),
	KOREAN("kr");
	
	private String languageTag;
	
	Language(String languageTag) {
		
		this.languageTag = languageTag;
	}
	
	public static Language getLanguage(String language) {
		
		if (language.equals("de") ) return GERMAN;
		if (language.equals("en") ) return ENGLISH;
		if (language.equals("kr") ) return KOREAN;
		return UNSUPPORTED;
	}
}
