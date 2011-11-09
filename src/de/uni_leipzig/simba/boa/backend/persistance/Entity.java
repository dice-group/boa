package de.uni_leipzig.simba.boa.backend.persistance;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 
 * @author Daniel Gerber
 */
@MappedSuperclass
public class Entity {

	protected int id;
//	protected final Date creationDate = new Date();
//	protected Date lastModified = new Date();

	/**
	 * @return the creationDate
	 */
//	public void setCreationDate(Date date) {
//	
//		// dont change anything
//	}
	
	/**
	 * @return the creationDate
	 */
//	@Basic
//	public Date getCreationDate() {
//	
//		return creationDate;
//	}

	/**
	 * @return the lastModified
	 */
//	@Basic
//	public Date getLastModified() {
//	
//		return lastModified;
//	}

	
	/**
	 * @param lastModified the lastModified to set
	 */
//	public void setLastModified(Date lastModified) {
//	
//		this.lastModified = lastModified;
//	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public int getId() {
	
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
	
		this.id = id;
	}
}
