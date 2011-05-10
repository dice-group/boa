package de.uni_leipzig.simba.boa.backend.limes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import semiosys.colt.xtend.SparseSuperDoubleMatrix2D;
import de.uni_leipzig.bf.cluster.BorderFlow;
import de.uni_leipzig.bf.cluster.ClusterGraph;
import de.uni_leipzig.cugar.harden.HardenMaxQuality;
import de.uni_leipzig.cugar.harden.QualityMeasureRelativeFlow;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.cache.HybridCache;
import de.uni_leipzig.simba.data.Mapping;
import de.uni_leipzig.simba.filter.LinearFilter;
import de.uni_leipzig.simba.io.ConfigReader;
import de.uni_leipzig.simba.io.KBInfo;
import de.uni_leipzig.simba.mapper.SetConstraintsMapper;
import de.uni_leipzig.simba.mapper.SetConstraintsMapperFactory;
import de.uni_leipzig.simba.mapper.atomic.PPJoinMapper;

/**
 * 
 * @author Daniel Gerber
 */
public class PatternSimilarityCalculator {

	private final ConfigReader configReader = new ConfigReader();
	private final NLPediaLogger logger = new NLPediaLogger(PatternSimilarityCalculator.class);
	
	private HybridCache source = new HybridCache();
	private HybridCache target = new HybridCache();
	
	private List<SimilarityStatement> statementsAccepted = null;
	private List<SimilarityStatement> statementsToReview = null;
	
	/**
	 * 
	 */
	public PatternSimilarityCalculator(Map<Integer, String> sourceAndTargetPatterns) {
		
		this.readLimesConfig();
		this.fillCaches(sourceAndTargetPatterns);
	}
	
	public void runLinking() {
		
		long startTime = System.currentTimeMillis();
		
		SetConstraintsMapper mapper = SetConstraintsMapperFactory.getMapper(
				this.configReader.executionPlan, this.configReader.source, this.configReader.target, this.source, this.target, new PPJoinMapper(), new LinearFilter());
        
		Mapping mapping = mapper.getLinks(this.configReader.metricExpression, this.configReader.verificationThreshold);        

		this.statementsAccepted = new ArrayList<SimilarityStatement>();
		this.statementsToReview = new ArrayList<SimilarityStatement>();
		
        // now get results
        for ( String key : mapping.map.keySet() ) {
        	
            for ( String value : mapping.map.get(key).keySet() ) {
            	
            	Double similarity = mapping.map.get(key).get(value);
            	
            	// all patterns are unique -> similarity of 1 is only possible if we compare the same pattern
            	if ( similarity != 1 ) {
            		
            		if ( similarity >= this.configReader.acceptanceThreshold ) {
                    	
                    	this.statementsAccepted.add( new SimilarityStatement(key, this.configReader.acceptanceRelation, value, similarity));
                    } 
                    else if ( similarity >= this.configReader.verificationThreshold ) {
                    	
                    	this.statementsToReview.add( new SimilarityStatement(key, this.configReader.acceptanceRelation, value, similarity));
                    }
            	}
            }
        }
        logger.info("Limes finished with " + this.statementsAccepted.size() + " accpeted links.");
        logger.info("Limes finished with " + this.statementsToReview.size() + " links which need review.");
        System.out.println("Limes finished with " + this.statementsAccepted.size() + " accpeted links.");
        System.out.println("Limes finished with " + this.statementsToReview.size() + " links which need review.");
	}
	
	/**
	 * Runs a clustering for the output of limes with the help of the borderflow
	 * algorithm. 
	 * 
	 * @param statements list of statments: subject,predicat,object:similarity
	 * @return clusters of the statements
	 */
	public Map<TreeSet<String>, TreeSet<String>> runClustering(List<SimilarityStatement> statements) {
		
		ClusterGraph graph = this.initializeClusterGraph(statements);
		BorderFlow borderFlow = new BorderFlow(graph);
		Map<TreeSet<Integer>, TreeSet<Integer>> clusters = borderFlow.cluster(-10, true, true, true);
		HardenMaxQuality harden = new HardenMaxQuality(new QualityMeasureRelativeFlow());
		return borderFlow.getLabels(harden.harden(clusters, graph));
	}

