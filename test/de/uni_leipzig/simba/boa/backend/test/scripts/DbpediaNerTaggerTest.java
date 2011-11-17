package de.uni_leipzig.simba.boa.backend.test.scripts;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.nlp.NamedEntityRecognizer;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;


public class DbpediaNerTaggerTest {

//	private static AbstractSequenceClassifier classifier = new NERClassifierCombiner(
//		CRFClassifier.getClassifierNoExceptions("/Users/gerb/Desktop/person-ner-model.ser.gz"),
//		CRFClassifier.getClassifierNoExceptions("/Users/gerb/Desktop/musical-artist-ner-model.ser.gz")); 
		
		//CRFClassifier.getClassifierNoExceptions("/Users/gerb/Desktop/person-ner-model.ser.gz");
	private static String[] testSentences = new String[]{
		
		"Angie Be is a 32 year old model from Paris . ",
		"Jinx is the ninth studio release by Irish musician Rory Gallagher . ",
		"After only one year , George Acosta 's `` Lostworld '' was the # 1 radio show in all age groups . ",
		"After years of playing in clubs throughout the U.S. , George Acosta wanted to bring dance music from the underground to the masses . ",
		"The song was also featured on Selena . ",
		"In 1945 he recorded a session as leader -LRB- the first of only two -RRB- with Freddie Webster and a young Bud Powell for Duke Records . ",
		"Frank Socolow -LRB- September 18 , 1923 -- April 30 , 1981 -RRB- , born in New York City , was a jazz saxophonist and oboist , noted for his tenor playing . ",
		"In addition to the dual roles played by Kenneth Branagh and Emma Thompson , actress Jo Anderson and the film 's composer Patrick Doyle both play small dual parts , appearing in the present-day and 1940s sequences . ",
		"Patrick Doyle was nominated for a Golden Globe for his suspenseful John Addison-like orchestral music score . ",
		"It was recently announced by singer Chris Daughtry at a concert that Adam had co-written a song with him . ",
		"Adam Gontier 's mother -LRB- Patricia Duffy -RRB- was the one who got Gontier interested in music and taught him to play the guitar . ",
		"He has a tattoo of the word `` grace '' tattooed on his knuckles on his right hand using the font from Jeff Buckley 's Grace -LRB- Jeff Buckley album -RRB- album . ",
		"Gradually , these characters evolved under the influence of Claude Dubois and above all Jean-Louis Pesch who turned the comperes into comic protagonists and added some characters . ",
		"From 1956 , Jean-Louis Pesch and Claude Dubois took over the series and drawn and wrote albums on their own and separately , but still in the same series , published by Fleurus from 1953 . ",
		"Mack David was the elder brother of American lyricist and songwriter , Hal David . ",
		"Mack David was born to a Jewish family in New York City , New York , on July 5 , 1912 . ",
		"It is also interesting to note that David 's `` most remunerative '' song `` Sunflower '' , published in 1948 and turned into a hit by Frank Sinatra , used the same melody line as Jerry Herman 's hit theme song for Hello , Dolly ! The illustrations are separatly by Maurice Cuvillier , Claude Dubois , Pierre Chéry and Jean-Louis Pesch . ",
		"In the summer of 2002 , Alsou began a tour of the former Soviet countries . ",
		"Alsou had recorded two songs for the movie . ",
		"After her success in Eurovision 2000 , Alsou began a large-scale tour of Russia . ",
		"Alsou performed `` Solo '' again at Congratulations , the 50th anniversary Eurovision concert in Copenhagen , Denmark , in October 2005 . ",
		"During 2000 -- 2001 , Alsou began working on her debut English album also called Alsou , which was released in Russia on June 28 , 2001 . "
	};
	
