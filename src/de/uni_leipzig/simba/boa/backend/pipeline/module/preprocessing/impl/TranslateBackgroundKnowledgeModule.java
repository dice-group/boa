package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;
import com.github.gerbsen.file.FileUtil;
import com.github.gerbsen.rdf.NtripleUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledgeManager;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.search.surfaceforms.SurfaceFormGenerator;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

public class TranslateBackgroundKnowledgeModule extends AbstractPreprocessingModule{

    private final NLPediaLogger logger = new NLPediaLogger(TranslateBackgroundKnowledgeModule.class);
    
    private final String BACKGROUND_KNOWLEDGE_OBJECT_PATH                   = NLPediaSettings.BOA_DATA_DIRECTORY.replace("/" + NLPediaSettings.BOA_LANGUAGE+ "/", "/en/") + Constants.BACKGROUND_KNOWLEDGE_OBJECT_PROPERTY_PATH;
    private final String TARGET_LANGUAGE_DBPEDIA_LABELS_FILE                = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH + "labels_" + NLPediaSettings.BOA_LANGUAGE + ".ttl";
    private final String TARGET_LANGUAGE_DBPEDIA_INTERLANGUAGE_LINKS_FILE   = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH + "interlanguage_links_en.ttl";
    private final String TARGET_LANGUAGE_OUTPUT_PATH                        = BACKGROUND_KNOWLEDGE_OBJECT_PATH + NLPediaSettings.BOA_LANGUAGE + "/";

    // for the report
    private long translateBackgroundKnowledgeFilesTime;
    
	@Override
	public String getName() {
		
		return "Translate Background Knowledge Module";
	}

	/**
	 * 
	 */
	@Override
	public void run() {
	    
	    // we start here three threads which download the three files
        long startTranslateBackgroundKnowledgeFiles = System.currentTimeMillis();
        this.logger.info("Starting to translate background knowledge from 'en' to " + NLPediaSettings.BOA_LANGUAGE + ".");
        
	    // read the target label data file, this is basically the translation dictionary
        // e.g. http://de.dbpedia.org/resource/Leipzig -> Leipzig
		Map<String,String> uriToLabelMapping              = this.loadTargetLanguageFile(TARGET_LANGUAGE_DBPEDIA_LABELS_FILE, NLPediaSettings.BOA_LANGUAGE);
		this.logger.info("Finished reading target language file");
		// e.g. http://dbpedia.org/resource/Leipzig -> http://de.dbpedia.org/resource/Leipzig
		Map<String,String> englishUriToForeignUriMappingNoPrefix    = this.loadInterlanguageLinks(TARGET_LANGUAGE_DBPEDIA_INTERLANGUAGE_LINKS_FILE, NLPediaSettings.BOA_LANGUAGE);
		this.logger.info("Finished reading interlanguage links file");
		
		final String FOREIGN_PREFIX = "http://"+ NLPediaSettings.BOA_LANGUAGE + ".dbpedia.org/resource/";
		
		// gp through every file in the object background knowledge directory // TODO datatype properties
		for ( File file : FileUtils.listFiles(new File(BACKGROUND_KNOWLEDGE_OBJECT_PATH), FileFilterUtils.suffixFileFilter(".txt"), null)) {
		    
		    this.logger.info("Translating background knowledge file: " + file.getAbsolutePath());
		    
		    // open the english file and write the output in a file with the same name only in ./$language
		    BufferedFileReader reader = FileUtil.openReader(file.getAbsolutePath());
	        BufferedFileWriter writer = FileUtil.openWriter(TARGET_LANGUAGE_OUTPUT_PATH + file.getName(), "UTF-8", WRITER_WRITE_MODE.OVERRIDE);

	        String line = "";
	        while ((line = reader.readLine()) != null) {
	            
	            // read the background knowledge 
	            BackgroundKnowledge bk = BackgroundKnowledgeManager.getInstance().createBackgroundKnowledge(line, true);
	            
	            // uris of the background knowledge will always be NOT language specific: http://dbpedia.org/resource/Leipzig
	            String englishSubjectUriNoPrefix = bk.getSubjectUri().replace("http://dbpedia.org/resource/", "");
	            String foreignSubjectUri = englishUriToForeignUriMappingNoPrefix.get(englishSubjectUriNoPrefix);
	            
	            String englishObjectUriNoPrefix = bk.getObjectUri().replace("http://dbpedia.org/resource/", "");
                String foreignObjectUri = englishUriToForeignUriMappingNoPrefix.get(englishObjectUriNoPrefix);
	            
	            // we only include the current english triple if we have translations for subject and object
	            if (uriToLabelMapping.containsKey(foreignSubjectUri) ) {
	            	
	                bk.setSubjectPrefixAndLocalname(Constants.DBPEDIA_RESOURCE_PREFIX + foreignSubjectUri);
	                bk.setSubjectLabel(uriToLabelMapping.get(foreignSubjectUri));
	            }
	            if ( uriToLabelMapping.containsKey(foreignObjectUri)) {
	            	
	                bk.setObjectPrefixAndLocalname(Constants.DBPEDIA_RESOURCE_PREFIX + foreignObjectUri);
	                bk.setObjectLabel(uriToLabelMapping.get(foreignObjectUri));
	            }
	            
	            // first time takes forever because it loads the surface form file	                
                if( new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + NLPediaSettings.BOA_LANGUAGE + "_surface_forms.tsv").exists() ) {
                    
                	bk = SurfaceFormGenerator.getInstance().createSurfaceFormsForBackgroundKnowledge(bk);
                }
                else {
                    
                	HashSet<String> subjectSurfaceForm	= new HashSet<String>();
                	HashSet<String> objectSurfaceForm	= new HashSet<String>();
                	subjectSurfaceForm.add(" " + bk.getSubjectLabel().toLowerCase() + " ");
                	objectSurfaceForm.add(" " + bk.getObjectLabel().toLowerCase() + " ");
                	bk.setSubjectSurfaceForms(subjectSurfaceForm);
                	bk.setObjectSurfaceForms(objectSurfaceForm);
                }
                writer.write(bk.toString());
	        }
	        reader.close();
	        writer.close();
		}
		
