package de.uni_leipzig.simba.boa.backend.configuration.command.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.configuration.command.Command;
import de.uni_leipzig.simba.boa.backend.crawl.RelationFinder;
import de.uni_leipzig.simba.boa.backend.dao.DaoFactory;
import de.uni_leipzig.simba.boa.backend.dao.rdf.TripleDao;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Resource;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.wordnet.query.WordnetQuery;


public class LoadKnowledgeCommand implements Command {

	private Map<Integer,Triple> tripleHashToTriple = new HashMap<Integer,Triple>();
	private List<Triple> triples = new ArrayList<Triple>();
//	static {
//		
//		allowedProperties = Arrays.asList("http://dbpedia.org/ontology/crosses","http://dbpedia.org/ontology/locatedInArea","http://dbpedia.org/ontology/manager",
//				"http://dbpedia.org/ontology/creator","http://dbpedia.org/ontology/operator","http://dbpedia.org/ontology/unitaryAuthority",
//				"http://dbpedia.org/ontology/notableCommander","http://dbpedia.org/ontology/capital","http://dbpedia.org/ontology/ceremonialCounty",
//				"http://dbpedia.org/ontology/place","http://dbpedia.org/ontology/foundationPerson","http://dbpedia.org/ontology/hubAirport",
//				"http://dbpedia.org/ontology/child","http://dbpedia.org/ontology/cinematography","http://dbpedia.org/ontology/architect",
//				"http://dbpedia.org/ontology/influencedBy","http://dbpedia.org/ontology/regionServed","http://dbpedia.org/ontology/garrison",
//				"http://dbpedia.org/ontology/commandStructure","http://dbpedia.org/ontology/leftTributary","http://dbpedia.org/ontology/rightTributary",
//				"http://dbpedia.org/ontology/mother","http://dbpedia.org/ontology/militaryUnit","http://dbpedia.org/ontology/recordPlace",
//				"http://dbpedia.org/ontology/father","http://dbpedia.org/ontology/tenant","http://dbpedia.org/ontology/musicComposer",
//				"http://dbpedia.org/ontology/network","http://dbpedia.org/ontology/sisterStation","http://dbpedia.org/ontology/guest",
//				"http://dbpedia.org/ontology/managerClub","http://dbpedia.org/ontology/leaderName","http://dbpedia.org/ontology/nearestCity",
//				"http://dbpedia.org/ontology/publisher","http://dbpedia.org/ontology/author","http://dbpedia.org/ontology/coachedTeam",
//				"http://dbpedia.org/ontology/spouse","http://dbpedia.org/ontology/affiliation","http://dbpedia.org/ontology/ground",
//				"http://dbpedia.org/ontology/riverMouth","http://dbpedia.org/ontology/musicalArtist","http://dbpedia.org/ontology/musicalBand",
//				"http://dbpedia.org/ontology/award","http://dbpedia.org/ontology/writer","http://dbpedia.org/ontology/almaMater",
//				"http://dbpedia.org/ontology/occupation","http://dbpedia.org/ontology/formerTeam","http://dbpedia.org/ontology/deathPlace",
//				"http://dbpedia.org/ontology/birthPlace","http://dbpedia.org/ontology/trainer");
//	}
	
//	public static void main(String[] args) {

//		NLPediaSetup s = new NLPediaSetup(false);
//		LoadKnowledgeCommand c = new LoadKnowledgeCommand();
//		c.execute();
//	}
	
