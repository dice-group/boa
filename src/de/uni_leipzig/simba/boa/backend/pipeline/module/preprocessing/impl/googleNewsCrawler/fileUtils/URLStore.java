package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class URLStore {

	private BufferedWriter writer;
	
	public URLStore(BufferedWriter br){
		writer=br;
	}
	
	public static URLStore createNewURLStore(String file){
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(file),false));
			
			return new URLStore(br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static URLStore createOpenExistingURLStore(String file, Collection<String> urls){
		if(urls!=null){
			urls.addAll(readUrlsFromFile(file));			
		}
		
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(file),true));
			
			return new URLStore(br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static List<String> readUrlsFromFile(String file){
		ArrayList<String> urls = new ArrayList<String>();
		
		File f= new File(file);
		if(f.exists()){
			
		}
		return urls;
	}
	
	public void addUrl(String url) throws IOException{
		writer.append(url);
		writer.newLine();
		writer.flush();
	}
	
	public void close(){
		try {
			writer.close();
		} catch (IOException e) {}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