		this.translateBackgroundKnowledgeFilesTime = System.currentTimeMillis() - startTranslateBackgroundKnowledgeFiles;
		this.logger.info("Finished translation of background knowledge from 'en' to " + NLPediaSettings.BOA_LANGUAGE + " in " + TimeUtil.convertMilliSeconds(this.translateBackgroundKnowledgeFilesTime));
	}

	/**
	 * 
	 * @param interlanguageLinksFile
	 * @param targetLanguage
	 * @return
	 */
	private Map<String, String> loadInterlanguageLinks(String interlanguageLinksFile, String targetLanguage) {
		
		Map<String,String> results = new HashMap<String,String>();
        
        NxParser nxp = NtripleUtil.openNxParser(interlanguageLinksFile);
        while (nxp.hasNext()) {

            Node[] ns = nxp.next();
            
            if ( !ns[2].toString().startsWith("http://" + targetLanguage + ".dbpedia.org/") ) continue;
            if ( results.size() == 10 ) {
            	
            	 System.out.println(results);
            	            }
            
            results.put(
            		ns[0].toString().replace(Constants.DBPEDIA_RESOURCE_PREFIX, ""), 
            		ns[2].toString().replace("http://" + targetLanguage + ".dbpedia.org/resource/", ""));
        }
        return results;
	}

	/**
	 * 
	 * @param languageFile
	 * @param targetLanguage
	 * @return
	 */
	private Map<String, String> loadTargetLanguageFile(String languageFile, String targetLanguage) {

		Map<String,String> results = new HashMap<String,String>();
        
        NxParser nxp = NtripleUtil.openNxParser(languageFile);
        while (nxp.hasNext()) {

            Node[] ns = nxp.next();
            
            if ( !ns[0].toString().startsWith("http://" + targetLanguage + ".dbpedia.org/resource/") ) continue;
//            if ( results.size() == 10 ) {
//            	
// System.out.println(results);
//            }
            
            results.put(ns[0].toString().replace("http://" + targetLanguage + ".dbpedia.org/resource/", ""), ns[2].toString());
        }
        return results;
	}

	@Override
	public String getReport() {

	    return "Finished translation of background knowledge from 'en' to '" + NLPediaSettings.BOA_LANGUAGE + "' in " + TimeUtil.convertMilliSeconds(this.translateBackgroundKnowledgeFilesTime);
	}

	@Override
	public void updateModuleInterchangeObject() {

	    // nothing to do here 
	}

	@Override
	public boolean isDataAlreadyAvailable() {
	    // create the output directory so we do not override the english data
	    if ( !new File(TARGET_LANGUAGE_OUTPUT_PATH).exists() ) new File(TARGET_LANGUAGE_OUTPUT_PATH).mkdir();

	    // lists all files in the directory which end with .txt and does not go into subdirectories
        return // true of more than one file is found
            FileUtils.listFiles(new File(TARGET_LANGUAGE_OUTPUT_PATH), FileFilterUtils.suffixFileFilter(".txt"), null).size() > 0;
	}

	@Override
	public void loadAlreadyAvailableData() {

		// nothing to do here
	}
}
