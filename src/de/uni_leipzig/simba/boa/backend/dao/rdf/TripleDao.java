package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
    	
    	List<Triple> triples = (List<Triple>) super.findAllEntitiesByClass(Triple.class);
    	System.out.println(String.format("findAllTriples returned %s triples", triples.size()));
        return triples;
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
	public boolean exists(Triple triple) {

		Session session = HibernateFactory.getSessionFactory().openSession();
    	
		String query = "select r1.uri, r2.uri, r3.uri from triple as t, resource as r1, resource as r2, resource as r3 where t.subject_id = r1.id and t.property_id = r2.id and t.object_id = r3.id and t.correct = 0 and r1.uri = '"+triple.getSubject().getUri()+"' and r2.uri = '" + triple.getProperty().getUri() + "' and r3.uri = '"+triple.getObject().getUri()+"' order by confidence desc limit 1;";
		List<Object[]> result = session.createSQLQuery(query).list();
		return result != null && result.size() > 0 ? true : false ;
	}

	public List<Triple> queryTopNTriples(Integer maxValues) {

		Session session = HibernateFactory.getSessionFactory().openSession();
		List<Triple> triples = session.createCriteria(Triple.class).
								add(Restrictions.eq("correct", false)).
								addOrder(Order.desc("confidence")).setMaxResults(maxValues).list();
		
		session.close();
		return triples;
	}
}