	/**
	 * Create a new ClusterGraph and sets the necessary values according
	 * to the list of statements provided.
	 * 
	 * @param statements - produced by limes: x,y,0.77
	 * @return a new cluster graph
	 */
	private ClusterGraph initializeClusterGraph(List<SimilarityStatement> statements) {

		// first build index
		logger.info("Generating index for graph...");
		
		TreeSet<String> terms = new TreeSet<String>();             
		for (SimilarityStatement statement : statements) {
			
			terms.add(statement.getSubject());
			terms.add(statement.getObject());
		}

		//fill the index table
		Map<String, Integer> index = new HashMap<String, Integer>();
		Map<Integer, String> reverseIndex = new HashMap<Integer, String>();
		
		Iterator<String> iter = terms.iterator();
		for (int id = 0; iter.hasNext() ; id++) {
			
			String term = iter.next();
			index.put(term, new Integer(id));
			reverseIndex.put(new Integer(id), term);
		}
		//now build matrix
		logger.info("Reading in graph ...");
		// set graph size
		int size = terms.size();             
		SparseSuperDoubleMatrix2D matrix = new SparseSuperDoubleMatrix2D(size,size);

		for (SimilarityStatement statement : statements) {

			// link to node itself
			if (index.get(statement.getSubject()).compareTo(index.get(statement.getObject())) == 0) continue;
			
			// 
			if (index.get(statement.getSubject()).compareTo(index.get(statement.getObject())) > 0) { 
				
				matrix.set(	index.get(statement.getSubject()).intValue(), 
							index.get(statement.getObject()).intValue(), 
							statement.getSimilarity());
			}
			else {
				
				matrix.set(	index.get(statement.getObject()).intValue(), 
							index.get(statement.getSubject()).intValue(), 
							statement.getSimilarity());
			}
		}
		
		ClusterGraph graph = new ClusterGraph();
		graph.setSize(size);
		graph.setMatrix(matrix);
		graph.setIndex(index);
		graph.setReverseIndex(reverseIndex);
		
		return graph;
	}

	/**
	 * read the limes configuration and log what's happening
	 */
	private void readLimesConfig() {

		// calculation settings
		this.configReader.acceptanceRelation	= "naturalLanguageRepresenation";
		this.configReader.acceptanceThreshold	= new Double(NLPediaSettings.getInstance().getSetting("limesAcceptanceThreshold"));
		this.configReader.verificationThreshold = new Double(NLPediaSettings.getInstance().getSetting("limesVerificationThreshold"));
		this.configReader.metricExpression		= NLPediaSettings.getInstance().getSetting("limesMetricExpression");
		this.configReader.outputFormat			= "TAB";

		// source settings		
		this.configReader.source			= new KBInfo();
		this.configReader.source.id 		= "pattern1";
		this.configReader.source.var 		= "?x";
		this.configReader.source.type		= "csv"; // TODO wont need that
		this.configReader.source.properties	= Arrays.<String>asList("naturalLanguageRepresentation");

		// target settings
		this.configReader.target			= new KBInfo();
		this.configReader.target.id			= "pattern2";
		this.configReader.target.var		= "?y";
		this.configReader.target.type		= "csv"; // TODO wont need that
		this.configReader.target.properties	= Arrays.<String>asList("naturalLanguageRepresentation");
		
		this.logger.info(this.configReader.getSourceInfo().toString());
		this.logger.info(this.configReader.getTargetInfo().toString());
	}
	
	/**
	 * 
	 * 
	 * @param sourceAndTarget
	 */
	private void fillCaches(Map<Integer,String> sourceAndTarget) {

		String predicate = "naturalLanguageRepresentation";
		
		for ( Integer key : sourceAndTarget.keySet() ) {
			
			this.source.addTriple(key.toString(), predicate, sourceAndTarget.get(key));
			this.target.addTriple(key.toString(), predicate, sourceAndTarget.get(key));
		}
	}
	
	/**
	 * @return the statementsAccepted
	 */
	public List<SimilarityStatement> getStatementsAccepted() {
	
		return statementsAccepted;
	}

	/**
	 * @return the statementsToReview
	 */
	public List<SimilarityStatement> getStatementsToReview() {
	
		return statementsToReview;
	}
}


























//	/**
//	 * read the two knowledge bases parallel
//	 */
//	private void loadSourceAndTargetData() {
//
//		Thread readSourceThread = new Thread( new Runnable() {
//									
//			public void run() {
//				
//				//2. Fill caches using the query module
//		        //2.1 First source
//				logger.info("Loading source data ...");
//		        source = new HybridCache();
//		        source = getData(configReader.getSourceInfo());
//	        }
//		});
//		
//		Thread readTargetThread = new Thread( new Runnable() {
//			
//			public void run() {
//				
//				 //2.2 Then target
//				logger.info("Loading target data ...");
//		        target = new HybridCache();
//		        target = getData(configReader.getSourceInfo());
//	        }
//		});
//		
//		// start the threads
//		readSourceThread.start();
//		readTargetThread.start();
//		
//		// wait for them to be finished before moving on
//		try {
//			
//			readSourceThread.join();
//			readSourceThread.join();
//		}
//		catch (InterruptedException e) {
//			
//			this.logger.fatal("Reading source and target was interrupted!", e);
//			e.printStackTrace();
//			throw new RuntimeException();
//		}
//	}
	
//	/**
//	 * swap the data source/target and the corresponding config values 
//	 */
//	private void swapSourceAndTargetIfNecessary() {
//
//       if (this.target.size() > this.source.size()) {
//
//       	this.logger.info("Swapping data sources as |T| > |S|");
//       	
//       	//2.3 Swap target and source if target size is larger than source size
//           HybridCache help;
//           KBInfo swap;
//           String var;
//       	
//       	//swap data sources
//           help = this.target;
//           this.target = this.source;
//           this.source = help;
//           
//           //swap configs
//           swap = this.configReader.source;
//           this.configReader.source = this.configReader.target;
//           this.configReader.target = swap;
//       }
//	}