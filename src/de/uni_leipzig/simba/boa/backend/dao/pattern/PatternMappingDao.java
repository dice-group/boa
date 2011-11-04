package de.uni_leipzig.simba.boa.backend.dao.pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.persistance.Entity;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


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
    public PatternMapping createAndSavePatternMapping(PatternMapping patternMapping) {
    	
        return (PatternMapping) super.saveOrUpdateEntity(patternMapping);
    }
    
    /**
     * 
     * @return
     */
    public PatternMapping createNewEntity(Entity p) {
    	
    	if ( p instanceof Property ) {
    		
    		return (PatternMapping) super.saveOrUpdateEntity(new PatternMapping((Property) p));
    	}
        throw new RuntimeException("Parameter passed to this method was not a Property but a: " + p.getClass() );
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
			
			String queryString = (uri == null) 
				? 		"select distinct(pm.id), r.uri, r.rdfsRange, r.rdfsDomain " +
						"from pattern_mapping as pm, pattern as p, resource as r " +
						"where pm.id = p.patternMapping_id and (p.specificity > 0 or p.support > 0 or p.typicity > 0) and pm.property_id = r.id and r.DTYPE = 'PROPERTY' " +
						"order by r.uri;"
				: "select pm.id, r.uri, r.rdfsRange, r.rdfsDomain from pattern_mapping as pm, resource as r where pm.property_id = r.id and r.DTYPE = 'PROPERTY' and r.uri='"+uri+"';"; 
			
			
			Query query = session.createSQLQuery(queryString);
			
			List<Object[]> objs = query.list();
			for (Object[] obj : objs) {
				
				PatternMapping pm = new PatternMapping((String) obj[1], "", (String) obj[3], (String) obj[2]);
				pm.setId((Integer) obj[0]);
				objects.add(pm);
			}
			
			tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(tx);
			super.logger.error("Error...", he);
			he.printStackTrace();
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}
		return objects;
	}

	@Override
	public Entity createNewEntity() {

		throw new RuntimeException("Do not use this method!");
	}

	public void deleteAllPatternMappings() {

		Transaction tx = null;
		Session session = null;
		
		try {
			
			session = HibernateFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			String queryString = "delete from pattern_mapping;";			
			Query query = session.createSQLQuery(queryString);
			query.executeUpdate();
			
			queryString = "delete from pattern;";
			query = session.createSQLQuery(queryString);
			query.executeUpdate();
			
			queryString = "delete from pattern_learned_from;";
			query = session.createSQLQuery(queryString);
			query.executeUpdate();
			
			tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(tx);
			super.logger.error("Error...", he);
			he.printStackTrace();
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}// TODO Auto-generated method stub
		
	}

	public List<String> findPatternMappingsWithPatterns() {

		Session session = HibernateFactory.getSessionFactory().openSession();
    	String queryString = "select distinct(prop.uri) from pattern_mapping as pm, resource as prop, pattern_mapping_pattern as pmp, pattern as p  where pm.id = pmp.pattern_mapping_id and pmp.pattern_id = p.id and pm.property_id = prop.id order by prop.uri;";
        List<String> results = (List<String>) session.createSQLQuery(queryString).list();
        HibernateFactory.closeSession(session);
        return results;
	}
	
	public PatternMapping findPatternMappingByUri(String uri) {

		Session session = HibernateFactory.getSessionFactory().openSession();
    	List<PatternMapping> resourceList = session.createCriteria(PatternMapping.class)
    							.createCriteria("property").add(Restrictions.eq("uri", uri))
    							.list();
    	
    	HibernateFactory.closeSession(session);
    	
//    	if ( resourceList.size() > 0 ) return resourceList.get(0);
//    	else return null;
    	
        return this.findPatternMapping(resourceList.get(0).getId());
	}
	
	public Double findPatternMappingsWithSamePattern(String pattern) {
		
		String sqlQuery = 
				"select distinct (pattern_mapping.id) " +
				"from pattern, pattern_mapping, pattern_mapping_pattern " +
				"where " +
					"pattern_mapping_pattern.pattern_id = pattern.id " +
					"and pattern_mapping_pattern.pattern_mapping_id = pattern_mapping.id " +
					"and pattern.naturalLanguageRepresentation = :pattern ";
		Session session = HibernateFactory.getSessionFactory().openSession();
		
		Query query = session.createSQLQuery(sqlQuery);
		query.setString("pattern", pattern);
        Set<Integer> results = new HashSet<Integer>(query.list());
        HibernateFactory.closeSession(session);
        return Double.valueOf(results.size());
	}
}
