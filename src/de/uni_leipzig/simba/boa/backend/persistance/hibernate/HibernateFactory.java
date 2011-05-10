package de.uni_leipzig.simba.boa.backend.persistance.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.Cluster;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class HibernateFactory {
	
    private static SessionFactory sessionFactory;
    private static NLPediaLogger logger = new NLPediaLogger(HibernateFactory.class);

    /**
     * 
     * @return
     */
    public static SessionFactory getSessionFactory() {
    	
    	if ( HibernateFactory.sessionFactory == null ) {
    		
    		HibernateFactory.sessionFactory = new Configuration()
    											// Add classes 
									            .addAnnotatedClass(Pattern.class)
									            .addAnnotatedClass(PatternMapping.class)
									            .addAnnotatedClass(Cluster.class)
									            
									            // Add settings
										        .setProperty("hibernate.connection.driver_class", 	NLPediaSettings.getInstance().getSetting("hibernateConnectionDriverClass"))
										        .setProperty("hibernate.connection.url", 			NLPediaSettings.getInstance().getSetting("hibernateConnectionUrl"))
										        .setProperty("hibernate.connection.username", 		NLPediaSettings.getInstance().getSetting("hibernateConnectionUsername"))
										        .setProperty("hibernate.connection.password", 		NLPediaSettings.getInstance().getSetting("hibernateConnectionPassword"))
										        .setProperty("hibernate.dialect", 					NLPediaSettings.getInstance().getSetting("hibernateDialect"))
										        .setProperty("hibernate.hbm2ddl.auto",				NLPediaSettings.getInstance().getSetting("hibernateHbm2ddlAuto"))
										        .setProperty("hibernate.connection.autoReconnect", 	"true")
										        .buildSessionFactory();
    	}
        return sessionFactory;
    }
    
    public static void changeConnection(String database) {
    	
    	HibernateFactory.sessionFactory = new Configuration()
		// Add classes 
        .addAnnotatedClass(Pattern.class)
        .addAnnotatedClass(PatternMapping.class)
        .addAnnotatedClass(Cluster.class)
        
        // Add settings
        .setProperty("hibernate.connection.driver_class", 	"com.mysql.jdbc.Driver")
        .setProperty("hibernate.connection.url", 			"jdbc:mysql://127.0.0.1:3306/" + database)
        .setProperty("hibernate.connection.username", 		"root")
        .setProperty("hibernate.connection.password", 		"root")
        .setProperty("hibernate.dialect", 					"org.hibernate.dialect.MySQLDialect")
        .setProperty("hibernate.hbm2ddl.auto",				"update")
        .setProperty("hibernate.connection.autoReconnect", 	"true")
        .buildSessionFactory();
    }
    
    /**
     * 
     * @return
     */
    public static boolean checkConnection() {
    	
    	return sessionFactory.isClosed();	
    }
    
    /**
     * 
     * @return
     * @throws HibernateException
     */
    public static Session openSession() throws HibernateException {

    	if ( HibernateFactory.sessionFactory == null ) HibernateFactory.getSessionFactory();
    	
    	return sessionFactory.openSession();
    }
    
    /**
     * 
     */
    public static void closeSessionFactory() {
        
    	if ( HibernateFactory.sessionFactory != null ) {
    		
            try {
            	
            	HibernateFactory.sessionFactory.close();
            }
            catch (HibernateException he) {
            	
            	HibernateFactory.logger.error("Couldn't close SessionFactory", he);
            }
        }
    }

    /**
     * 
     * @param session
     */
    public static void closeSession(Session session) {
        
    	if ( session != null ) {
    		
            try {
            
            	session.close();
            }
            catch (HibernateException he) {
            	
            	HibernateFactory.logger.error("Couldn't close Session", he);
            }
        }
    }
    
    /**
     * 
     * @param tx
     */
    public static void rollback(Transaction tx) {
        
    	try {
            
    		if ( tx != null ) {
    			
                tx.rollback();
            }
        }
    	catch (HibernateException he) {
    		
    		HibernateFactory.logger.error("Couldn't rollback Transaction", he);
        }
    }
}
