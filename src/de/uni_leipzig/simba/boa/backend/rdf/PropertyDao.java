package de.uni_leipzig.simba.boa.backend.rdf;

import java.util.Iterator;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import de.uni_leipzig.simba.boa.backend.Constants;


public class PropertyDao {

	private Model model;

	public PropertyDao(Model model) {
		
		this.model = model;
	}
	
	/**
	 * Creates a property with the given attributes. All attributes are saved to the
	 * database. 
	 * 
	 * @param uri
	 * @param type
	 * @param label
	 * @param domain
	 * @param range
	 * @return
	 */
	public Property createAndSaveProperty(String uri, String type, String label, String domain, String range){
		
		Property property = new Property(uri);
		RDFNode propertyResource = this.model.createResource(uri);
		
		if ( type != null && !type.isEmpty() ) {

			// save the type of the property
			property.setType(type);
			com.hp.hpl.jena.rdf.model.Property res2 = this.model.createProperty(Constants.RDF_TYPE);
			RDFNode res3 = this.model.createResource(type);
			this.model.addStatement(this.model.createStatement(propertyResource, res2, res3));

		}
		if ( label != null && !label.isEmpty() ) {
		
			// create the label for the property
			property.setLabel(label);
			com.hp.hpl.jena.rdf.model.Property labelProperty = this.model.createProperty(Constants.RDFS_LABEL);
			this.model.addStatement(this.model.createStatement((Resource) propertyResource, labelProperty, label));
		}
		if ( domain != null && !domain.isEmpty() ) {
			
			// create the domain for the property			
			property.setDomain(domain);
			com.hp.hpl.jena.rdf.model.Property rangeProperty = this.model.createProperty(Constants.RDFS_RANGE);
			RDFNode rangeResource = this.model.createResource(range);
			this.model.addStatement(this.model.createStatement(propertyResource, rangeProperty, rangeResource));
		}
		if ( range != null && !range.isEmpty() ) {
			
			// create the domain for the property			
			property.setRange(range);
			com.hp.hpl.jena.rdf.model.Property domainProperty = this.model.createProperty(Constants.RDFS_DOMAIN);
			RDFNode domainResource = this.model.createResource(domain);
			this.model.addStatement(this.model.createStatement(propertyResource, domainProperty, domainResource));
		}
		return property;
	}
	
	public Property findPropertyByUriWithDomainRangeLabelType(String uri) {
		
		String queryString = 
			"SELECT * " +
			"WHERE {" +
			"	<"+ uri + "> <http://www.w3.org/2000/01/rdf-schema#domain> ?domain . " +
			"	<"+ uri + "> <http://www.w3.org/2000/01/rdf-schema#range> ?range . " +
			"	<"+ uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?label . " +	
			"	<"+ uri + "> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type . " +	
			"}";
		
		System.out.println(queryString);
		
		QueryExecution qexec = QueryExecutionFactory.create(queryString, model.getModel());
		
		Iterator<QuerySolution> resultsIterator = qexec.execSelect() ;
	    while (resultsIterator.hasNext()) {
	        QuerySolution solution = resultsIterator.next();
	        
	        RDFNode domain = solution.get("domain");
	        RDFNode range = solution.get("range");
	        RDFNode type = solution.get("type");
	        String label = solution.get("label").toString();
	        
	        Property property = new Property(uri);
	        property.setDomain(domain.toString());
	        property.setRange(range.toString());
	        property.setType(type.toString());
	        property.setLabel(label);
	        
	        return property;
	    }
		return null;
	}
}
