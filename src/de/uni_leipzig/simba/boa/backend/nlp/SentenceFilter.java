package de.uni_leipzig.simba.boa.backend.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;

/**
 * 
 * @author Daniel Gerber
 */
public class SentenceFilter {

	private final NLPediaLogger logger = new NLPediaLogger(SentenceFilter.class);
	
	private List<String> reasons = new ArrayList<String>();
	
	private List<String> repairedSentences = new ArrayList<String>();
	private List<String> validSentences = new ArrayList<String>();
	private List<String> invalidSentences = new ArrayList<String>();
	
	public List<String> filterSentences(List<String> sentences) {
		
		this.logger.debug("Filtering " + sentences.size() + " sentences!");
		this.invalidSentences	= new ArrayList<String>();
		this.validSentences		= new ArrayList<String>();
		this.repairedSentences	= new ArrayList<String>();

		Pattern pattern;// = Pattern.compile("[\\p{Ll}\\p{Nd}][.!?]\\p{Lu}");
		Matcher matcher;// = pattern.matcher(sentence);
		
		boolean isValid = true;
		String reason = " 0 ";
		
		// check for each sentence if it's valid
		for ( String sentence : sentences ) {
			
			// the sentence detection does not work properly: there are sentences like "... this has been true.Regardless ..." 
			// so we try to split them 
//			if ( matcher.find() ) {
				
//				int indexOfPunctuation = sentence.indexOf(matcher.group());
//				System.out.println(sentence + " index of (.): " + indexOfPunctuation + " match:" + matcher.group());
				
//				String secondSentence = sentence.substring(indexOfPunctuation + 2);
//				this.repairedSentences.add(secondSentence);
//				sentence = sentence.substring(0, indexOfPunctuation + 2);
//			}
			
			isValid = true;
			reason = " 0 ";
			this.logger.debug("Filter: " + sentence);
			
			// start of record: lowercase letter, math symbol, punctuation, opening brackets, underscore, closing brackets
			pattern = Pattern.compile("^[\\p{Ll}\\p{Sm}\\p{Pd}\\p{Ps}\\p{Pc}\\p{Pe}]");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 1 ";
			}
			
			// other special wrong start letters
			pattern = Pattern.compile("^[*\\?\\.\\,\\/]");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 2 ";
			}
			
			// no punctuation at end of sentence
			pattern = Pattern.compile("\\p{P}$");
			matcher = pattern.matcher(sentence);
			if ( !matcher.find() ) {
				
				isValid = false;
				reason += " 3 ";
			}
			
			// s t r i n g s   l i k e   t h i s
			pattern = Pattern.compile(" \\p{L} \\p{L} ");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 4 "; 
			}
			
			// two subsequent spaces
			pattern = Pattern.compile("  ");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				sentence = sentence.replaceAll("  ", " ");
			}
			
			// sentences with at least ten commas
			pattern = Pattern.compile(",.*,.*,.*,.*,.*,.*,.*,.*,.*,");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 6 ";
			}
			
			// more then ten points
			pattern = Pattern.compile("\\..*\\..*\\..*\\..*\\..*\\..*\\..*\\.");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 7 ";
			}
			
			// replace multiple whitespace or new line characters with one space
			sentence = sentence.replaceAll("\\s{2,}", " ");
			sentence = sentence.replaceAll("\\n", "");
			
			// more than fifty whitespace
			if ( StringUtils.countOccurrencesOf(sentence, " ") > 50 ) {
				
				isValid = false;
				reason += " 9 ";
			}
			
			// sentences with two many spaces in relation to length
			if ( sentence.length() / ( 1 + sentence.length() - StringUtils.countOccurrencesOf(sentence, " ") ) > 1.4 ) {
				
				isValid = false;
				reason += " 10 ";
			}
			
			// sentences with strange symbols | [Ê]Ê<< >>
			if ( sentence.contains("|") 
					|| sentence.contains("[") 
					|| sentence.contains("]") 
					|| sentence.contains("<<") 
					|| sentence.contains(">>")
					|| sentence.contains("^")) {
				
				isValid = false;
				reason += " 11 ";
			}
			
			// sentences with high amount of strange symbols
			if ( StringUtils.countOccurrencesOf(sentence, "/") > 9 
					|| StringUtils.countOccurrencesOf(sentence, ")") > 5
					|| StringUtils.countOccurrencesOf(sentence, "(") > 5 
					|| StringUtils.countOccurrencesOf(sentence, "&") > 5 
					|| StringUtils.countOccurrencesOf(sentence, ":") > 5 ) {
				
				isValid = false;
				reason += " 12 ";
			}
			
			// many letters/numbers/symbols of same kind in a row
			pattern = Pattern.compile("[\\p{Lu} \\.,\\/-]{22}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 13 ";
			}
			pattern = Pattern.compile("/[[:digit:] .,\\/-]{18}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 14 ";
			}
			
			// short sentences which contain many numbers before .,/
			pattern = Pattern.compile("[[:digit:].,\\/-]{6}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() && sentence.length() < 45  ) {
				
				isValid = false;
				reason += " 15 ";
			}
			
			// short sentences which . . .  at the end
			pattern = Pattern.compile("\\. \\. \\.$");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() && sentence.length() < 60  ) {
				
				isValid = false;
				reason += " 16 ";
			}
			
			// short sentences which contain ...
			pattern = Pattern.compile("\\.\\.\\.");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() && sentence.length() < 60  ) {
				
				isValid = false;
				reason += " 17 ";
			}
			
			// sentence with multiple !! or ??  in a row
			pattern = Pattern.compile("[?!]{2}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 18 ";
			}
			
			// sentence with more than eight capital words in a row
			pattern = Pattern.compile("(\\p{Lu}\\p{L}* ){8}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 19 ";
			}
			
			// sentence with " " in front of punctuation
