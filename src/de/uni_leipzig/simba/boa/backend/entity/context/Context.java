package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.NamedEntityRecognition;


public abstract class Context {

    protected List<String> cleanWords;
    protected List<String> taggedWords;
    protected String pattern;
    
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
        
        for ( String word : this.taggedWords ) {
            
            if ( word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + Context.namedEntityRecognitionMappings.get(entityType)) 
                || word.contains(Constants.NAMED_ENTITY_TAG_DELIMITER + Context.namedEntityRecognitionMappings.get(entityType)) ) 
                return true;
        }
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
        
        nerToolClassMappings.put("ORGANIZATION","ORGANIZATION");
        nerToolClassMappings.put("LOCATION","LOCATION");
        nerToolClassMappings.put("PERSON","PERSON");
    }
    
    
    public static final Map<String,String> namedEntityRecognitionMappings = new HashMap<String,String>();
    static {
    
        namedEntityRecognitionMappings.put("AdministrativeRegion",                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Airport",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("ArchitecturalStructure",                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Arena",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Atoll",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("BodyOfWater",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Bridge",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Building",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Canal",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Cave",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("City",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Continent",                             Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Country",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("HistoricBuilding",                      Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("HistoricPlace",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Hospital",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Hotel",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Infrastructure",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Island",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Lake",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("LaunchPad",                             Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Library",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Lighthouse",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("LunarCrater",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Monument",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Mountain",                              Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("MountainPass",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("MountainRange",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Museum",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("NaturalPlace",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Park",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Place",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("PopulatedPlace",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("PowerStation",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("ProtectedArea",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("PublicTransitSystem",                   Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("RailwayLine",                           Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("RailwayTunnel",                         Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Restaurant",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("River",                                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Road",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("RoadJunction",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("RoadTunnel",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("RouteOfTransportation",                 Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Settlement",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("ShoppingMall",                          Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("SiteOfSpecialScientificInterest",       Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("SkiArea",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Skyscraper",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Stadium",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Station",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Stream",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Theatre",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Town",                                  Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Tunnel",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Valley",                                Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("Village",                               Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("WaterwayTunnel",                        Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("WineRegion",                            Constants.NAMED_ENTITY_TAG_PLACE);
        namedEntityRecognitionMappings.put("WorldHeritageSite",                     Constants.NAMED_ENTITY_TAG_PLACE);
        
        namedEntityRecognitionMappings.put("Actor",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("AdultActor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Ambassador",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("AmericanFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Architect",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Artist",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Astronaut",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Athlete",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("AustralianRulesFootballPlayer",                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("BadmintonPlayer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("BaseballPlayer",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("BasketballPlayer",                              Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Boxer",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("BritishRoyalty",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("CanadianFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Cardinal",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Celebrity",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Chancellor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("ChessPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("ChristianBishop",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Cleric",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("CollegeCoach",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Comedian",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("ComicsCharacter",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("ComicsCreator",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Congressman",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Cricketer",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Criminal",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Cyclist",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Deputy",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("FictionalCharacter",                            Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("FigureSkater",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("FormulaOneRacer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("GaelicGamesPlayer",                             Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("GolfPlayer",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Governor",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("GridironFootballPlayer",                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("IceHockeyPlayer",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Journalist",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Judge",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Lieutenant",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("MartialArtist",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Mayor",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("MemberOfParliament",                            Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("MilitaryPerson",                                Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Model",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Monarch",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("MusicalArtist",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("NascarDriver",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("NationalCollegiateAthleticAssociationAthlete",  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("OfficeHolder",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Person",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Philosopher",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("PlayboyPlaymate",                               Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("PokerPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("PolishKing",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Politician",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Pope",                                          Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("President",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Priest",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("PrimeMinister",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Royalty",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("RugbyPlayer",                                   Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Saint",                                         Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Scientist",                                     Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Senator",                                       Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("SnookerChamp",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("SnookerPlayer",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("SoccerManager",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("SoccerPlayer",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("TennisPlayer",                                  Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("VicePresident",                                 Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("VicePrimeMinister",                             Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("VoiceActor",                                    Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("VolleyballPlayer",                              Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Wrestler",                                      Constants.NAMED_ENTITY_TAG_PERSON);
        namedEntityRecognitionMappings.put("Writer",                                        Constants.NAMED_ENTITY_TAG_PERSON);
        
        namedEntityRecognitionMappings.put("Airline",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("AmericanFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("AmericanFootballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("AustralianFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("AutoRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Band",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BaseballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BasketballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BasketballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BowlingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BoxingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("BroadcastNetwork",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Broadcast",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Broadcaster",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("CanadianFootballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("CanadianFootballTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("College",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Company",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("CricketLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("CurlingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("CyclingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("EducationalInstitution",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("FieldHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("GeopoliticalORG",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("GolfLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("GovernmentAgency",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("HandballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("HockeyTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("IceHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("InlineHockeyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("LacrosseLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("LawFirm",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Legislature",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Library",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("MilitaryUnit",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("MixedMartialArtsLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("MotorcycleRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Non-ProfitORG",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("Organisation", Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("PaintballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("PoliticalParty",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("PoloLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("RadioControlledRacingLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("RadioStation",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("RecordLabel",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("RugbyLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("School",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SoccerClub",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SoccerLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SoftballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SpeedwayLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SpeedwayTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SportsLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("SportsTeam",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("TelevisionStation",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("TennisLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("TradeUnion",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("University",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("VideogamesLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
        namedEntityRecognitionMappings.put("VolleyballLeague",        Constants.NAMED_ENTITY_TAG_ORGANIZATION);
            
        namedEntityRecognitionMappings.put("Activity",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Game",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Sport",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("AnatomicalStructure",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Artery",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Bone",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Brain",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Embryology",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Lymph",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Muscle",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Nerve",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Vein",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Asteroid",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Award",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ChemicalSubstance",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ChemicalCompound",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ChemicalElement",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Colour",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Currency",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Database",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("BiologicalDatabase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Device",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("AutomobileEngine",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Weapon",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Disease",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Drug",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("EthnicGroup",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Event",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Convention",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Election",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("FilmFestival",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MilitaryConflict",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MusicFestival",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SpaceMission",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SportsEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("FootballMatch",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("GrandPrix",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MixedMartialArtsEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Olympics",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Race",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("WomensTennisAssociationTournament",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("WrestlingEvent",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("YearInSpaceflight",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Food",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Beverage",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Galaxy",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Gene",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("GovernmentType",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Holiday",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Ideology",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Language",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("LegalCase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SupremeCourtOfTheUnitedStatesCase",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MeanOfTransportation",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Aircraft",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Automobile",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Locomotive",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Rocket",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Ship",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SpaceShuttle",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SpaceStation",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Spacecraft",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MusicGenre",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Name",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("GivenName",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Surname",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("OlympicResult",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("PersonFunction",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Planet",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ProgrammingLanguage",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Project",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ResearchProject",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Protein",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Sales",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Single",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("SnookerWorldRanking",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Species",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Archaea",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Bacteria",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Eukaryote",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Animal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Amphibian",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Arachnid",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Bird",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Crustacean",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Fish",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Insect",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Mammal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Mollusca",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Reptile",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Fungus",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Plant",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("ClubMoss",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Conifer",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Cycad",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Fern",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("FloweringPlant",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Grape",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Ginkgo",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Gnetophytes",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("GreenAlga",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Moss",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Work",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Film",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Musical",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("MusicalWork",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Album",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Song",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("EurovisionSongContestEntry",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Painting",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Sculpture",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Software",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("VideoGame",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("TelevisionEpisode",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("TelevisionShow",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Website",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("WrittenWork",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Book",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("PeriodicalLiterature",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("AcademicJournal",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Magazine",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Newspaper",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        namedEntityRecognitionMappings.put("Play",    Constants.NAMED_ENTITY_TAG_MISCELLANEOUS);
        
        namedEntityRecognitionMappings.put("http://www.w3.org/2001/XMLSchema#date", "DATE");
    }
}
