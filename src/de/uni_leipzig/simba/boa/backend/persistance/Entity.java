package de.uni_leipzig.simba.boa.backend.persistance;

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

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
