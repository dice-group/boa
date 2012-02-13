package de.uni_leipzig.simba.boa.backend.dao;

import java.util.HashMap;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * Singleton - initialized by the spring framework
 * 
 * Use this factory to create DAOs.
 * 
 * The create a new Dao just use the createDAO() method by referring the
 * Dao interface. The factory will return the specific implementation of this Dao interface.
 * 
 * Available implementations of the DAOs are specified in the jawa_extensions.xml file.
 */
public class DaoFactory {
	
	private static DaoFactory INSTANCE;
	
	private Map<String, AbstractDao> daos;
	
	private final NLPediaLogger log;
	
	/**
	 * Singleton
	 */
	private DaoFactory() {
		
		this.log = new NLPediaLogger(DaoFactory.class);
		this.daos = new HashMap<String, AbstractDao>();
	}
	
	/**
	 * @return the instance of this singleton
	 */
	public static DaoFactory getInstance() {
		
		if ( INSTANCE == null ) {
			
			INSTANCE = new DaoFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * @return the daos
	 */
	public Map<String, AbstractDao> getDaos() {
	
		return this.daos;
	}
	
	/**
	 * [used by spring]
	 * 
	 * Sets the available DAOs specified in the jawa_setup.xml file.
	 * @param daos - list of available DAOs
	 */
	public void setDaos(Map<String, AbstractDao> daoList) {
		
		this.daos = daoList;
	}
	
	/**
	 * Returns a new instance of the specified Dao interface.
	 * The specific implementations of the DAOs have to be declared
	 * in the dao_config.xml file.
	 * 
	 * @param daoClazz - the Dao interface to be instantiated
	 * @return the instantiated Dao or null if could not be found
	 */
	@SuppressWarnings("unchecked")
	public AbstractDao createDAO(Class daoClazz) {

		AbstractDao dao = this.daos.get(daoClazz.getName());
		if ( dao == null ) {
			
			this.log.debug("Can't create requested dao: " + daoClazz.getName());
		}
		return dao;
	}
}