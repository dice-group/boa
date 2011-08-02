package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;

public class TripleDao extends AbstractDao {

	public TripleDao() {
        super();
    }

	/**
	 * 
	 * @param triple
	 * @return 
	 */
    public Triple createAndSaveTriple(Triple triple) {
    	
        return (Triple) super.saveOrUpdateEntity(triple);
    }
    
    /**
     */
    public Triple createNewEntity() {

    	return (Triple) super.saveOrUpdateEntity(new Triple());
	}

    /**
     * 
     * @param triple
     */
    public void deleteTriple(Triple triple) {
    	
        super.deleteEntity(triple);
    }

    /**
     * 
     * @param id
     * @return
     */
    public Triple findTriple(int id) {
    	
        return (Triple) super.findEntityById(Triple.class, id);
    }

    /**
     * 
     * @param triple
     */
    public void updateTriple(Triple triple) {
    	
        super.saveOrUpdateEntity(triple);
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Triple> findAllTriples() {
    	
        return (List<Triple>) super.findAllEntitiesByClass(Triple.class);
    }
}