	@Override
	public void execute() {
		
		System.out.println("Starting to load background knowledge into database!");
		long start = new Date().getTime();
		
		TripleDao tripleDao		= (TripleDao) DaoFactory.getInstance().createDAO(TripleDao.class);

		List<String[]> labels =  RelationFinder.getRelationFromFile(NLPediaSettings.getInstance().getSetting("labelOutputFile"));
		
		System.out.println(String.format("There are %s strings in the LoadKnowledgeCommand from RelationFinder!", labels.size()));
		
		Map<String,Resource> resourceMap = new HashMap<String, Resource>();
		
		Pattern pattern = Pattern.compile("\\(.+?\\)");
	    Matcher matcher;

		// 0_URI1 ||| 1_LABEL1 ||| 2_LABELS1 ||| 3_PROP ||| 4_URI2 ||| 5_LABEL2 ||| 6_LABELS2 ||| 7_RANGE ||| 8_DOMAIN
		for ( String[] line : labels ) {
			
			// all subject information
			String subjectUri		= "";
			String subjectLabel		= "";
			String subjectLabels	= "";
			String subjectContext	= "";
			String subjectType		= "";
			
			// all object information
			String objectUri		= "";
			String objectLabel		= "";
			String objectLabels		= "";
			String objectContext	= "";
			String objectType		= "";
			
			// all predicate information
			String predicate		= line[3];
			String predicateType	= line[4].startsWith("http://") ? "owl:ObjectProperty" : "owl:DatatypeProperty";
			String range			= line[7];
			String domain			= line[8];
			
			// ################ SUBJECT ############################
			
			// uri of the subject
			subjectUri 		= line[0];
			// context like person: heist_(artist)
		    matcher = pattern.matcher(line[1]);
		    while (matcher.find()) { subjectContext = matcher.group(); }
		    // subject label without text in brackets
			subjectLabel	= line[1].replaceAll("\\(.+?\\)", "").trim();
			// labels from wikipedia surface forms
			subjectLabels	= line[2];
			// rdf:type of the subject
			subjectType		= domain;
			
			// ################ OBJECT ############################
			
			// uri of the object
			objectUri		= line[4];
			// context like person: heist_(artist)
		    matcher			= pattern.matcher(line[5]);
		    while (matcher.find()) { objectContext = matcher.group(); }
			// object label without text in brackets
		    objectLabel		= line[5].replaceAll("\\(.+?\\)", "").trim();
			// labels from wikipedia surface forms
			objectLabels	= line[6];
			// rdf:type of the object
			objectType		= range;
			
			// ################ resources: subject, property, object ############################
			
			// create the resource: subject if not found
			Resource sub = resourceMap.get(subjectUri);
			if ( sub == null ) {
				
				sub = new Resource();
				sub.setUri(subjectUri);
				sub.setLabel(subjectLabel);
				sub.setSurfaceForms(subjectLabels.toLowerCase());
				sub.setType(subjectType);
				if ( subjectContext.length() > 0 ) {
					sub.setContext(subjectContext.substring(1, subjectContext.length()-1));	
				}
				resourceMap.put(subjectUri, sub);
			}
			
			// create the property if not found
			Property p = (Property) resourceMap.get(predicate); 
			if ( p == null ) {
				
				p = new Property();
				p.setUri(predicate);
				p.setRdfsDomain(domain);
				p.setRdfsRange(range);
				p.setType(predicateType);
				p.setLabel(StringUtils.join(predicate.replace("http://dbpedia.org/ontology/", "").split("(?=\\p{Upper})"), " ").toLowerCase());
				p.setSynsets(StringUtils.join(WordnetQuery.getSynsetsForAllSynsetTypes(p.getLabel()), ","));
				resourceMap.put(predicate, p);
			}
			
			// create the resource: object if not found
			Resource obj = resourceMap.get(objectUri);
			if ( obj == null ) {
				
				obj = new Resource();
				obj.setType(objectType);
				
				// object properties have there own labels
				if ( predicateType.equals("owl:ObjectProperty") ) {
					
					obj.setUri(objectUri);
					obj.setLabel(objectLabel);
					obj.setSurfaceForms(objectLabels.toLowerCase());
					// only resources have context information
					if ( objectContext.length() > 0 ) {
						obj.setContext(objectContext.substring(1, objectContext.length()-1));
					}
				}
				// for datatype properties we need to create some surface forms
				else {
					
					// they dont have uris so create random strings
					obj.setUri(UUID.randomUUID().toString());
					obj.setLabel(objectLabel);
					obj.setSurfaceForms(this.createDatatypePropertyLabels(predicateType, objectLabel));
				}
				resourceMap.put(objectUri, obj);
			}
			
			// create and save the triple
			Triple triple = new Triple();
			triple.setCorrect(true);
			triple.setSubject(sub);
			triple.setProperty(p);
			triple.setObject(obj);
			
			tripleHashToTriple.put(triple.hashCode(),triple);
		}
		System.out.println(String.format("Starting to batch save %s triples to database!", tripleHashToTriple.size()));
		
		this.triples = new ArrayList<Triple>(tripleHashToTriple.values());
		tripleDao.batchSaveOrUpdate(this.triples);
		
		System.out.println("Loading background knowledge took " + (new Date().getTime() - start) + "ms.");
	}
	
	private String createDatatypePropertyLabels(String predicateType, String objectLabel) {

		StringBuffer labels = new StringBuffer();
		
		if ( predicateType.equals("http://www.w3.org/2001/XMLSchema#date") ) {
			
			try {
				
				String d = getDateString("2011-07-11", "yyyy-MM-dd", "d");
				String MM = getDateString("2011-07-11", "yyyy-MM-dd", "MM");
				String MMM = getDateString("2011-07-11", "yyyy-MM-dd", "MMM");
				String MMMM = getDateString("2011-07-11", "yyyy-MM-dd", "MMMM");
				String yy = getDateString("2011-07-11", "yyyy-MM-dd", "yy");
				String yyyy = getDateString("2011-07-11", "yyyy-MM-dd", "yyyy");
				
				labels.append(d + getOrdinalFor(Integer.valueOf(d)) + " of " + MMMM + " " + yyyy).append("_&_").
				append(d + " " + MMMM + " " + yyyy).append("_&_").
				append(d + "." + MM + "." + yyyy).append("_&_").
				append(d + "." + MM + "." + yy).append("_&_").
				append(d + " " + MMM + " " + yyyy).append("_&_").
				append(d + " " + MMMM  + " '" + yy).append("_&_").
				append(d + " " + MMM  + " '" + yy).append("_&_").
				append(MMMM + " " + yyyy).append("_&_").
				append(MMM + " " + yyyy).append("_&_").
				append(MMMM).append("_&_").
				append(yyyy);
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return labels.toString();
	}

	public List<Triple> getTriples(){

		return this.triples;
	}
	
	public static void main(String[] args) throws ParseException {

	}
	
	private static String getDateString(String dateString, String fromPattern, String toPattern) throws ParseException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fromPattern);
		Date date1 = simpleDateFormat.parse(dateString);
		simpleDateFormat.applyPattern(toPattern);
		return simpleDateFormat.format(date1);
	}
	
	public static String getOrdinalFor(int value) {
		 int hundredRemainder = value % 100;
		 int tenRemainder = value % 10;
		 if(hundredRemainder - tenRemainder == 10) {
		  return "th";
		 }
		 
		 switch (tenRemainder) {
		  case 1:
		   return "st";
		  case 2:
		   return "nd";
		  case 3:
		   return "rd";
		  default:
		   return "th";
		 }
		}
}
