package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.persistance.Entity;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;

public class ResourceDao extends AbstractDao {

	public ResourceDao() {
        super();
    }

	/**
	 * 
	 * @param resource
	 * @return 
	 */
    public Resource createAndSaveResource(Resource resource) {
    	
        return (Resource) super.saveOrUpdateEntity(resource);
    }
    
    /**
     */
    public Resource createNewEntity(String uri, String label) {

		return new Resource(uri, label);
	}

    /**
     * 
     * @param resource
     */
    public void deleteResource(Resource resource) {
    	
        super.deleteEntity(resource);
    }

    /**
     * 
     * @param id
     * @return
     */
    public Resource findResource(int id) {
    	
        return (Resource) super.findEntityById(Resource.class, id);
    }

    /**
     * 
     * @param resource
     */
    public void updateResource(Resource resource) {
    	
        super.saveOrUpdateEntity(resource);
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Resource> findAllResources() {
    	
        return (List<Resource>) super.findAllEntitiesByClass(Resource.class);
    }

	@Override
	public Resource createNewEntity() {
		
		return (Resource) super.saveOrUpdateEntity(new Resource());
	}
}
