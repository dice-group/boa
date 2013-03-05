package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;


public abstract class Context {

    protected List<String> cleanWords;
    protected List<String> taggedWords;
    protected String pattern;
    protected String sentence;
    
    protected NamedEntityRecognition ner;
    
    /**
     * Returns true if the context contains an entity according to the specified type. For example 
     * if the entity type is Person than this method returns only true
     * if a entity by the ner tagger with the tag "B-PER" was found.
     * 
     * @param entityType the uri of the entity
     * @return true if sentence contains the entity 
     */
    public boolean containsSuitableEntity(String entityType) {

		String entityMapping = Context.namedEntityRecognitionMappings.get(entityType);
		
		for (String word: this.taggedWords) {
			if (entityMapping == null) {
				for (String tag : Context.namedEntityRecognitionTags)
					if (word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + tag))
						return true;
			}
			else if (word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + entityMapping))
				return true;
		}

//        for ( String word : this.taggedWords ) {
//            
//            if ( word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + Context.namedEntityRecognitionMappings.get(entityType)) 
//                || word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + Context.namedEntityRecognitionMappings.get(entityType)) ) 
//                return true;
//        }
        return false;
    }
    
    /**
     * 
     * @param entityType
     * @return
     */
    public abstract int getSuitableEntityDistance(String entityType);
    
    /**
     * 
     * @param entityType
     * @return
     */
    public abstract String getSuitableEntity(String entityType);

    /**
     * @param cleanWords the cleanWords to set
     */
    public void setCleanWords(List<String> words) {

        this.cleanWords = words;
    }

    /**
     * @return the cleanWords
     */
    public List<String> getCleanWords() {

        return this.cleanWords;
    }

    
    
    /**
     * @return the taggedWords
     */
    public List<String> getTaggedWords() {
    
        return taggedWords;
    }

    
    /**
     * @param taggedWords the taggedWords to set
     */
    public void setTaggedWords(List<String> taggedWords) {
    
        this.taggedWords = taggedWords;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
    
        return pattern;
    }

    
    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
    
        this.pattern = pattern;
    }
    
    public static void main(String[] args) {

        String filter = "";
        
        for ( Map.Entry<String, String> entry : namedEntityRecognitionMappings.entrySet() ) {
            
            filter += String.format(" Filter ( ?class != <%s>) ", entry.getKey());
        }
        
        String query = 
            "SELECT ?class " + 
            "WHERE { " +
                " ?class rdf:type owl:Class . " + filter +  
            " }";    
        
        System.out.println(query);
    }
    
    public static final Map<String,String> nerToolClassMappings = new HashMap<String,String>();
    static {
        
        nerToolClassMappings.put("http://dbpedia.org/ontology/ORGANIZATION","ORGANIZATION");
        nerToolClassMappings.put("http://dbpedia.org/ontology/LOCATION","LOCATION");
        nerToolClassMappings.put("http://dbpedia.org/ontology/PERSON","PERSON");
    }
    
    
	public static final HashSet<String> namedEntityRecognitionTags = new HashSet<String>();
    public static final Map<String,String> namedEntityRecognitionMappings = new HashMap<String,String>();
    static {
		namedEntityRecognitionTags.add(Constants.NAMED_ENTITY_TAG_PLACE);
		namedEntityRecognitionTags.add(Constants.NAMED_ENTITY_TAG_PERSON);
		namedEntityRecognitionTags.add(Constants.NAMED_ENTITY_TAG_ORGANIZATION);
		namedEntityRecognitionTags.add(Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
    
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Agent",                  				Constants.NAMED_ENTITY_TAG_ORGANIZATION);
		
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdministrativeRegion",                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airport",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ArchitecturalStructure",                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Arena",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Atoll",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BodyOfWater",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bridge",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Building",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Canal",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cave",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/City",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Continent",                             Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Country",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricBuilding",                      Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricPlace",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hospital",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hotel",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Infrastructure",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Island",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lake",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LaunchPad",                             Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lighthouse",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LunarCrater",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monument",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mountain",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainPass",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainRange",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Museum",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NaturalPlace",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Park",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Place",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PopulatedPlace",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PowerStation",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ProtectedArea",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PublicTransitSystem",                   Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayLine",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayTunnel",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Restaurant",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/River",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Road",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadJunction",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadTunnel",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RouteOfTransportation",                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Settlement",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ShoppingMall",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SiteOfSpecialScientificInterest",       Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SkiArea",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Skyscraper",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stadium",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Station",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stream",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Theatre",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Town",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Tunnel",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Valley",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Village",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WaterwayTunnel",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WineRegion",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WorldHeritageSite",                     Constants.NAMED_ENTITY_TAG_PLACE);
        
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Actor",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdultActor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ambassador",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Architect",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artist",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Astronaut",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Athlete",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianRulesFootballPlayer",                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BadmintonPlayer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballPlayer",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballPlayer",                              Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Boxer",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BritishRoyalty",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cardinal",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Celebrity",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Chancellor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChessPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChristianBishop",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cleric",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CollegeCoach",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Comedian",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCharacter",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCreator",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Congressman",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cricketer",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Criminal",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cyclist",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Deputy",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FictionalCharacter",                            Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FigureSkater",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FormulaOneRacer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GaelicGamesPlayer",                             Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfPlayer",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Governor",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GridironFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyPlayer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Journalist",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Judge",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lieutenant",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MartialArtist",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mayor",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MemberOfParliament",                            Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryPerson",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Model",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monarch",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalArtist",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NascarDriver",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NationalCollegiateAthleticAssociationAthlete",  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/OfficeHolder",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Person",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Philosopher",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PlayboyPlaymate",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PokerPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PolishKing",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Politician",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Pope",                                          Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/President",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Priest",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PrimeMinister",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Royalty",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Saint",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Scientist",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Senator",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerChamp",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerPlayer",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerManager",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerPlayer",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisPlayer",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePresident",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePrimeMinister",                             Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VoiceActor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballPlayer",                              Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Wrestler",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Writer",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airline",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AutoRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Band",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BowlingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BoxingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BroadcastNetwork",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcast",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcaster",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/College",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Company",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CricketLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CurlingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CyclingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EducationalInstitution",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FieldHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GeopoliticalORG",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GovernmentAgency",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HandballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HockeyTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/InlineHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LacrosseLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LawFirm",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Legislature",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryUnit",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MixedMartialArtsLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MotorcycleRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Non-ProfitORG",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Organisation", Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PaintballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoliticalParty",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoloLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioControlledRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioStation",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RecordLabel",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/School",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerClub",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoftballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionStation",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TradeUnion",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/University",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VideogamesLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
            
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Activity",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Game",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sport",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AnatomicalStructure",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artery",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bone",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Brain",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Embryology",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lymph",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Muscle",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Nerve",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Vein",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Asteroid",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Award",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalSubstance",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalCompound",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalElement",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Colour",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Currency",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Database",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BiologicalDatabase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Device",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AutomobileEngine",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Weapon",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Disease",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Drug",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EthnicGroup",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Event",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Convention",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Election",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FilmFestival",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryConflict",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicFestival",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceMission",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FootballMatch",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GrandPrix",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MixedMartialArtsEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Olympics",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Race",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WomensTennisAssociationTournament",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WrestlingEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/YearInSpaceflight",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Food",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Beverage",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Galaxy",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Gene",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GovernmentType",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Holiday",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ideology",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Language",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LegalCase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SupremeCourtOfTheUnitedStatesCase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MeanOfTransportation",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Aircraft",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Automobile",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Locomotive",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Rocket",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ship",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceShuttle",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceStation",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Spacecraft",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicGenre",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Name",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GivenName",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Surname",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/OlympicResult",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PersonFunction",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Planet",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ProgrammingLanguage",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Project",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ResearchProject",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Protein",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sales",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Single",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerWorldRanking",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Species",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Archaea",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bacteria",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Eukaryote",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Animal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Amphibian",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Arachnid",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bird",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Crustacean",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fish",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Insect",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mammal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mollusca",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Reptile",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fungus",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Plant",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ClubMoss",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Conifer",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cycad",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fern",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FloweringPlant",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Grape",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ginkgo",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Gnetophytes",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GreenAlga",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Moss",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Work",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Film",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Musical",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalWork",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Album",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Song",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EurovisionSongContestEntry",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Painting",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sculpture",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Software",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VideoGame",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionEpisode",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionShow",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Website",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WrittenWork",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Book",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PeriodicalLiterature",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AcademicJournal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Magazine",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Newspaper",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Play",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        
        namedEntityRecognitionMappings.put("http://www.w3.org/2001/XMLSchema#date", "DATE");
        namedEntityRecognitionMappings.put("http://www.w3.org/2001/XMLSchema#double", "NUMBER");
        namedEntityRecognitionMappings.put("http://www.w3.org/2001/XMLSchema#nonNegativeInteger", "NUMBER");
        
        namedEntityRecognitionMappings.put("NA", "UNKNOWN");
    }
}
