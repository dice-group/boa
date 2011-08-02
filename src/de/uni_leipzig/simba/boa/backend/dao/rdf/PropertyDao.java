package de.uni_leipzig.simba.boa.backend.dao.rdf;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.persistance.Entity;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;

public class PropertyDao extends AbstractDao {

	public PropertyDao() {
        super();
    }

	/**
	 * 
	 * @param resource
	 * @return 
	 */
    public Property createAndSaveProperty(Property property) {
    	
        return (Property) super.saveOrUpdateEntity(property);
    }
    
    /**
     */
    public Property createNewEntity() {

    	return (Property) super.saveOrUpdateEntity(new Property());
	}
    
    /**
     * 
     * @param property
     */
    public void deleteProperty(Property property) {
    	
        super.deleteEntity(property);
    }

    /**
     * 
     * @param id
     * @return
     */
    public Property findProperty(int id) {
    	
        return (Property) super.findEntityById(Property.class, id);
    }

    /**
     * 
     * @param property
     */
    public void updateProperty(Property property) {
    	
        super.saveOrUpdateEntity(property);
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<Property> findAllProperties() {
    	
        return (List<Property>) super.findAllEntitiesByClass(Property.class);
    }
}
