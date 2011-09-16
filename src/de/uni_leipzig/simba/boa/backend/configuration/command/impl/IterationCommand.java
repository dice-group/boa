package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class IterationCommand implements Command {

	public static Integer CURRENT_ITERATION_NUMBER = 1;
	public static final Integer MAXIMUM_ITERATIONS = 1;
	
	@Override
	public void execute() {

		List<PatternMapping> patternMappings;
		Map<Integer,Triple>	triples;
		
		long start = new Date().getTime();
		
		// load the SPARQL dump into the database
		Command loadKnowledgeCommand = new LoadKnowledgeCommand();
		loadKnowledgeCommand.execute();
		
		for ( ; CURRENT_ITERATION_NUMBER <= MAXIMUM_ITERATIONS ; CURRENT_ITERATION_NUMBER++) {
			
			long startIteration = new Date().getTime();
			System.out.println("Starting iteration " + CURRENT_ITERATION_NUMBER + "!");
			
			// search the patterns
			Command patternSearchCommand = new PatternSearchCommand(((LoadKnowledgeCommand)loadKnowledgeCommand).getTriples());
			patternSearchCommand.execute();
			
			// filter patterns
			Command patternFilterCommand = new PatternFilterCommand(((PatternSearchCommand) patternSearchCommand).getPatternMappings());
//			Command patternFilterCommand = new PatternFilterCommand(null);
			patternFilterCommand.execute();
			
			// calculate confidence, hand over the filtered patterns
//			Command patternConfidenceMeasureCommand = new PatternConfidenceMeasureCommand(((PatternSearchCommand) patternSearchCommand).getPatternMappings());
			Command patternConfidenceMeasureCommand = new PatternConfidenceMeasureCommand(((PatternFilterCommand) patternFilterCommand).getPatternMappingList());
			patternConfidenceMeasureCommand.execute();
			
			// generate rdf
			patternMappings = ((PatternConfidenceMeasureCommand) patternConfidenceMeasureCommand).getPatternMappingList();
			triples	= ((PatternSearchCommand) patternSearchCommand).getTriples();
			Command createKnowledgeCommand = new CreateKnowledgeCommand(patternMappings, triples);
//			Command createKnowledgeCommand = new CreateKnowledgeCommand(patternMappings, null);
			createKnowledgeCommand.execute();
			
			System.out.println("Iteration " + CURRENT_ITERATION_NUMBER + " took " + ((new Date().getTime() - startIteration) / 1000) + "s." );
		}
		System.out.println("For " + MAXIMUM_ITERATIONS + " iterations, BOA needed: " + ((new Date().getTime() - start) / 1000) + "s.");
	}
}
