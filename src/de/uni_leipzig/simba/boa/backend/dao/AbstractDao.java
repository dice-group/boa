package de.uni_leipzig.simba.boa.backend.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.Entity;
import de.uni_leipzig.simba.boa.backend.persistance.hibernate.HibernateFactory;

/**
 * 
 * @author Daniel Gerber
 */
public abstract class AbstractDao {

	protected Session session;
	private Transaction tx;
	
	protected String name;
	
	protected NLPediaLogger logger = new NLPediaLogger(AbstractDao.class);

	/**
     * 
     */
	public AbstractDao() {

		// Initialize SessionFactory
		HibernateFactory.getSessionFactory();
	}
	
	/**
	 * 
	 * @param entity
	 */
	protected Entity saveOrUpdateEntity(Entity entity) {

		try {

			this.startOperation();
			entity.setLastModified(new Date());
			this.session.saveOrUpdate(entity);
			this.tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(this.tx);
			this.logger.error("Could not save entity with id: " + entity.getId(), he);
		}
		finally {
			
			HibernateFactory.closeSession(this.session);
		}
		
		return entity;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract Entity createNewEntity();
		
	
	/**
	 * 
	 * @param entity
	 */
	protected void deleteEntity(Entity entity) {

		try {
			
			this.startOperation();
			this.session.delete(entity);
			this.tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(this.tx);
			this.logger.error("Could not delete entity with id: " + entity.getId(), he);
		}
		finally {
			
			HibernateFactory.closeSession(this.session);
		}
	}

	/**
	 * 
	 * @param clazz
	 * @param id
	 * @return
	 */
	protected Entity findEntityById(Class clazz, int id) {

		Entity obj = null;
		try {
			
			this.startOperation();
			obj = (Entity) this.session.load(clazz, id);
			this.tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(this.tx);
			this.logger.error("Could not find entity with id: " + id, he);
		}
		finally {
			
			HibernateFactory.closeSession(session);
		}
		return obj;
	}

	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	protected List findAllEntitiesByClass(Class clazz) {

		List<Entity> objects = null;
		
		try {
			
			this.startOperation();
			Query query = this.session.createQuery("from " + clazz.getName());
			objects = query.list();
			this.tx.commit();
		}
		catch (HibernateException he) {
			
			HibernateFactory.rollback(this.tx);
			this.logger.error("Could not find all entities of type: " + clazz.getName(), he);
		}
		finally {
			
			HibernateFactory.closeSession(this.session);
		}
		return objects;
	}

	/**
	 * 
	 * @throws HibernateException
	 */
	protected void startOperation() throws HibernateException {

		this.session = HibernateFactory.openSession();
		this.tx = session.beginTransaction();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		
		return this.getName();
	}
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		
		this.name = name;
	}
}