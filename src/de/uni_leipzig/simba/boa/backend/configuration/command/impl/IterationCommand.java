package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class IterationCommand implements Command {

	@Override
	public void execute() {

		List<PatternMapping> patternMappings;
		Map<Integer,Triple>	triples;
		
		int iterations = 1;
		long start = new Date().getTime();
		
//		// load the SPARQL dump into the database
		Command loadKnowledgeCommand = new LoadKnowledgeCommand();
		loadKnowledgeCommand.execute();
		
		for ( int i = 1 ; i <= iterations ; i++) {
			
			long startIteration = new Date().getTime();
			System.out.println("Starting iteration " + i + "!");
			
			// search the patterns
			Command patternSearchCommand = new PatternSearchCommand(((LoadKnowledgeCommand)loadKnowledgeCommand).getTriples());
			((PatternSearchCommand) patternSearchCommand).setIteration(i);
			patternSearchCommand.execute();
			
			// filter patterns
//			Command patternFilterCommand = new PatternFilterCommand(((PatternSearchCommand) patternSearchCommand).getPatternMappings());
//			Command patternFilterCommand = new PatternFilterCommand(null);
//			patternFilterCommand.execute();
			
			// calculate confidence, hand over the filtered patterns
//			Command patternConfidenceMeasureCommand = new PatternConfidenceMeasureCommand(((PatternFilterCommand) patternFilterCommand).getPatternMappingList());
//			patternConfidenceMeasureCommand.execute();
			
			// generate rdf
//			List<PatternMapping> patternMappings = ((PatternConfidenceMeasureCommand) patternConfidenceMeasureCommand).getPatternMappingList();
//			Map<Integer,Triple>	triples	= ((PatternSearchCommand) patternSearchCommand).getTriples();
//			Command createKnowledgeCommand = new CreateKnowledgeCommand(patternMappings, triples);
//			Command createKnowledgeCommand = new CreateKnowledgeCommand(patternMappings, null);
//			createKnowledgeCommand.execute();
			
			System.out.println("Iteration " + i + " took " + ((new Date().getTime() - startIteration) / 1000) + "s." );
		}
		System.out.println("Pattern search took: " + ((new Date().getTime() - start) / 1000) + "ms.");
	}
}
