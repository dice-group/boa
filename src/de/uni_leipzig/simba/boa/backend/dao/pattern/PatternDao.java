package de.uni_leipzig.simba.boa.backend.dao.pattern;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;


/**
 * 
 * @author Daniel Gerber
 */
public class PatternDao extends AbstractDao {
	
    public PatternDao() {
        super();
    }

	/**
	 * 
	 * @param pattern
	 * @return 
	 */
    public Pattern createAndSavePattern(Pattern pattern) {
    	
        return (Pattern) super.saveOrUpdateEntity(pattern);
    }
    
    /**
     * return null!!!!
     */
    public Pattern createNewEntity() {

    	new RuntimeException("dont use this constructor!");
		return null;
	}

    /**
     * 
     * @param pattern
     */
    public void deletePattern(Pattern pattern) {
    	
        super.deleteEntity(pattern);
    }

    /**
     * 
     * @param id
     * @return
     */
    public Pattern findPattern(int id) {
    	
        return (Pattern) super.findEntityById(Pattern.class, id);
    }

    /**
     * 
     * @param pattern
     */
    public void updatePattern(Pattern pattern) {
    	
        super.saveOrUpdateEntity(pattern);
    }

    /**
     * 
     * @return
     */
    public List<Pattern> findAllPatterns() {
    	
        return (List<Pattern>) super.findAllEntitiesByClass(Pattern.class);
    }
    
//    public List<Pattern> findUniquePatterns() {
//    	
//    	List<Pattern> objects = null;
//		
//		Session session = null;
//		Transaction tx = null;
//		
//		try {
//			
//			session = HibernateFactory.getSessionFactory().openSession();
//			tx = session.beginTransaction();
//
//			Query query = session.createSQLQuery("");
//			
//			objects = uniqueCriteria.list();
//			
//			tx.commit();
//		}
//		catch (HibernateException he) {
//			
//			HibernateFactory.rollback(tx);
//			logger.error("Could not find pattern with naturalLanguageRepresentation: " + naturalLanguageRepresentation, he);
//		}
//		finally {
//			
//			HibernateFactory.closeSession(session);
//		}
//		return objects;
//    }
    
    /**
     * 
     * @param naturalLanguageRepresentation
     * @return
     */
    public List<Pattern> findPatternByNaturalLanguageRepresentation(String naturalLanguageRepresentation) {

		List<Pattern> objects = null;
		
		Session session = null;
		Transaction tx = null;
		
		try {
			
			session = HibernateFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			Query query = session.createSQLQuery("select * from Pattern_naturalLanguageRepresentationByLanguage where naturalLanguageRepresentationByLanguage=:naturalLanguageRepresentation");
			query.setString("naturalLanguageRepresentation", naturalLanguageRepresentation);
			objects = query.list();
			
			tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(tx);
			logger.error("Could not find pattern with naturalLanguageRepresentation: " + naturalLanguageRepresentation, he);
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}
		return objects;
	}
}

