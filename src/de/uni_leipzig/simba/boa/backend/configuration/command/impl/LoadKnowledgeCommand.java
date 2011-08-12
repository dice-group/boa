package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;


public class LoadKnowledgeCommand implements Command {

	private List<Triple> tripleList = new ArrayList<Triple>();
	
	public static void main(String[] args) {

		NLPediaSetup s = new NLPediaSetup(false);
		LoadKnowledgeCommand c = new LoadKnowledgeCommand();
		c.execute();
	}
	
	@Override
	public void execute() {
		
		System.out.println("Starting to load background knowledge into database!");
		long start = new Date().getTime();
		
		TripleDao tripleDao		= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);

		List<String[]> labels =  RelationFinder.getRelationFromFile("");
		Map<String,Resource> resourceMap = new HashMap<String, Resource>();
		
		int i = 0;
		
		for ( String[] line : labels ) {
			
//			if (i++==1000) break;
			
			String subjectUri		= "";
			String subjectLabel		= "";
			String subjectType		= "";
			String objectUri		= "";
			String objectLabel		= "";
			String objectType		= "";
			String predicate		= line[2];
			String range			= line[5];
			String domain			= line[6];
			
			boolean isSubject		= line[7].equals("isSubject") ? true : false;
			
			if ( isSubject ) { 
				
				subjectUri 		= line[0];
				subjectLabel	= line[1];
				objectUri		= line[3];
				objectLabel		= line[4];
				subjectType		= domain;
				objectType		= range;
			}
			else {
				
				subjectUri 		= line[3];
				subjectLabel	= line[4];
				objectUri		= line[0];
				objectLabel		= line[1];
				subjectType		= range;
				objectType		= domain;
			}
			
			// create the resource: subject if not found
			Resource sub = resourceMap.get(subjectUri);
			if ( sub == null ) {
				
				sub = new Resource();
				sub.setUri(subjectUri);
				sub.setLabel(subjectLabel);
				sub.setType(subjectType);
				resourceMap.put(subjectUri, sub);
			}
			
			// create the property if not found
			Property p = (Property) resourceMap.get(predicate); 
			if ( p == null ) {
				
				p = new Property();
				p.setUri(predicate);
				p.setRdfsDomain(domain);
				p.setRdfsRange(range);
				p.setLabel(StringUtils.join(predicate.replace("http://dbpedia.org/ontology/", "").split("(?=\\p{Upper})"), " ").toLowerCase());
				resourceMap.put(predicate, p);
			}
			
			// create the resource: object if not found
			Resource obj = resourceMap.get(objectUri);
			if ( obj == null ) {
				
				obj = new Resource();
				obj.setUri(objectUri);
				obj.setLabel(objectLabel);
				obj.setType(objectType);
				resourceMap.put(objectUri, obj);
			}
			
			// create and save the triple
			Triple triple = new Triple();
			triple.setLearnedInIteration(0);
			triple.setCorrect(true);
			triple.setSubject(sub);
			triple.setProperty(p);
			triple.setObject(obj);
			tripleList.add(triple);
		}
		System.out.println("Starting to batch save triples to database!");
		tripleDao.batchSaveOrUpdate(tripleList);
		
		System.out.println("Loading background knowledge took " + (new Date().getTime() - start) + "ms.");
	}
	
	public List<Triple> getTriples(){
		
		return this.tripleList;
	}
}
