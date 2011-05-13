package de.uni_leipzig.simba.boa.backend.rdf.store;

import java.sql.SQLException;


import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.db.ModelRDB;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.DoesNotExistException;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;


public class Store {
	
	private final NLPediaLogger logger = new NLPediaLogger(Store.class);
	
	private static final String JENA_DB_URL 		= "jdbc:mysql://127.0.0.1:3306/en_wiki_rdf";
	private static final String JENA_DB_USERNAME	= "root";
	private static final String JENA_DB_PASSWORD	= "root";
	private static final String JENA_DB_TYPE		= "MySQL";

	public Store() {}
	
	/**
	 * Checks if a model is available 
	 * 
	 * @param modelName
	 * @return
	 */
	public boolean isModelAvailable(String modelName) {
		
		try {
			
			Class.forName ("com.mysql.jdbc.Driver");
			
			// Create database connection
			IDBConnection conn = new DBConnection ( Store.JENA_DB_URL, Store.JENA_DB_USERNAME, Store.JENA_DB_PASSWORD, Store.JENA_DB_TYPE);
			
			boolean modelAvailable = conn.containsModel(modelName);
			
			// Close the database connection
			conn.close();
			
			return modelAvailable;		
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public de.uni_leipzig.simba.boa.backend.rdf.Model createModelIfNotExists(String modelName){
		
		Model model = null;
		
		try {
			
			Class.forName ("com.mysql.jdbc.Driver");
			
			// Create database connection
			IDBConnection conn = new DBConnection ( Store.JENA_DB_URL, Store.JENA_DB_USERNAME, Store.JENA_DB_PASSWORD, Store.JENA_DB_TYPE);
			
			if( !conn.containsModel(modelName) ) {
				
				model = ModelRDB.createModel(conn, modelName);
				this.logger.info("Created model: " + modelName);
			}
			else {
				
				model = ModelRDB.open(conn, modelName);
				this.logger.info("Opened model: " + modelName);
			}
					
			// Close the database connection
			conn.close();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		de.uni_leipzig.simba.boa.backend.rdf.Model newModel = new de.uni_leipzig.simba.boa.backend.rdf.Model(model);
		
		return newModel;
	}

	/**
	 * @param modelName - the name of the model
	 * @return the model or null if model was not found
	 */
	public de.uni_leipzig.simba.boa.backend.rdf.Model getModel(String modelName) {

		de.uni_leipzig.simba.boa.backend.rdf.Model model;
		
		try {
			
			IDBConnection conn = new DBConnection ( Store.JENA_DB_URL, Store.JENA_DB_USERNAME, Store.JENA_DB_PASSWORD, Store.JENA_DB_TYPE);
			model = new de.uni_leipzig.simba.boa.backend.rdf.Model(ModelRDB.open(conn, modelName));
		}
		catch (DoesNotExistException dnee) {
			
			// we could not found a model so return null
			model = null;
		}
		return model;
	}
	
	/**
	 * This deletes all data from the database!! Use it with care!
	 * @see http://jena.sourceforge.net/javadoc/com/hp/hpl/jena/db/IDBConnection.html#cleanDB()
	 */
	public void dropDatabase() {
		
		try {
			
			new DBConnection(Store.JENA_DB_URL, Store.JENA_DB_USERNAME, Store.JENA_DB_PASSWORD, Store.JENA_DB_TYPE).cleanDB();
		}
		catch (SQLException e) {
			
			e.printStackTrace();
			this.logger.fatal("Could not clean rdf database", e);
		}
	}

	/**
	 * Removes a model from the database.
	 * 
	 * @param modelName - the name of the model to be deleted
	 */
	public void removeModel(String modelName) {
		
		IDBConnection conn = new DBConnection ( Store.JENA_DB_URL, Store.JENA_DB_USERNAME, Store.JENA_DB_PASSWORD, Store.JENA_DB_TYPE);
		ModelRDB.open(conn, modelName).remove();
	}
}
