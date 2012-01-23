package de.uni_leipzig.simba.boa.backend.rdf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import de.uni_leipzig.simba.boa.backend.Constants;

/**
 * 
 * @author Daniel Gerber
 */
public class Model {

	private com.hp.hpl.jena.rdf.model.Model model;
	
	public Model (com.hp.hpl.jena.rdf.model.Model model) {
		
		this.model = model;
	}
	
	/**
	 * @param statement the statement to add
	 */
	public void addStatement(Statement statement) {
		
		this.model.add(statement);
	}
	
	/**
	 * This method removes all statements from this model
	 */
	public void emptyModel() {
		
		this.model.removeAll();
	}

	/**
	 * @return the size of the model
	 */
	public long getNumberOfStatements() {
		
		return this.model.size();
	}
	
	/**
	 * @param uri for the resource
	 * @return a new resource with the uri
	 */
	public RDFNode createResource(String uri) {
		
		return this.model.createResource(uri);
	}
	
	/**
	 * @param uri for the property
	 * @return a new property with the uri
	 */
	public Property createProperty(String uri) {
		
		return this.model.createProperty(uri);
	}
	
	/**
	 * Create a triple and return it, the triple is not added to this model.
	 * In this case the object is a literal. 
	 * 
	 * @param subject
	 * @param property
	 * @param object
	 * @return the statement or triple
	 */
	public Statement createStatement(Resource subject, Property property, String object) {
		
		return this.model.createStatement(subject, property, object);
	}
	
	/**
	 * Create a triple and return it, the triple is not added to this model.
	 * In this case the object is an other resource. 
	 * 
	 * @param subject
	 * @param property
	 * @param object
	 * @return the statement or triple
	 */
	public Statement createStatement(RDFNode subject, Property property, RDFNode object) {
		
		return this.model.createStatement((Resource) subject, property, object);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {

		Iterator nsIterator = this.model.listNameSpaces();
		Iterator statementIterator = this.model.listStatements();
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("Available namespaces: " + Constants.NEW_LINE_SEPARATOR);
		while (nsIterator.hasNext()) {
			
			builder.append(" - " + nsIterator.next() + Constants.NEW_LINE_SEPARATOR);
		}
		
		builder.append(Constants.NEW_LINE_SEPARATOR + "Available statements: " + Constants.NEW_LINE_SEPARATOR);
		while (statementIterator.hasNext()) {
			
			builder.append(statementIterator.next() + Constants.NEW_LINE_SEPARATOR);
		}
		
		return builder.toString();
	}

	/**
	 * Sets the model but does not save it to the database;
	 * 
	 * @param model
	 */
	public void setModel(com.hp.hpl.jena.rdf.model.Model model) {

		this.model = model;
	}

	/**
	 * 
	 * @return
	 */
	public com.hp.hpl.jena.rdf.model.Model getModel() {

		return this.model;
	}

	/**
	 * Add a list of statements to this model and to the database.
	 * 
	 * @param list
	 */
	public void addStatements(List<Statement> statementList) {

		this.model.add(statementList);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Statement> getStatements() {
		
		return this.model.listStatements().toList();
	}

	/**
	 * @param string
	 * @param nTriple
	 */
	public void fileExport(String pathToFile, String language) {

		try {
			
			Writer out = new PrintWriter(new BufferedWriter(new FileWriter(pathToFile)));
			this.model.write(out, language);
		}
		catch (IOException e) {

			e.printStackTrace();
		}
	}
}
