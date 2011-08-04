package de.uni_leipzig.simba.boa.backend.dao.pattern;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
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
     * return empty pattern
     */
    public Pattern createNewEntity() {

    	return (Pattern) super.saveOrUpdateEntity(new Pattern());
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

    public void batchSaveOrUpdatePattern(List<Pattern> pattern) {
    	
    	Session session = HibernateFactory.getSessionFactory().openSession();
    	Transaction tx = session.beginTransaction();
    	int batchSize = Integer.valueOf(NLPediaSettings.getInstance().getSetting("hibernate.jdbc.batch_size"));
    	   
    	for ( int i = 0; i < pattern.size() ; i++ ) {
    	    
    	    session.saveOrUpdate(pattern.get(i));
    	    
    	    if ( i % batchSize == 0 ) { 
    	        //flush a batch of inserts and release memory:
    	        session.flush();
    	        session.clear();
    	    }
    	}
    	   
    	tx.commit();
    	session.close();
    }
    
    /**
     * 
     * @return
     */
    public List<Pattern> findAllPatterns() {
    	
        return (List<Pattern>) super.findAllEntitiesByClass(Pattern.class);
    }
    
	public int countPatternMappingsWithSameNaturalLanguageRepresenation(String naturalLanguageRepresentation) {

		Session session = null;
		Transaction tx = null;
		
		BigInteger numberOfPatternMappings = BigInteger.valueOf(0);
		
		try {
			
			session = HibernateFactory.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			String sql = "select count(distinct(patternMapping_id)) from pattern where naturalLanguageRepresentation=:naturalLanguageRepresentation";
			SQLQuery query = session.createSQLQuery(sql);
			query.setString("naturalLanguageRepresentation", naturalLanguageRepresentation);
			
			   List<BigInteger> listCounter = (List<BigInteger>)query.list();
			   if (!listCounter.isEmpty()) {
				   numberOfPatternMappings = listCounter.get(0);
			   }
			
			tx.commit();
		}
		catch (HibernateException he) {
			
			he.printStackTrace();
			HibernateFactory.rollback(tx);
			logger.error("Could not find pattern with naturalLanguageRepresentation: " + naturalLanguageRepresentation, he);
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}
		return numberOfPatternMappings.intValue();
	}
}

