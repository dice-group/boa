package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
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
    public synchronized Resource createAndSaveResource(Resource resource) {
    	
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

	public synchronized Resource findResourceByUri(String subject) {

		session = HibernateFactory.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Resource.class);
        criteria.add(Restrictions.eq("uri", subject));
        criteria.setMaxResults(1);
        List<Resource> resourceList = criteria.list();
        if ( resourceList.size() == 1) return resourceList.get(0);
        return null;
	}
}
