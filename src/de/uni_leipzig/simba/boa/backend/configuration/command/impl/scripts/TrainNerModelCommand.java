package de.uni_leipzig.simba.boa.backend.configuration.command.impl.scripts;

import java.util.Scanner;

import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.nlp.learn.NamedEntityRecognizerLearner;


public class TrainNerModelCommand implements Command {

	@Override
	public void execute() {

//		Scanner scanner = new Scanner(System.in);
//		System.out.print("Enter path of DBpedia-Ontology:\t");
//		String pathToDBpediaOntology = scanner.next();
//		System.out.print("Enter path of output file:\t");
//		String pathToTrainedSentenceFile = scanner.next();
//		System.out.print("Enter path of labels file:\t");
//		String pathToLabelsFile = scanner.next();
//		System.out.print("Enter path of types file:\t");
//		String pathToTypesFile = scanner.next();
		
		NamedEntityRecognizerLearner learner = new NamedEntityRecognizerLearner();
//		learner.setPathToTrainedSentenceFile(pathToTrainedSentenceFile);
//		learner.setPathToDBpediaOntology(pathToDBpediaOntology);
//		learner.setPathToLabelsFile(pathToLabelsFile);
//		learner.setPathToTypesFile(pathToTypesFile);
		learner.learn();
	}
}
