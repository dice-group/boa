package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums;

public enum Feature {
		
		// IMPORTANT: make sure those are in alphabetcal order, otherwise you get total confusion
		// REVERB is 1 in database, specificity is 2 in db etc.
		/* 0*/REVERB(									IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		/* 1*/SPECIFICITY(								IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		/* 2*/SUPPORT_NUMBER_OF_MAX_PAIRS_LEARNED_FROM(	IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		/* 3*/SUPPORT_NUMBER_OF_PAIRS_LEARNED_FROM(		IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		/* 4*/TF_IDF_IDF(								IsZeroToOneValue.NO, 	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		/* 5*/TF_IDF_TF(								IsZeroToOneValue.NO, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		/* 6*/TF_IDF_TFIDF(								IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.NO),
		/* 7*/TYPICITY(									IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.NO),
		/* 8*/TYPICITY_CORRECT_DOMAIN_NUMBER(			IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		/* 9*/TYPICITY_CORRECT_RANGE_NUMBER(			IsZeroToOneValue.YES,	NormalizeGlobaly.YES, 	UseForPatternLearning.YES),
		/*10*/TYPICITY_SENTENCES(						IsZeroToOneValue.NO,	NormalizeGlobaly.NO, 	UseForPatternLearning.YES),
		/*11*/WORDNET_DISTANCE(							IsZeroToOneValue.YES, 	NormalizeGlobaly.YES, 	UseForPatternLearning.YES);
		
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