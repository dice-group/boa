//package de.uni_leipzig.simba.boa.backend.entity.context;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import nlpbox.nlpbox.NLPBox;
//import nlpbox.util.Entity;
//import edu.stanford.nlp.util.StringUtils;
//
//public class LeftFoxContext extends LeftContext {
//
//	private List<Entity> entites;
//
//	public LeftFoxContext(String nerTaggedString, String sentence, String patternWithOutVariables, List<Entity> entities) {
//		super(nerTaggedString, sentence, patternWithOutVariables);
//		
//		this.entites = entities;
//	}
//
//	@Override
//	public String getSuitableEntity(String entityType) {
//
//		String toFindEntitiesIn = (this.buildLeftString() + " ").replaceAll("_\\S* ", " ");
//		System.out.println(toFindEntitiesIn);
//		
//		List<String> entities = new ArrayList<String>();
//		
//		for ( Entity entity : this.entites ) {
//			
//			System.out.println(entity.text);
//			
//			if ( toFindEntitiesIn.contains(entity.text) ) {
//				
//				System.out.println(true);
//				
//				// type is something like ORG,LOC,Per
//				String type = Context.namedEntityRecognitionMappings.get(entityType);
//				
//				System.out.println("type: " + type);
//				
//				// entity type is something like Organisation, Person, Location
//				String s = Context.nerToolClassMappings.get(type);
//				
//				System.out.println("entity type : " +  entity.type);
//				System.out.println("s: " + s);
//				
//				if ( entity.type.equals(s) ) {
//					
//					System.out.println(12123); 
//					entities.add(entity.text);
//				}
//			}
//		}
//		return "";//entities.get(entites.size() - 1);
//	}
//	
//	private String buildLeftString(){
//		
//		// list for the copy of the cleanWords and reverse the order because its leftcontext and we want to iterate from 0 > size -1 not the other way around
//		List<String> cleanWords = new ArrayList<String>(this.words);
//		Collections.copy(cleanWords, this.words);
//		Collections.reverse(cleanWords);
//		
//		// get the first five cleanWords (or less if size is smaller) and remove the these cleanWords from the list
//		String firstNWords = StringUtils.join(cleanWords.subList(0, cleanWords.size() >= 5 ? 5 : cleanWords.size()), " ");
//		cleanWords = cleanWords.subList(cleanWords.size() >= 5 ? 5 : cleanWords.size(), cleanWords.size());
//		
//		for (int i = 0; i < cleanWords.size() ; i++) {
//			
//			// the next word contains an entity (part)
//			if ( !cleanWords.get(i).contains("_O") ) {
//				
//				firstNWords += " " + cleanWords.get(i);
//			}
//			else break;
//		}
//		List<String> newWords = Arrays.asList(firstNWords.split(" "));
//		Collections.reverse(newWords);
//		return StringUtils.join(newWords, " ");
//	}
//
//	public static void main(String[] args) {
//
//		String testAnnotated 			= "Has_O a_O of_B-Per Josephine_B-PER Daughter_I-PER ,_O who_O was_O born_O in_O Germany_LOC ._O";
//		String test			 			= "Has a of Josephine Daughter , who was born in Germany .";
//		String patternWithOutVariables1 = ", who was born in";
//		
//		NLPBox fox =  new NLPBox();
//		
//		Context leftContext = new LeftFoxContext(testAnnotated,test,patternWithOutVariables1, fox.getNER(test));
//		System.out.println(leftContext.getSuitableEntity("http://dbpedia.org/ontology/Person"));
//	}
//	
//}
