package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.danielgerber.file.BufferedFileReader;
import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.danielgerber.rdf.NtripleUtil;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;

public class TranslateE2KPreprocessingModule extends AbstractPreprocessingModule{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 1. Read Korean labels from the delivered url. 
	 * 2. Read English background knowledge.
	 * 
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Map<String,String> uriToLabelMapping = NtripleUtil.parseNTripleFile("/Users/gerb/labels_ko.nt");	// Reads in Korean labels.
		  
		  BufferedFileReader reader = FileUtil.openReader("/Users/gerb/en_relation_surface.txt");			// Reads in English background knowledge.
		  BufferedFileWriter writer = FileUtil.openWriter("/Users/gerb/ko_relation_surface.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
		  String line = "";

		  while ((line = reader.readLine()) != null ) {
		   
		   String[] lineParts = line.split(" \\|\\|\\| ");
		   
		   String subjectUri = lineParts[0];
		   String objectUri = lineParts[4];
		   
		   if ( uriToLabelMapping.containsKey(subjectUri) && uriToLabelMapping.containsKey(objectUri) ) {
		    
		    lineParts[1] = uriToLabelMapping.get(subjectUri);
		    lineParts[2] = uriToLabelMapping.get(subjectUri);
		    lineParts[5] = uriToLabelMapping.get(objectUri);
		    lineParts[6] = uriToLabelMapping.get(objectUri);
		    
		    writer.write(StringUtils.join(lineParts, " ||| ") + "\n");
		   }
		  }
		  reader.close();
		  writer.close();
	}

	@Override
	public String getReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateModuleInterchangeObject() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDataAlreadyAvailable() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