//			pattern = Pattern.compile("[\\.,?!]");
//			matcher = pattern.matcher(sentence);
//			if ( matcher.find() ) {
//				
//				isValid = false;
//				reason += " 20 ";
//			}
			
			// sentence with initals and abbreveations at the end
			pattern = Pattern.compile("([\\. ]\\p{Lu}| \\p{N}|-ing|str)[\\.:][\"\\']*$");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 21 ";
			}
			
			// to short sentence 
			if ( StringUtils.countOccurrencesOf(sentence, " ") == 0 || sentence.length() < 15 ) {
				
				isValid = false;
				reason += " 22 ";
			}
			
			// too long sentences
			if ( sentence.length() > 512 ) {
				
				isValid = false;
				reason += " 23 ";
			}
			
			// sentence with two www
			pattern = Pattern.compile("www\\.{2}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 24 ";
			}
			
			// sentences with two http
			pattern = Pattern.compile("http{2}");
			matcher = pattern.matcher(sentence);
			if ( matcher.find() ) {
				
				isValid = false;
				reason += " 25 ";
			}
			
			// sentence needs to be removed from list of sentences
			if ( isValid ) {
				
				this.logger.debug("Sentence correct.");
				validSentences.add(sentence);
			}
			else {
				
				this.logger.debug("Sentence not correct because of reason: " + reason);
				invalidSentences.add(reason + " XXX " + sentence);
				reasons.add(reason);
			}
		}
		// send repaired sentences to filtering again and add good sentences to return value
//		this.validSentences.addAll(this.repairedSentences);
		
//		int[] errors = new int[26];
//		
//		for (String reasonString : reasons) {
//			
//			for ( String error : reasonString.split(" ") ){
//				
//				if ( !error.equals("") ) {
//					errors[new Integer(error).intValue()]++;
//				}
//			}
//		}
//		
//		for ( int i = 0 ; i < errors.length ; i++) {
//			
//			this.logger.debug("Error: " + i + ": " + errors[i]);
//		}
		
//		try {
//			
//			Writer writer;
//			writer = new PrintWriter(new BufferedWriter(new FileWriter(NLPediaSettings.getInstance().getSetting("sentenceErrorFile"), true)));
//			for (String error : this.invalidSentences ) {
//				
//				writer.write(error);
//				writer.write(System.getProperty("line.separator"));
//			}
//			writer.close();
//			
//			writer = new PrintWriter(new BufferedWriter(new FileWriter(NLPediaSettings.getInstance().getSetting("sentenceRepairedFile"), true)));
//			for (String error : this.repairedSentences ) {
//				
//				writer.write(error);
//				writer.write(System.getProperty("line.separator"));
//			}
//			writer.close();
//		}
//		catch (IOException e) {
//			
//			e.printStackTrace();
//			this.logger.debug("Writing error sentences did not finish correctly..", e);
//		}
		
		return validSentences;
	}
}
