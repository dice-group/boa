package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.googleNewsCrawler.fileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileWithSubjectsReader {

	public FileWithSubjectsReader(){
		
	}

	
	public static List<String> readSubjectsFromFile(String file){
		ArrayList<String> subjects= new ArrayList<String>();
		
		BufferedReader bf=null;
		
		try {
			bf = new BufferedReader(new FileReader(new File( file)));
			
			String line;
			while((line=bf.readLine())!=null){
				if(!line.trim().isEmpty()){
					subjects.add(line.trim().replace("\"", ""));
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(bf!=null)
				try {
					bf.close();
				} catch (IOException e) {		
				}
		}
		
		return subjects;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