	private static String[] goldStandard = new String[]{
		
		"Angie_MusicalArtist Be_MusicalArtist is_O a_O 32_O year_O old_O model_O from_O Paris_O ._O",
		"Jinx_O is_O the_O ninth_O studio_O release_O by_O Irish_O musician_O Rory_MusicalArtist Gallagher_MusicalArtist ._O",
		"After_O only_O one_O year_O ,_O George_MusicalArtist Acosta_MusicalArtist 's_O ``_O Lostworld_O ''_O was_O the_O #_O 1_O radio_O show_O in_O all_O age_O groups_O ._O",
		"After_O years_O of_O playing_O in_O clubs_O throughout_O the_O U.S._O ,_O George_MusicalArtist Acosta_MusicalArtist wanted_O to_O bring_O dance_O music_O from_O the_O underground_O to_O the_O masses_O ._O",
		"The_O song_O was_O also_O featured_O on_O Selena_MusicalArtist ._O",
		"In_O 1945_O he_O recorded_O a_O session_O as_O leader_O -LRB-_O the_O first_O of_O only_O two_O -RRB-_O with_O Freddie_O Webster_O and_O a_O young_O Bud_MusicalArtist Powell_MusicalArtist for_O Duke_O Records_O ._O",
		"Frank_MusicalArtist Socolow_MusicalArtist -LRB-_O September_O 18_O ,_O 1923_O --_O April_O 30_O ,_O 1981_O -RRB-_O ,_O born_O in_O New_O York_O City_O ,_O was_O a_O jazz_O saxophonist_O and_O oboist_O ,_O noted_O for_O his_O tenor_O playing_O ._O",
		"In_O addition_O to_O the_O dual_O roles_O played_O by_O Kenneth_O Branagh_O and_O Emma_O Thompson_O ,_O actress_O Jo_O Anderson_O and_O the_O film_O 's_O composer_O Patrick_MusicalArtist Doyle_MusicalArtist both_O play_O small_O dual_O parts_O ,_O appearing_O in_O the_O present-day_O and_O 1940s_O sequences_O ._O",
		"Patrick_MusicalArtist Doyle_MusicalArtist was_O nominated_O for_O a_O Golden_O Globe_O for_O his_O suspenseful_O John_O Addison-like_O orchestral_O music_O score_O ._O",
		"It_O was_O recently_O announced_O by_O singer_O Chris_MusicalArtist Daughtry_MusicalArtist at_O a_O concert_O that_O Adam_O had_O co-written_O a_O song_O with_O him_O ._O",
		"Adam_MusicalArtist Gontier_MusicalArtist 's_O mother_O -LRB-_O Patricia_O Duffy_O -RRB-_O was_O the_O one_O who_O got_O Gontier_O interested_O in_O music_O and_O taught_O him_O to_O play_O the_O guitar_O ._O",
		"He_O has_O a_O tattoo_O of_O the_O word_O ``_O grace_O ''_O tattooed_O on_O his_O knuckles_O on_O his_O right_O hand_O using_O the_O font_O from_O Jeff_MusicalArtist Buckley_MusicalArtist 's_O Grace_O -LRB-_O Jeff_MusicalArtist Buckley_MusicalArtist album_O -RRB-_O album_O ._O",
		"Gradually_O ,_O these_O characters_O evolved_O under_O the_O influence_O of_O Claude_MusicalArtist Dubois_MusicalArtist and_O above_O all_O Jean-Louis_O Pesch_O who_O turned_O the_O comperes_O into_O comic_O protagonists_O and_O added_O some_O characters_O ._O",
		"From_O 1956_O ,_O Jean-Louis_O Pesch_O and_O Claude_MusicalArtist Dubois_MusicalArtist took_O over_O the_O series_O and_O drawn_O and_O wrote_O albums_O on_O their_O own_O and_O separately_O ,_O but_O still_O in_O the_O same_O series_O ,_O published_O by_O Fleurus_O from_O 1953_O ._O",
		"Mack_MusicalArtist David_MusicalArtist was_O the_O elder_O brother_O of_O American_O lyricist_O and_O songwriter_O ,_O Hal_O David_O ._O",
		"Mack_MusicalArtist David_MusicalArtist was_O born_O to_O a_O Jewish_O family_O in_O New_O York_O City_O ,_O New_O York_O ,_O on_O July_O 5_O ,_O 1912_O ._O",
		"It_O is_O also_O interesting_O to_O note_O that_O David_O 's_O ``_O most_O remunerative_O ''_O song_O ``_O Sunflower_O ''_O ,_O published_O in_O 1948_O and_O turned_O into_O a_O hit_O by_O Frank_O Sinatra_O ,_O used_O the_O same_O melody_O line_O as_O Jerry_MusicalArtist Herman_MusicalArtist 's_O hit_O theme_O song_O for_O Hello_O ,_O Dolly_O !_O The_O illustrations_O are_O separatly_O by_O Maurice_O Cuvillier_O ,_O Claude_MusicalArtist Dubois_MusicalArtist ,_O Pierre_O Chéry_O and_O Jean-Louis_O Pesch_O ._O",
		"In_O the_O summer_O of_O 2002_O ,_O Alsou_MusicalArtist began_O a_O tour_O of_O the_O former_O Soviet_O countries_O ._O",
		"Alsou_MusicalArtist had_O recorded_O two_O songs_O for_O the_O movie_O ._O",
		"After_O her_O success_O in_O Eurovision_O 2000_O ,_O Alsou_MusicalArtist began_O a_O large-scale_O tour_O of_O Russia_O ._O",
		"Alsou_MusicalArtist performed_O ``_O Solo_O ''_O again_O at_O Congratulations_O ,_O the_O 50th_O anniversary_O Eurovision_O concert_O in_O Copenhagen_O ,_O Denmark_O ,_O in_O October_O 2005_O ._O",
		"During_O 2000_O --_O 2001_O ,_O Alsou_MusicalArtist began_O working_O on_O her_O debut_O English_O album_O also_O called_O Alsou_MusicalArtist ,_O which_O was_O released_O in_O Russia_O on_O June_O 28_O ,_O 2001_O ._O"
	};
	
	public static void main(String[] args) {

		for (int i = 0; i < goldStandard.length ; i++) {
			
			String taggedSentence = recognizeEntitiesInString(testSentences[i]).trim();
			System.out.println("Test: " + taggedSentence);
			System.out.println("Gold: " + goldStandard[i]);
			System.out.println("Equa: " + goldStandard[i].equals(taggedSentence));
			System.out.println();
		}
	}
	
	public static String recognizeEntitiesInString(String sentence) throws java.lang.NullPointerException {
		
//		StringBuilder buffer = new StringBuilder();
//		
//		for ( List<CoreLabel> thisSentence : ((List<List<CoreLabel>>) classifier.classify(sentence)) ) {
//		
//			for ( CoreLabel word : thisSentence ) {
//				
//				if ( word.get(AnswerAnnotation.class).equals("O") ) {
//					
//					buffer.append(word.word() + NamedEntityRecognizer.DELIMITER + "O ");
//				}
//				else {
//					
//					buffer.append(word.word() + NamedEntityRecognizer.DELIMITER + word.get(AnswerAnnotation.class) + " ");
//				}
//			}
//		}
		return null;//buffer.toString();
	}
}
