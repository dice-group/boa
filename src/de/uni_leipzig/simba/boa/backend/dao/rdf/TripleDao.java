package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
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
    
    public void batchSaveOrUpdate(List<Triple> triples) {
    	
    	Session session = HibernateFactory.getSessionFactory().openSession();
    	Transaction tx = session.beginTransaction();
    	int batchSize = Integer.valueOf(NLPediaSettings.getInstance().getSetting("hibernate.jdbc.batch_size"));
    	   
    	for ( int i = 0; i < triples.size() ; i++ ) {
    	    
    	    session.saveOrUpdate(triples.get(i));
    	    
    	    if ( i % batchSize == 0 ) { 
    	        //flush a batch of inserts and release memory:
    	        session.flush();
    	        session.clear();
    	    }
    	}
    	   
    	tx.commit();
    	session.close();
    }
    
    @SuppressWarnings("unchecked")
   	public List<Triple> findCorrectTriples() {

   		Session session = HibernateFactory.getSessionFactory().openSession();
       	List<Triple> triples = session.createCriteria(Triple.class)
       							.add(Restrictions.eq("correct", true))
       							.list();
       	session.close();
   		return triples;
   	}
    
    @SuppressWarnings("unchecked")
	public List<Triple> findNewTriplesForUri(String uri) {

		Session session = HibernateFactory.getSessionFactory().openSession();
    	List<Triple> triples = session.createCriteria(Triple.class)
    							.add(Restrictions.eq("correct", false))
    							.createCriteria("property").add(Restrictions.eq("uri", uri))
    							.list();
    	
    	session.close();
		return triples;
	}

	@SuppressWarnings("unchecked")
	public Triple findTripleBySubjectPredicateObject(Resource subject, Property property, Resource object) {

		Session session = HibernateFactory.getSessionFactory().openSession();
    	List<Triple> triples = session.createCriteria(Triple.class)
    							.createCriteria("subject").add(Restrictions.eq("uri", subject.getUri()))
    							.createCriteria("property").add(Restrictions.eq("uri", property.getUri()))
    							.createCriteria("object").add(Restrictions.eq("uri", object.getUri()))
    							.list();
    	session.close();
		return triples.size() > 0 ? triples.get(0) : null;
	}
}
