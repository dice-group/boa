//package de.uni_leipzig.simba.boa.backend.pipeline.module.patternsearch.impl;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
//import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectObjectPredicatePattern;
//import de.uni_leipzig.simba.boa.backend.entity.pattern.impl.SubjectPredicateObjectPattern;
//import de.uni_leipzig.simba.boa.backend.entity.patternmapping.PatternMapping;
//import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
//import de.uni_leipzig.simba.boa.backend.rdf.entity.Property;
//import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;
//import de.uni_leipzig.simba.boa.backend.search.result.comparator.SearchResultComparator;
//
//public class KoreanPatternSearchModule extends DefaultPatternSearchModule{
//    protected void createPatternMappings(List<SearchResult> results) {
//        
//        // get the cache from the interchange object
//        this.properties = this.moduleInterchangeObject.getProperties();
//        
//        // sort the patterns first by property and then by their natural language representation
//        Collections.sort(results, new SearchResultComparator());
//        
//        String currentProperty = null;
//        PatternMapping currentMapping = null;
//        
//        for ( SearchResult searchResult : results) {
//        	
//            
//            String propertyUri      = searchResult.getProperty();
//            String patternString    = searchResult.getNaturalLanguageRepresentation();
//            String label1           = searchResult.getFirstLabel();
//            String label2           = searchResult.getSecondLabel();
//            Integer sentence        = searchResult.getSentence();
//      
//            // next line is for the same property
//            if ( propertyUri.equals(currentProperty) ) {
//                
//                // add the patterns to the list with the hash-code of the natural language representation
//                Pattern pattern = patterns.get(propertyUri.hashCode()).get(patternString.hashCode()); //(patternString.hashCode());
//                
//                // pattern was not found, create a new pattern 
//                if ( pattern == null ) {
//                    if(patternString.startsWith("?D? ?R?") || patternString.startsWith("?R? ?D?")){
//                    	pattern	= new SubjectObjectPredicatePattern(patternString);
//                    }else{
//                    	pattern = new SubjectPredicateObjectPattern(patternString);
//                    }
//                    pattern.addLearnedFrom(label1 + "-;-" + label2);
//                    pattern.addPatternMapping(currentMapping);
//                    pattern.getFoundInSentences().add(sentence);
//                    
//                    if ( patterns.get(propertyUri.hashCode()) != null ) {
//                        
//                        patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
//                    }
//                    else {
//                        
//                        Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
//                        patternMap.put(patternString.hashCode(), pattern);
//                        patterns.put(propertyUri.hashCode(), patternMap);
//                    }
//                    // add the current pattern to the current mapping
//                    currentMapping.addPattern(pattern);
//                }
//                // pattern already created, just add new values
//                else {
//                    
//                    pattern.increaseNumberOfOccurrences();
//                    pattern.addLearnedFrom(label1 + "-;-" + label2);
//                    pattern.getFoundInSentences().add(sentence);
//                    pattern.addPatternMapping(currentMapping);
//                }
//            }
//            // next line contains pattern for other property
//            // so create a new pattern mapping and a new pattern
//            else {
//                
//                // create it to use the proper hash function, the properties map has a COMPLETE list of all properties
//                Property p = properties.get(propertyUri.hashCode());
//                currentMapping = mappings.get(propertyUri.hashCode());
//                
//                if ( currentMapping == null ) {
//                    
//                    currentMapping = new PatternMapping(p);
//                    this.patternMappingCount++;
//                }
//                
//                Pattern pattern = null;
//                if(patternString.startsWith("?D? ?R?") || patternString.startsWith("?R? ?D?")){
//                	pattern	= new SubjectObjectPredicatePattern(patternString);
//                }else{
//                	pattern = new SubjectPredicateObjectPattern(patternString);
//                }
//                pattern.addLearnedFrom(label1 + "-;-" + label2);
//                pattern.addPatternMapping(currentMapping);
//                pattern.getFoundInSentences().add(sentence);
//                
//                currentMapping.addPattern(pattern);
//                
//                if ( patterns.get(propertyUri.hashCode()) != null ) {
//                    
//                    patterns.get(propertyUri.hashCode()).put(patternString.hashCode(), pattern);
//                }
//                else {
//                    
//                    Map<Integer,Pattern> patternMap = new HashMap<Integer,Pattern>();
//                    patternMap.put(patternString.hashCode(), pattern);
//                    patterns.put(propertyUri.hashCode(), patternMap);
//                }
//                mappings.put(propertyUri.hashCode(), currentMapping);
//            }
//            currentProperty = propertyUri;
//        }
//        
////        for (Map.Entry<Integer, PatternMapping> hashToMappings : this.mappings.entrySet()){
////        	PatternMapping pm	= hashToMappings.getValue();
////        	System.out.println("======================================");        	
////        	for(Pattern p: pm.getPatterns()){
////        		System.out.println(p.getNaturalLanguageRepresentation());
////        	}
////        }
//        
//        // filter the patterns which do not abide certain thresholds, mostly occurrence thresholds
//        this.filterPatterns(mappings.values());
//        
////        for (Map.Entry<Integer, PatternMapping> hashToMappings : this.mappings.entrySet()){
////        	PatternMapping pm	= hashToMappings.getValue();
////        	System.out.println("AFTER ====================================== AFTER");        	
////        	for(Pattern p: pm.getPatterns()){
////        		System.out.println(p.getNaturalLanguageRepresentation());
////        	}
////        }
//        
//        // save the mappings
//        SerializationManager.getInstance().serializePatternMappings(mappings.values(), PATTERN_MAPPING_FOLDER);
//    }
//
//}
