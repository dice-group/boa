package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.queryParser.ParseException;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternMappingDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.ResourceDao;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.entity.context.Context;
import de.uni_leipzig.simba.boa.backend.entity.context.LeftContext;
import de.uni_leipzig.simba.boa.backend.entity.context.RightContext;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;
import de.uni_leipzig.simba.boa.backend.rdf.uri.impl.DbpediaUriRetrieval;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.concurrent.CreateKnowledgeCallable;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil;
import de.uni_leipzig.simba.boa.backend.util.PatternUtil.PatternSelectionStrategy;

/**
 * 
 * @author Daniel Gerber
 */
public class CreateKnowledgeCommand implements Command {

	private final NLPediaLogger logger = new NLPediaLogger(CreateKnowledgeCommand.class);
	
	private final PatternMappingDao patternMappingDao			= (PatternMappingDao) DaoFactory.getInstance().createDAO(PatternMappingDao.class);
	private final TripleDao tripleDao							= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);
	
	private final Integer NUMBER_OF_CREATE_KNOWLEDGE_THREADS	= Integer.valueOf(NLPediaSettings.getInstance().getSetting("number.of.create.knowledge.threads"));
	private final String N_TRIPLES_FILE							=	NLPediaSettings.getInstance().getSetting("ntriples.file.path") +  
																	"triples_score" + 
																	NLPediaSettings.getInstance().getSetting("score.threshold.create.knowledge") +
																	"_topn" + 
																	NLPediaSettings.getInstance().getSetting("top.n.pattern") +
																	".nt";
																	// something like: /path/to/file/triples_score0.7_topn20.nt
	
	private final List<PatternMapping> patternMappingList;

	public CreateKnowledgeCommand(List<PatternMapping> mappings) {

		if ( mappings != null ) this.patternMappingList = mappings;
		else this.patternMappingList = patternMappingDao.findAllPatternMappings();
	}

	/**
	 * 
	 */
	@Override
	public void execute() {

		// one thread per pattern mapping but only n threads get executed at the same time
		for (PatternMapping mapping : this.patternMappingList ) {
			
			this.writeNTriplesFile(new CreateKnowledgeCallable(mapping).call());
			this.logger.info("Added worker for mapping: " + mapping.getProperty().getUri());
		}
		
//		try {
//			
//			// create a thread pool and service for n threads/callable
//			ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CREATE_KNOWLEDGE_THREADS);
//			this.logger.info("Created executorservice for knowledge creation of " + NUMBER_OF_CREATE_KNOWLEDGE_THREADS + " threads.");
//			
//			List<Callable<Collection<Triple>>> todo = new ArrayList<Callable<Collection<Triple>>>(this.patternMappingList.size());
//
//			// one thread per pattern mapping but only n threads get executed at the same time
//			for (PatternMapping mapping : this.patternMappingList ) {
//				
//				todo.add(new CreateKnowledgeCallable(mapping));
//				this.logger.info("Added worker for mapping: " + mapping.getProperty().getUri());
//			}
//			
//			// invoke all waits until all threads are finished
//			List<Future<Collection<Triple>>> answers = executorService.invokeAll(todo);
//			
//			for (Future<Collection<Triple>> future : answers) {
//			
//				Collection<Triple> triples = future.get();
//				this.logger.info("Calling write to file method with " + triples.size() + " triples.");
//				this.writeNTriplesFile(triples);
//			}
//			
//			// shut down the service and all threads
//			executorService.shutdown();
//		}
//		catch (ExecutionException e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
//		catch (InterruptedException e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
//		catch (Exception e) {
//			
//			this.logger.error("Execption", e);
//			e.printStackTrace();
//		}
	}
	
	private void writeNTriplesFile(Collection<Triple> resultList) {

		try {
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(N_TRIPLES_FILE, true), "UTF-8"));

			for (Triple t : resultList) {
				
				if ( t != null ) {
					
					if ( t.getObject() != null) {
						
						if ( t.getObject().getUri().startsWith("http://")) {
							
							writer.write("<" + t.getSubject().getUri() + "> " +
										 "<"+ t.getProperty().getUri() + "> " +
										 "<" + t.getObject().getUri() +"> . " + Constants.NEW_LINE_SEPARATOR);
						}
						else {
							
							writer.write("<" + t.getSubject().getUri() + "> " +
										 "<"+ t.getProperty().getUri() + "> " +
										 "\"" + t.getObject().getLabel() +"\" . " + Constants.NEW_LINE_SEPARATOR);
						}
					}
					else {
						
						this.logger.error("Object null for triple: " + t);
					}
				}
				else {
					
					this.logger.error("Triple was null!");
				}
			}
//			tripleDao.batchSaveOrUpdate(new ArrayList<Triple>(resultList));
			writer.close();
		}
		catch (UnsupportedEncodingException e1) {
			
			this.logger.error("UnsupportedEncodingException", e1);
		}
		catch (FileNotFoundException e1) {
			
			this.logger.error("UnsupportedEncodingException", e1);
		}
		catch (IOException e) {
			
			this.logger.error("UnsupportedEncodingException", e);
		}
	}

	private static void uploadToDydra(Triple triple) {

		String dydraCurlPush = "curl -H 'Content-Type: text/plain' \\ " +
				 				"-d '<"+ triple.getSubject().getUri() +"> <"+ triple.getProperty().getUri() +"> <"+ triple.getObject().getUri() +"> .' \\ " + 
				 				"http://ow8MG1zTy3yYWmNwlPph@dydra.com/daniel-gerber" + NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl").substring(NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl").lastIndexOf("/"));
		 
		System.out.println(dydraCurlPush);
		try {
			 
			Runtime.getRuntime().exec(dydraCurlPush);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
