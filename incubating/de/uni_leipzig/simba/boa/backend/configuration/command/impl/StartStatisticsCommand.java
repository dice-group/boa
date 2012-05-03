/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.cluster.ClusterDao;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
import de.uni_leipzig.simba.boa.backend.entity.cluster.Cluster;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class StartStatisticsCommand implements Command {

	private NLPediaLogger logger = new NLPediaLogger(StartStatisticsCommand.class);
	
	private List<Pattern> patterns = null;
	
	/**
	 * 
	 */
	public void execute() {
		
		PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
		ClusterDao clusterDao = (ClusterDao) DaoFactory.getInstance().createDAO(ClusterDao.class);
		this.patterns = patternDao.findAllPatterns();
		
		// #############################################################################
		
		int min = this.calculateSmallestPatternLenght(Constants.ENGLISH_LANGUAGE);
		System.out.println("Number of token in smallest pattern: " + min);
		this.logger.info("Number of token in smallest pattern: " + min);
		
		// #############################################################################
		
		int max = this.calculateLargestPatternLenght(Constants.ENGLISH_LANGUAGE);
		System.out.println("Number of token in largest pattern: " + max);
		this.logger.info("Number of token in largest pattern: " + max);
		
		// #############################################################################
		
		double avg = this.calculateAveragePatternLenght(Constants.ENGLISH_LANGUAGE);
		System.out.println("Number of token in average pattern: " + avg);
		this.logger.info("Number of token in average pattern: " + avg);

		// #############################################################################
		
		Map<Integer,Integer> patternLenghtDistribution = this.calculateLenghtDistribution(Constants.ENGLISH_LANGUAGE);
		for ( Integer i : patternLenghtDistribution.keySet() ) {
			
			System.out.println("Number of tokens for pattern with length " + i + ": " + patternLenghtDistribution.get(i));
			this.logger.info("Number of tokens for pattern with length " + i + ": " + patternLenghtDistribution.get(i));
		}
		
		// #############################################################################
		
		Map<Character,Integer> characterDistribution = this.calculateCharacterDistribution(Constants.ENGLISH_LANGUAGE);
		List<Character> characters = new ArrayList<Character>(characterDistribution.keySet());
		Collections.sort(characters);
		for ( Character c : characters ) {
			
			System.out.println("Number of occurrences for character \"" + c + "\": " + characterDistribution.get(c));
			this.logger.info("Number of occurrences for character \"" + c + "\": " + characterDistribution.get(c));
		}
		
		// #############################################################################
		
		final List<String> usedUris 		= Arrays.asList("largestCity","location","city","deathPlace","placeOfBirth","headquarters","hometown","mouthMountain","federalState","leaderName");
		final List<String> maybeUsedUris	= Arrays.asList("hqCity","hq","born","placeofbirth","birthplace","placedeath","cityofdeath","placeOfDeath","deathplace","largestcity");
		
		List<Cluster> clusters = clusterDao.findAllCluster();
		for ( Cluster cluster : clusters ) {
			
			if ( usedUris.contains(cluster.getName()) || maybeUsedUris.contains(cluster.getName())) {
				
				System.out.println(cluster.getName());
				System.out.println("=============================");
				for (Entry<String,Double> entries : cluster.getUriAffiliationPropabilities().entrySet()) {
					
					System.out.println(entries.getKey() + " : " + entries.getValue());
				}
				System.out.println("=====");
				for ( Pattern pattern : cluster.getPattern() ) {
					
					System.out.println("\t" + pattern.getNumberOfOccurrences() + " : " + pattern.getNaturalLanguageRepresentation());
				}
				System.out.println();System.out.println();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private int calculateLargestPatternLenght(String language) {
		
		int maxLenght = -1;
		for ( Pattern p : this.patterns) {
			
			maxLenght = Math.max(p.getNaturalLanguageRepresentation().split(" ").length, maxLenght);
		}
		return maxLenght;
	}
	
	/**
	 * 
	 * @return
	 */
	private int calculateSmallestPatternLenght(String language) {
		
		int minLenght = 1000;
		for ( Pattern p : this.patterns) {
			
			minLenght = Math.min(p.getNaturalLanguageRepresentation().split(" ").length, minLenght);
		}
		return minLenght;
	}
	
	/**
	 * 
	 * @return
	 */
	private double calculateAveragePatternLenght(String language) {
		
		int lenght = 0;
		for ( Pattern p : this.patterns) {
			
			lenght += p.getNaturalLanguageRepresentation().split(" ").length;
		}
		return lenght/patterns.size();
	}
	
	/**
	 * 
	 * @param englishLanguage
	 * @return
	 */
	private Map<Integer, Integer> calculateLenghtDistribution(String language) {
		
		Map<Integer,Integer> distribution = new HashMap<Integer,Integer>();
		
		for ( Pattern p : this.patterns) {
			
			int lenght = p.getNaturalLanguageRepresentation().split(" ").length;
			
			if ( distribution.get(lenght) == null) {
				distribution.put(lenght, 1);
			}
			else {
				distribution.put(lenght, distribution.get(lenght) + 1);
			}
		}
		
		return distribution;
	}
	
	private Map<Character,Integer> calculateCharacterDistribution(String language) {
		
		Map<Character,Integer> distribution = new HashMap<Character,Integer>();
		
		for ( Pattern p : this.patterns ) {
			
			char[] textChars = p.getNaturalLanguageRepresentation().toCharArray();
			
			for( int i = 0; i < textChars.length ; i++ ) {
				
				Character currChar = new Character(textChars[i]);
				Integer cnt = distribution.get(currChar);
				
				if( cnt == null ) {
					distribution.put(currChar, new Integer(1));
				}
				else{
					distribution.put(currChar, ++cnt);
				}
			}
		}
		
		return distribution;
	}
}
