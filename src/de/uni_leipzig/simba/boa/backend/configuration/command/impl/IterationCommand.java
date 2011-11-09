package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class IterationCommand implements Command {

	// TODO make configurable
	public static Integer CURRENT_ITERATION_NUMBER = 1;
	public static final Integer MAXIMUM_ITERATIONS = 1;
	
	@Override
	public void execute() {

		long start = new Date().getTime();
		
		for ( ; CURRENT_ITERATION_NUMBER <= MAXIMUM_ITERATIONS ; CURRENT_ITERATION_NUMBER++) {
			
			long startIteration = new Date().getTime();
			System.out.println("Starting iteration " + CURRENT_ITERATION_NUMBER + "!");
			
			// search the patterns
			Command patternSearchCommand = new PatternSearchCommand(null);
			patternSearchCommand.execute();
			
			// calculate confidence measure values, hand over the filtered patterns
			Command patternConfidenceMeasureCommand = new PatternScoreFeatureCommand(((PatternSearchCommand) patternSearchCommand).getPatternMappings());
			patternConfidenceMeasureCommand.execute();
			
			// calculate confidence with neuronal network
			Command patternScoreCommand = new PatternScoreCommand(((PatternScoreFeatureCommand)patternConfidenceMeasureCommand).getPatternMappingList());
			patternScoreCommand.execute();
			
			// generate rdf
			List<PatternMapping> patternMappings = ((PatternScoreCommand) patternScoreCommand).getPatternMappingList();
			Map<Integer,Triple> triples = ((PatternSearchCommand) patternSearchCommand).getTriples();
			Command createKnowledgeCommand = new CreateKnowledgeCommand(patternMappings, triples);
			createKnowledgeCommand.execute();
			
			System.out.println("Iteration " + CURRENT_ITERATION_NUMBER + " took " + ((new Date().getTime() - startIteration) / 1000) + "s." );
		}
		System.out.println("For " + MAXIMUM_ITERATIONS + " iterations, BOA needed: " + ((new Date().getTime() - start) / 1000) + "s.");
	}
}
