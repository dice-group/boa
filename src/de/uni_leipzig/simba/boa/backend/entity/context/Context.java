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
	
	protected String buffer;
	
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
	
	public static final Map<String,String> namedEntityRecognitionMappings = new HashMap<String,String>();
	static {
	
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdministrativeRegion","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airport","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ArchitecturalStructure","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Arena","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Atoll","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BodyOfWater","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Bridge","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Building","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Canal","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cave","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/City","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Continent","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Country","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricBuilding","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HistoricPlace","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hospital","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Hotel","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Infrastructure","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Island","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lake","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LaunchPad","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lighthouse","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LunarCrater","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monument","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mountain","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainPass","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MountainRange","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Museum","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NaturalPlace","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Park","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Place","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PopulatedPlace","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PowerStation","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ProtectedArea","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PublicTransitSystem","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayLine","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RailwayTunnel","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Restaurant","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/River","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Road","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadJunction","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RoadTunnel","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RouteOfTransportation","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Settlement","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ShoppingMall","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SiteOfSpecialScientificInterest","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SkiArea","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Skyscraper","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stadium","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Station","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Stream","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Theatre","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Town","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Tunnel","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Valley","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Village","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WaterwayTunnel","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WineRegion","LOC");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/WorldHeritageSite","LOC");
		
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Actor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AdultActor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Ambassador","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Architect","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Artist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Astronaut","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Athlete","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianRulesFootballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BadmintonPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Boxer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BritishRoyalty","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cardinal","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Celebrity","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Chancellor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChessPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ChristianBishop","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cleric","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CollegeCoach","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Comedian","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCharacter","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/ComicsCreator","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Congressman","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cricketer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Criminal","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Cyclist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Deputy","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FictionalCharacter","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FigureSkater","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FormulaOneRacer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GaelicGamesPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Governor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GridironFootballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Journalist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Judge","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Lieutenant","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MartialArtist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Mayor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MemberOfParliament","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryPerson","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Model","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Monarch","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MusicalArtist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NascarDriver","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/NationalCollegiateAthleticAssociationAthlete","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/OfficeHolder","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Person","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Philosopher","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PlayboyPlaymate","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PokerPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PolishKing","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Politician","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Pope","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/President","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Priest","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PrimeMinister","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Royalty","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Saint","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Scientist","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Senator","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerChamp","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SnookerPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerManager","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePresident","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VicePrimeMinister","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VoiceActor","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballPlayer","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Wrestler","PER");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Writer","PER");
		
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Airline","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AmericanFootballTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AustralianFootballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/AutoRacingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Band","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BaseballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BasketballTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BowlingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BoxingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/BroadcastNetwork","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcast","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Broadcaster","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CanadianFootballTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/College","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Company","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CricketLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CurlingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/CyclingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/EducationalInstitution","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/FieldHockeyLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GeopoliticalOrganisation","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GolfLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/GovernmentAgency","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HandballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/HockeyTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/IceHockeyLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/InlineHockeyLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LacrosseLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/LawFirm","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Legislature","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Library","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MilitaryUnit","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MixedMartialArtsLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/MotorcycleRacingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Non-ProfitOrganisation","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/Organisation", "ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PaintballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoliticalParty","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/PoloLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioControlledRacingLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RadioStation","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RecordLabel","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/RugbyLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/School","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerClub","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoccerLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SoftballLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SpeedwayTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/SportsTeam","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TelevisionStation","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TennisLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/TradeUnion","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/University","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VideogamesLeague","ORG");
		namedEntityRecognitionMappings.put("http://dbpedia.org/ontology/VolleyballLeague","ORG");

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
	}
}
