package de.uni_leipzig.simba.boa.backend.entity.pattern.confidence.impl.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class SentenceIterator implements Iterator<String> {

	private List<String> context;
	private String 
	
	public SentenceIterator(String context){
		
		this.context = Arrays.asList(context.split(" "));
	}
	
	@Override
	public boolean hasNext() {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String next() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {

		// TODO Auto-generated method stub

	}

}
