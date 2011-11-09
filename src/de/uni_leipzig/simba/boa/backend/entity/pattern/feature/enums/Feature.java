package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums;

public enum Feature {
		
		// IMPORTANT: make sure those are in alphabetcal order, otherwise you get total confusion
		// REVERB is 1 in database, specificity is 2 in db etc.
		REVERB(										IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		SPECIFICITY(								IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM(	IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM(		IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		TF_IDF_IDF(									IsZeroToOneValue.NO, 	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		TF_IDF_TF(									IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		TF_IDF_TFIDF(								IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.NO),
		TYPICITY(									IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.NO),
		TYPICITY_CORRECT_DOMAIN_NUMBER(				IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		TYPICITY_CORRECT_RANGE_NUMBER(				IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		TYPICITY_SENTENCES(							IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		WORDNET_DISTANCE(							IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES);
		
		private IsZeroToOneValue isZeroToOneValue;
		private NormalizeGlobaly needsGlobalNormalization;
		private UseForPatternLearning useForPatternFeatureLearning;
		
		Feature(IsZeroToOneValue isZeroToOneValue, NormalizeGlobaly needsGlobalNormalization, UseForPatternLearning useForPatternFeatureLearning) {
			
			this.isZeroToOneValue = isZeroToOneValue;
			this.needsGlobalNormalization = needsGlobalNormalization;
			this.useForPatternFeatureLearning = useForPatternFeatureLearning;
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
	}