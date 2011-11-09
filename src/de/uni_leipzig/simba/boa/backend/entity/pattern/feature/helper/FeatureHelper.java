package de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.enums.Feature;

public class FeatureHelper {
	
	private static PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);

	public static Double calculateLocalMaximum(PatternMapping mapping, Feature feature){
		
		Double maximum = 0D;
		for ( Pattern pattern : mapping.getPatterns() ) {
			
			maximum = Math.max(maximum, pattern.getFeatures().get(feature));
		}
		return maximum;
	}
	
	public static Double calculateGlobalMaximum(Feature feature){
		
		Double maximum = 0D;
		for ( PatternMapping mapping : pmDao.findAllPatternMappings() ) {
			
			maximum = Math.max(maximum, calculateLocalMaximum(mapping, feature));
		}
		return maximum;
	}
	
	public static String createNetworkTrainingFileLine(PatternMapping mapping, Pattern pattern) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(pattern.buildFeatureString(mapping));// adds all configured values to the rest of the line
		builder.append("MANUAL\t"); // thats where the manual annotation takes place
		builder.append(pattern.getId() + "\t");
		builder.append(mapping.getProperty().getUri() + "\t");
		builder.append(pattern.getNaturalLanguageRepresentation() + "\t");
		builder.append(Constants.NEW_LINE_SEPARATOR);
		
		return builder.toString();
	}
}
