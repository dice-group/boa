package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums;


public enum Language {

	UNSUPPORTED("n/a"),
	ENGLISH("en"),
	GERMAN("de");
	
	private String languageTag;
	
	Language(String languageTag) {
		
		this.languageTag = languageTag;
	}
	
	public static Language getLanguage(String language) {
		
		if (language.equals("de") ) return GERMAN;
		if (language.equals("en") ) return ENGLISH;
		return UNSUPPORTED;
	}
}
