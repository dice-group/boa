//package de.uni_leipzig.simba.boa.backend.limes;
//
//import java.util.List;
//import java.util.Map;
//import java.util.TreeSet;
//
//import de.uni_leipzig.simba.boa.backend.entity.cluster.Cluster;
//import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
//import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
//
//
//public class ClusterThread extends Thread {
//
//	private final NLPediaLogger logger = new NLPediaLogger(ClusterThread.class);
//	
//	private Map<Integer,Pattern> patternMap;
//	private List<Cluster> results;
//	private PatternSimilarityCalculator psc;
//	private boolean accepted;
//	
//	public ClusterThread(PatternSimilarityCalculator psc, Map<Integer,Pattern> patternMap, List<Cluster> results, boolean accepted) {
//		
//		this.results = results;
//		this.psc = psc;
//		this.patternMap = patternMap;
//		this.accepted = accepted;
//	}
//	
//	@Override public void run() {
//		
//		System.out.println(this.getName() + " starting to cluster!");
//		long start = System.currentTimeMillis();
//		this.logger.debug("Starting to cluster links which " + (accepted ? "are accepted!" : "need review!"));
//		this.createCluster(psc.runClustering(this.accepted ? psc.getStatementsAccepted() : psc.getStatementsToReview()), this.accepted);
//		this.logger.debug("Saving cluster which "+ (accepted ? "are accepted!" : "need review!") + " DONE! This took " + (System.currentTimeMillis() - start) + "ms.");
//	}
//	
//	private void createCluster(Map<TreeSet<String>, TreeSet<String>> cluster, boolean accepted) {
//		
//		this.logger.debug("Saving " + cluster.size() + " clusters to the database");
//		for (TreeSet<String> entry : cluster.keySet() ) {	
//			
////			System.out.println(entry);
//			
//        	Cluster newCluster = new Cluster(String.valueOf(entry.hashCode()));
//        	newCluster.setAccepted(accepted);
//        	
//        	// go through all cluster entries and try to find the pattern
//    		for ( String patternId : entry ) {
//    			
//    			Pattern p = this.patternMap.get(Integer.valueOf(patternId));
//    			newCluster.addPattern(p);
//    		}
//    		// look at every entry and try to determine which uri should be used, and save
//    		newCluster.calculateUriAffiliationPropabilities();
//    		newCluster.setName(newCluster.calculateMostLikelyUri());
//    		this.results.add(newCluster);
//		}
//	}
//}