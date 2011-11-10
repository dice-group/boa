package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.Feature;
import de.uni_leipzig.simba.boa.backend.entity.pattern.feature.helper.FeatureHelper;

public class CreateMachineLearningCommand implements Command {

	public static void main(String[] args) {

		NLPediaSetup s = new NLPediaSetup(false);
		CreateMachineLearningCommand l = new CreateMachineLearningCommand();
		l.execute();
	}
	
	@Override
	public void execute() {

		PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		List<Pair> pairs = new ArrayList<Pair>();
		
		// collect all the patterns
		for ( PatternMapping mapping : pmDao.findAllPatternMappings() ) {
			
			// get x patterns for each mapping and add it with the mapping URI to the map 
			List<Pattern> patterns = new ArrayList<Pattern>(mapping.getPatterns()); 
			Collections.shuffle(patterns);//PatternUtil.getTopNPattern(mapping, PatternSelectionStrategy.ALL, 5, null);
			
			for ( int i = 0; i < 5 && i < patterns.size() ; i++) {
				
				pairs.add(new Pair(mapping, patterns.get(i)));
			}
			System.out.println(String.format("Added %s patterns for mapping %s", patterns.size(), mapping.getProperty().getUri()));
		}
		System.out.println(String.format("Found %s patterns at all", pairs.size()));
		Collections.shuffle(pairs);
		
		try {
//			Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/Users/gerb/machine_learning_input.txt")));
			Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/home/gerber/machine_learning_input.txt")));
			
			int i = 0;
			for ( Pair pair : pairs ) {
				
				if ( i++ == 200 ) break;
				writer.write(FeatureHelper.createNetworkTrainingFileLine(pair.getMapping(), pair.getPattern()));
			}
			writer.close();
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private class Pair {
		
		private PatternMapping mapping;
		private Pattern pattern;
		
		private Pair (PatternMapping mapping, Pattern pattern) {
			
			this.setMapping(mapping);
			this.setPattern(pattern);
		}

		/**
		 * @return the mapping
		 */
		public PatternMapping getMapping() {

			return mapping;
		}

		/**
		 * @param mapping the mapping to set
		 */
		public void setMapping(PatternMapping mapping) {

			this.mapping = mapping;
		}

		/**
		 * @return the pattern
		 */
		public Pattern getPattern() {

			return pattern;
		}

		/**
		 * @param pattern the pattern to set
		 */
		public void setPattern(Pattern pattern) {

			this.pattern = pattern;
		}
	}
}
