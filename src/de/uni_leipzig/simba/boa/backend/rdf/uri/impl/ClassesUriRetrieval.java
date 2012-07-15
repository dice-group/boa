package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.danielgerber.file.FileUtil;
import de.danielgerber.file.BufferedFileReader;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.danielgerber.file.BufferedFileReader;
import de.uni_leipzig.simba.boa.backend.rdf.uri.UriRetrieval;

/**
 * @author Maciej Janicki <macjan@o2.pl>
 */
public class ClassesUriRetrieval implements UriRetrieval {

    protected final String BACKGROUND_KNOWLEDGE_OUTPUT_PATH = NLPediaSettings.BOA_DATA_DIRECTORY + de.uni_leipzig.simba.boa.backend.Constants.BACKGROUND_KNOWLEDGE_PATH;
	private final String CLASSES_SURFACE_FORMS_FILE = BACKGROUND_KNOWLEDGE_OUTPUT_PATH + "classes_surface_forms.tsv";
	private NLPediaLogger logger = new NLPediaLogger(ClassesUriRetrieval.class);
	private HashMap<String, ArrayList<String>> classesSurfaceForms = null;

	public ClassesUriRetrieval() {
		this.classesSurfaceForms = new HashMap<String, ArrayList<String>>();
        BufferedFileReader reader = FileUtil.openReader(CLASSES_SURFACE_FORMS_FILE, "UTF-8");
		String line;
		while ((line = reader.readLine()) != null) {
			String[] content = line.split("\t");
			if (!this.classesSurfaceForms.containsKey(content[0]))
				this.classesSurfaceForms.put(content[0], new ArrayList<String>());
			for (int i = 1; i < content.length; i++) {
				this.classesSurfaceForms.get(content[0]).add(content[i]);
			}
		}
		reader.close();
	}

	public ClassesUriRetrieval(HashMap<String, ArrayList<String>> classesSurfaceForms) {
		this.classesSurfaceForms = classesSurfaceForms;
	}
	
	@Override
	public String getUri(String label) {
		for (String uri: this.classesSurfaceForms.keySet()) {
			for (String sf: this.classesSurfaceForms.get(uri)) {
				int start = label.indexOf(sf);
				int end = start + sf.length() - 1;
				if (start == -1)
					continue;
				if ((start > 0) && (label.charAt(start-1) != ' '))
					continue;
				if ((end < label.length()-1) && (label.charAt(end+1) != ' '))
					continue;
				return uri;
			}
		}
		return null;
	}
}
