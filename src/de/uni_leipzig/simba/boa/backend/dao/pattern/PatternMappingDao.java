package de.uni_leipzig.simba.boa.backend.dao.pattern;

import java.util.ArrayList;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.Entity;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


/**
 * 
 * @author Daniel Gerber
 */
public class PatternMappingDao extends AbstractDao {

	public PatternMappingDao() {
        super();
    }

	/**
	 * 
	 * @param patternMapping
	 */
    public Entity createAndSavePatternMapping(PatternMapping patternMapping) {
    	
        return super.saveOrUpdateEntity(patternMapping);
    }
    
    /**
     * 
     * @return
     */
    public Entity createNewEntity() {
    	
        return this.createAndSavePatternMapping(new PatternMapping());
    }

    /**
     * 
     * @param patternMapping
     */
    public void deletePattern(PatternMapping patternMapping) {
    	
        super.deleteEntity(patternMapping);
    }

    /**
     * 
     * @param id
     * @return
     */
    public PatternMapping findPatternMapping(int id) {
    	
        return (PatternMapping) super.findEntityById(PatternMapping.class, id);
    }

    /**
     * 
     * @param patternMapping
     */
    public void updatePatternMapping(PatternMapping patternMapping) {
    	
        super.saveOrUpdateEntity(patternMapping);
    }

    /**
     * 
     * @return
     */
    public List<PatternMapping> findAllPatternMappings() {
    	
        return (List<PatternMapping>) super.findAllEntitiesByClass(PatternMapping.class);
    }

	/**
	 * 
	 * @return
	 */
	public List<PatternMapping> findPatternMappingsWithoutPattern(String uri) {

		List<PatternMapping> objects = new ArrayList<PatternMapping>();
		
		Transaction tx = null;
		Session session = null;
		
		try {
			
			session = HibernateFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			String queryString = (uri == null) ? "select distinct(pm.id), pm.uri, pm.rdfsRange, pm.rdfsDomain from pattern_mapping as pm, pattern as p where pm.id = p.pattern_mapping_id and (withLogConfidence >= 0 or withoutLogConfidence >= 0) and useForPatternEvaluation = 1 order by pm.uri;" : "select pm.id, pm.uri, pm.rdfsRange, pm.rdfsDomain from pattern_mapping as pm where uri=:uri"; 
			
			Query query = session.createSQLQuery(queryString);
			if ( uri != null ) query.setString("uri", uri);;
			
			List<Object[]> objs = query.list();
			for (Object[] obj : objs) {
				
				PatternMapping pm = new PatternMapping();
				pm.setId((Integer) obj[0]);
				pm.setUri((String) obj[1]);
				pm.setRdfsRange((String) obj[2]);
				pm.setRdfsDomain((String) obj[3]);
				objects.add(pm);
			}
			
			tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(tx);
			super.logger.error("Error...", he);
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}
		return objects;
	}
}
