package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	static List<Integer> doNotUseThose = new ArrayList<Integer>();
	
	@Override
	public void execute() {

		PatternMappingDao pmDao = (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
		
		List<Pair> pairs = new ArrayList<Pair>();
		
		// collect all the patterns
		for ( PatternMapping mapping : pmDao.findAllPatternMappings() ) {
			
			// get x patterns for each mapping and add it with the mapping URI to the map 
			List<Pattern> patterns = new ArrayList<Pattern>(mapping.getPatterns()); 
			Collections.shuffle(patterns);//PatternUtil.getTopNPattern(mapping, PatternSelectionStrategy.ALL, 5, null);
			
			if ( patterns.size() <= 200 ) {
				
				for (Pattern p : patterns) pairs.add(new Pair(mapping, p));
			}
			else {

				for ( int i = 0; i < 7 && i < patterns.size() ; i++) {
					
					if ( !doNotUseThose.contains( new Integer(patterns.get(i).getId())) ) {
						
						pairs.add(new Pair(mapping, patterns.get(i)));
					}
				}
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
				
				if ( i++ == 400 ) break;
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
	
	static {
		
		doNotUseThose.addAll(Arrays.asList(7063,6957,5777,5953,4876,405,5829,6963,7146,6162,6018,4940,1487,4890,6098,152,6839,4939,5925,7187,7027,4840,6196,6183,7345,430,6027,6797,1495,1475,5755,7306,5846,6299,5762,6020,7230,45,6234,5757,6131,5803,7007,6017,6998,5804,7309,1479,6249,5950,5819,7065,4823,41,7219,6254,5902,2119,417,7022,56,5956,5960,6138,11,1501,5922,7327,7183,7097,5805,423,6190,6006,4889,5910,1466,7147,4924,7179,6318,6931,5875,6057,5985,12,4999,6034,5906,5897,5975,407,5980,7048,138,6014,7137,6982,5503,7246,4925,1500,6901,6145,7075,6060,7353,7134,6276,4,6217,6265,4832,5812,5877,1040,7046,5009,5912,72,4844,7185,6149,7332,5788,4856,4850,6045,6812,28,6221,5978,5942,5761,7019,6843,6121,5823,6176,7267,7077,7360,7047,6155,7153,7205,5987,6307,6944,1476,7006,6223,5800,6010,7255,6046,6248,6275,6295,6062,7003,4969,51,7223,5928,5759,7174,4977,4907,5065,5979,5739,5913,7336,6031,4841,6022,6147,6883,6179,5918,7303,5826,7136,5884,409,7238,6999,6810,6820,5890,4912,4958,6077,5836,5947,4980,15,7092,5900));
	}
}
