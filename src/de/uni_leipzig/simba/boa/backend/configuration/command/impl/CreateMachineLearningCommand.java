package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil.PatternSelectionStrategy;


public class CreateMachineLearningCommand implements Command {

	private Double reverbMax = 0D, supportMax = 0D, specificityMax = 0D, typicityMax = 0D, occMax = 0D, simMax = 0D, tfIdfMax = 0D, maxMax = 0D, pairMax = 0D;
	
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
			
			this.calculateMaximas(mapping);
			
			// get the top 5 patterns for each mapping and add it with the mapping URI to the map 
			List<Pattern> patterns = PatternUtil.getTopNPattern(mapping, PatternSelectionStrategy.ALL, 5, null); 
			for ( Pattern pattern : patterns ) pairs.add(new Pair(mapping, pattern));
					
			System.out.println(String.format("Added %s patterns for mapping %s", patterns.size(), mapping.getProperty().getUri()));
		}
		System.out.println(String.format("Found %s patterns at all", pairs.size()));
		Collections.shuffle(pairs);
		System.out.println(String.format("reverbMax: %s\nsupportMax: %s\nspecificityMax: %s\ntypicityMax: %s\noccMax: %s\nsimMax: %s\ntfIdfMax: %s\npairMax: %s\nmaxMax: %s\n ", 
				reverbMax, supportMax, specificityMax, typicityMax, occMax, simMax, tfIdfMax, maxMax, pairMax));
		
		try {
			
			Writer writer = new PrintWriter(new BufferedWriter(new FileWriter("/home/gerber/machine_learning_input.txt")));
			
			int i = -1;
			for ( Pair pair : pairs ) {
				
				if ( i++ == 100 ) break;
				
				writer.write(this.createLine(pair.mapping, pair.pattern));
			}
			writer.close();
		}
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	private String createLine(PatternMapping mapping, Pattern pattern) {

		StringBuilder builder = new StringBuilder();
		
		builder.append(mapping.getProperty().getUri() + "\t");
		builder.append(pattern.getNaturalLanguageRepresentation() + "\t");
		builder.append(pattern.getReverb() / reverbMax + "\t");
		builder.append(pattern.getSupport() / supportMax + "\t");
		builder.append(pattern.getSpecificity() / specificityMax + "\t");
		builder.append(pattern.getTypicity() / typicityMax + "\t");
		builder.append(new Double(pattern.getNumberOfOccurrences()) / occMax + "\t");
		builder.append(pattern.getSimilarity() / simMax + "\t");
		builder.append(pattern.getTfIdf() / tfIdfMax + "\t");
		builder.append(pattern.getLearnedFromPairs() / pairMax + "\t");
		builder.append(pattern.getMaxLearnedFrom() / maxMax + "\t" + Constants.NEW_LINE_SEPARATOR);
		
		return builder.toString();
	}

	private void calculateMaximas(PatternMapping mapping) {

		// reverbMax, supportMax, specificityMax, typicityMax, occMax, simMax, tfIdfMax, maxMax, pairMax;
		for ( Pattern p: mapping.getPatterns()) {
			
			reverbMax		= Math.max(reverbMax, p.getReverb() == null ? 0D : p.getReverb());
			supportMax 		= Math.max(supportMax, p.getSupport() == null ? 0D : p.getSupport());
			specificityMax 	= Math.max(specificityMax, p.getSpecificity() == null ? 0D : p.getSpecificity());
			typicityMax 	= Math.max(typicityMax, p.getTypicity() == null ? 0D : p.getTypicity());
			occMax 			= Math.max(occMax, p.getNumberOfOccurrences() == null ? 0D : p.getNumberOfOccurrences());
			simMax 			= Math.max(simMax, p.getSimilarity() == null ? 0D : p.getSimilarity());
			tfIdfMax 		= Math.max(tfIdfMax, p.getTfIdf() == null ? 0D : p.getTfIdf());
			pairMax 		= Math.max(pairMax, p.getLearnedFromPairs());
			maxMax 			= Math.max(maxMax, p.getMaxLearnedFrom());
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
