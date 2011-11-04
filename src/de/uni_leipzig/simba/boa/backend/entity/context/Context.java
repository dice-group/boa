package de.uni_leipzig.simba.boa.backend.entity.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import edu.stanford.nlp.util.StringUtils;


public abstract class Context {

	protected List<String> words;
	protected String pattern;
	
	protected NamedEntityRecognizer ner;
	
	/**
	 * Returns true if the context contains an entity according to the specified type. For example 
	 * if the entity type is http://dbpedia.org/ontology/Person than this method returns only true
	 * if a entity by the ner tagger with the tag "B-PER" was found.
	 * 
	 * @param entityType the uri of the entity
	 * @return true if sentence contains the entity 
	 */
	public boolean containsSuitableEntity(String entityType) {
		
		for ( String word : this.words ) {
			
			if ( word.contains(NamedEntityRecognizer.DELIMITER + "B-" + Context.namedEntityRecognitionMappings.get(entityType)) 
					|| word.contains(NamedEntityRecognizer.DELIMITER + "I-" + Context.namedEntityRecognitionMappings.get(entityType)) )  
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
	 * @param words the words to set
	 */
	public void setWords(List<String> words) {

		this.words = words;
	}

	/**
	 * @return the words
	 */
	public List<String> getWords() {

		return this.words;
	}

	
	/**
	 * @return the ner
	 */
	public NamedEntityRecognizer getNer() {
	
		return this.ner;
	}

	
	/**
	 * @param ner the ner to set
	 */
	public void setNer(NamedEntityRecognizer ner) {
	
		this.ner = ner;
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
	
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdministrativeRegion","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airport","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ArchitecturalStructure","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Arena","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Atoll","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BodyOfWater","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bridge","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Building","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Canal","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cave","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/City","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Continent","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Country","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricBuilding","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricPlace","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hospital","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hotel","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Infrastructure","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Island","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lake","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LaunchPad","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lighthouse","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LunarCrater","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monument","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mountain","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainPass","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainRange","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Museum","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NaturalPlace","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Park","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Place","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PopulatedPlace","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PowerStation","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ProtectedArea","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PublicTransitSystem","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayLine","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayTunnel","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Restaurant","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/River","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Road","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadJunction","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadTunnel","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RouteOfTransportation","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Settlement","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ShoppingMall","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SiteOfSpecialScientificInterest","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SkiArea","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Skyscraper","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stadium","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Station","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stream","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Theatre","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Town","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Tunnel","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Valley","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Village","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WaterwayTunnel","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WineRegion","LOCATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WorldHeritageSite","LOCATION");
		
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Actor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdultActor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ambassador","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Architect","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Astronaut","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Athlete","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianRulesFootballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BadmintonPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Boxer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BritishRoyalty","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cardinal","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Celebrity","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Chancellor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChessPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChristianBishop","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cleric","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CollegeCoach","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Comedian","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCharacter","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCreator","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Congressman","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cricketer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Criminal","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cyclist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Deputy","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FictionalCharacter","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FigureSkater","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FormulaOneRacer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GaelicGamesPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Governor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GridironFootballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Journalist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Judge","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lieutenant","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MartialArtist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mayor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MemberOfParliament","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryPerson","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Model","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monarch","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalArtist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NascarDriver","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NationalCollegiateAthleticAssociationAthlete","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/OfficeHolder","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Person","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Philosopher","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PlayboyPlaymate","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PokerPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PolishKing","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Politician","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Pope","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/President","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Priest","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PrimeMinister","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Royalty","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Saint","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Scientist","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Senator","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerChamp","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerManager","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePresident","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePrimeMinister","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VoiceActor","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballPlayer","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Wrestler","PERSON");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Writer","PERSON");
		
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airline","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianFootballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AutoRacingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Band","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BowlingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BoxingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BroadcastNetwork","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcast","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcaster","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/College","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Company","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CricketLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CurlingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CyclingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EducationalInstitution","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FieldHockeyLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GeopoliticalORGANIZATION","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GovernmentAgency","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HandballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HockeyTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/InlineHockeyLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LacrosseLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LawFirm","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Legislature","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryUnit","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MixedMartialArtsLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MotorcycleRacingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Non-ProfitORGANIZATION","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ORGANIZATION", "ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PaintballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoliticalParty","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoloLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioControlledRacingLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioStation","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RecordLabel","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/School","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerClub","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoftballLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsTeam","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionStation","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TradeUnion","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/University","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VideogamesLeague","ORGANIZATION");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballLeague","ORGANIZATION");

		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Activity","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Game","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sport","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AnatomicalStructure","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artery","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bone","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Brain","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Embryology","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lymph","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Muscle","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Nerve","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Vein","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Asteroid","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Award","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalSubstance","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalCompound","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChemicalElement","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Colour","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Currency","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Database","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BiologicalDatabase","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Device","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AutomobileEngine","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Weapon","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Disease","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Drug","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EthnicGroup","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Event","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Convention","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Election","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FilmFestival","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryConflict","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicFestival","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceMission","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsEvent","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FootballMatch","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GrandPrix","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MixedMartialArtsEvent","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Olympics","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Race","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WomensTennisAssociationTournament","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WrestlingEvent","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/YearInSpaceflight","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Food","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Beverage","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Galaxy","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Gene","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GovernmentType","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Holiday","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ideology","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Language","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LegalCase","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SupremeCourtOfTheUnitedStatesCase","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MeanOfTransportation","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Aircraft","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Automobile","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Locomotive","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Rocket","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ship","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceShuttle","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpaceStation","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Spacecraft","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicGenre","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Name","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GivenName","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Surname","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/OlympicResult","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PersonFunction","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Planet","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ProgrammingLanguage","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Project","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ResearchProject","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Protein","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sales","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Single","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerWorldRanking","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Species","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Archaea","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bacteria","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Eukaryote","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Animal","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Amphibian","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Arachnid","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bird","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Crustacean","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fish","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Insect","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mammal","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mollusca","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Reptile","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fungus","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Plant","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ClubMoss","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Conifer","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cycad","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Fern","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FloweringPlant","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Grape","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ginkgo","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Gnetophytes","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GreenAlga","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Moss","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Work","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Film","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Musical","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalWork","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Album","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Song","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EurovisionSongContestEntry","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Painting","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Sculpture","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Software","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VideoGame","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionEpisode","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionShow","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Website","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WrittenWork","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Book","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PeriodicalLiterature","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AcademicJournal","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Magazine","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Newspaper","MISC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Play", "MISC");
		
		namedEntityRecognitionMappings.put("http://www.w3.org/2001/XMLSchema#date", "DATE");
	}
}
