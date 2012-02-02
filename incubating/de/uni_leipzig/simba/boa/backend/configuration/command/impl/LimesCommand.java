//package de.uni_leipzig.simba.boa.backend.configuration.command.impl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
//import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
//import de.uni_leipzig.simba.boa.backend.dao.cluster.ClusterDao;
//import de.uni_leipzig.simba.boa.backend.dao.pattern.PatternDao;
//import de.uni_leipzig.simba.boa.backend.entity.cluster.Cluster;
//import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
//import de.uni_leipzig.simba.boa.backend.exception.NLPediaRuntimeException;
//import de.uni_leipzig.simba.boa.backend.limes.ClusterThread;
//import de.uni_leipzig.simba.boa.backend.limes.PatternSimilarityCalculator;
//import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
//
//public class LimesCommand implements Command {
//	
//	private final NLPediaLogger logger = new NLPediaLogger(LimesCommand.class);
//
//	private final PatternDao patternDao = (PatternDao) DaoFactory.getInstance().createDAO(PatternDao.class);
//	
//	private final List<Pattern> patternList = patternDao.findAllPatterns();
//	private Map<Integer,Pattern> patternMap;
//	
//	@Override
//	public void execute() {
//
//		patternMap = new HashMap<Integer,Pattern>();
//		Map<Integer,String> sourceAndTargetForLimes = new HashMap<Integer,String>();
//		
//		for ( Pattern p : patternList ) {
//			
//			// pattern sollte au�er stoppw�rtern noch andere w�rter enthalten
//			if ( p.isUseForPatternEvaluation() ) {
//			
//				patternMap.put(Integer.valueOf(p.getId()), p);
//				sourceAndTargetForLimes.put(p.getId(), p.getNaturalLanguageRepresentation());
//			}
//		}
//		
//		PatternSimilarityCalculator psc = new PatternSimilarityCalculator(sourceAndTargetForLimes);
//		psc.runLinking();
//		
//		List<Cluster> results = new ArrayList<Cluster>();
//		
//		final ClusterThread ct1 = new ClusterThread(psc, patternMap, results, true);
//		final ClusterThread ct2 = new ClusterThread(psc, patternMap, results, false);
//		
//		// start and wait till both have finished
//		try {
//			
//			ct1.start();ct2.start();
//			ct1.join();ct2.join();
//		}
//		catch (InterruptedException e) {
//			
//			this.logger.fatal("Saving clusters was interrupted!", e);
//			e.printStackTrace();
//			throw new NLPediaRuntimeException("The multithreaded clustering was interrupted!");
//		}		
//		
//		final ClusterDao clusterDao = (ClusterDao) DaoFactory.getInstance().createDAO(ClusterDao.class);
//		for (Cluster cluster : results) {
//			
//			clusterDao.updateCluster(cluster);
//		}
//	}
//}
