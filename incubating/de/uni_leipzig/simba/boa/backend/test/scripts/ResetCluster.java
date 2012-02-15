package de.uni_leipzig.simba.boa.backend.test.scripts;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.cluster.ClusterDao;
import de.uni_leipzig.simba.boa.backend.entity.cluster.Cluster;


public class ResetCluster {
	
	
	public static void main(String[] args) {
		
		NLPediaSetup setup = new NLPediaSetup(false);
		
		ClusterDao clusterDao = (ClusterDao) DaoFactory.getInstance().createDAO(ClusterDao.class);
		
		for (Cluster c: clusterDao.findAllCluster()) {
			
			clusterDao.deleteCluster(c);
		}
	}
}
