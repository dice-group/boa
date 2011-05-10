package de.uni_leipzig.simba.boa.backend.dao.cluster;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.entity.Cluster;


/**
 * 
 * @author Daniel Gerber
 */
public class ClusterDao extends AbstractDao {

    public ClusterDao() {
        super();
    }

	/**
	 * 
	 * @param pattern
	 * @return 
	 */
    public Cluster createAndSaveCluster(Cluster cluster) {
    	
        return (Cluster) super.saveOrUpdateEntity(cluster);
    }
    
    /**
     * return null!!!!
     */
    public Cluster createNewEntity() {

    	new RuntimeException("dont use this constructor!");
		return null;
	}

    /**
     * 
     * @param pattern
     */
    public void deleteCluster(Cluster cluster) {
    	
        super.deleteEntity(cluster);
    }

    /**
     * 
     * @return
     */
    public List<Cluster> findAllCluster() {
    	
        return (List<Cluster>) super.findAllEntitiesByClass(Cluster.class);
    }
    
    /**
     * 
     * @param id
     * @return
     */
    public Cluster findCluster(int id) {
    	
        return (Cluster) super.findEntityById(Cluster.class, id);
    }

    /**
     * 
     * @param pattern
     */
    public void updateCluster(Cluster cluster) {
    	
        super.saveOrUpdateEntity(cluster);
    }
}
