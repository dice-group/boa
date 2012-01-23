package de.uni_leipzig.simba.boa.backend.nlp.date;

import javatools.parsers.DateParser;


public class DateNormalizer {


	public static String normalize(String stringToNormalize) {
		
		return DateParser.normalize(stringToNormalize);
	}
	
	public static void main(String[] args) {

		String test = "?R? was born in May 1960 in the city of Sulaymaniyah in ?D?";
		test = normalize(test);

		test = test.replaceAll(".{4}-.{2}-.{2}", "_DATE_");
		
		System.out.println(test);
	}
}
