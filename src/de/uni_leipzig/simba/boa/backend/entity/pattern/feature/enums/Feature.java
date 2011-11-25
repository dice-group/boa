package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums;

import java.util.ArrayList;
import java.util.List;

public enum Feature {
		
		// IMPORTANT: make sure those are in alphabetcal order, otherwise you get total confusion
		// REVERB is 1 in database, specificity is 2 in db etc.
		/* 0*/REVERB(									IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH),
		/* 1*/SPECIFICITY(								IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 2*/SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM(	IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 3*/SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM(		IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 4*/TF_IDF_IDF(								IsZeroToOneValue.NO, 	NormalizeGlobaly.NO, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 5*/TF_IDF_TF(								IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 6*/TF_IDF_TFIDF(								IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.NO,  Language.ENGLISH, Language.GERMAN),
		/* 7*/TYPICITY(									IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.NO,  Language.ENGLISH, Language.GERMAN),
		/* 8*/TYPICITY_CORRECT_DOMAIN_NUMBER(			IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/* 9*/TYPICITY_CORRECT_RANGE_NUMBER(			IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/*10*/TYPICITY_SENTENCES(						IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES, Language.ENGLISH, Language.GERMAN),
		/*11*/WORDNET_DISTANCE(							IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES, Language.ENGLISH);
		
		private List<Language> supportedLanguages;
		private IsZeroToOneValue isZeroToOneValue;
		private NormalizeGlobaly needsGlobalNormalization;
		private UseForPatternLearning useForPatternFeatureLearning;
		
		Feature(IsZeroToOneValue isZeroToOneValue, NormalizeGlobaly needsGlobalNormalization, UseForPatternLearning useForPatternFeatureLearning) {
			
			this.isZeroToOneValue = isZeroToOneValue;
			this.needsGlobalNormalization = needsGlobalNormalization;
			this.useForPatternFeatureLearning = useForPatternFeatureLearning;
			this.supportedLanguages = new ArrayList<Language>();
		}
		
		Feature(IsZeroToOneValue isZeroToOneValue, NormalizeGlobaly needsGlobalNormalization, UseForPatternLearning useForPatternFeatureLearning, Language ... supportedLanguages) {
			
			this.isZeroToOneValue = isZeroToOneValue;
			this.needsGlobalNormalization = needsGlobalNormalization;
			this.useForPatternFeatureLearning = useForPatternFeatureLearning;
			this.supportedLanguages = new ArrayList<Language>();
			for ( Language sl : supportedLanguages) this.supportedLanguages.add(sl);
		}
		
		public boolean isZeroToOneValue(){
			
			return this.isZeroToOneValue == IsZeroToOneValue.YES ? true : false;
		}
		
		public boolean needsGlobalNormalization() {
			
			return this.needsGlobalNormalization == NormalizeGlobaly.YES ? true : false;
		}
		
		public boolean useForPatternFeatureLearning() {
			
			return this.useForPatternFeatureLearning == UseForPatternLearning.YES ? true : false;
		}
		
		public List<Language> getSupportedLanguages(){
			
			return this.supportedLanguages;
		}
	}