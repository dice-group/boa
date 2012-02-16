/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.dbpediaspotlight;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.danielgerber.rdf.NtripleUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.DBpediaSpotlightDownloadModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl.DBpediaSpotlightSurfaceFormModule;
import edu.stanford.nlp.util.StringUtils;


/**
 * @author gerb
 *
 */
public class DBpediaSpotlightSurfaceFormGenerator {
    
    private static final NLPediaLogger logger = new NLPediaLogger(DBpediaSpotlightSurfaceFormGenerator.class);
    
    private static final int MAXIMUM_SURFACE_FORM_LENGHT        = NLPediaSettings.getIntegerSetting("spotlight.maxSurfaceFormLength");
    public static final String SURFACE_FORMS_FILE               = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + NLPediaSettings.BOA_LANGUAGE + "_uri_surface_form.tsv";

    private static List<String> LOWERCASE_STOPWORDS             = null;
    private static final List<String> STOPWORDS                 = FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "dbpedia/" + NLPediaSettings.BOA_LANGUAGE + "/stopwords.txt", "UTF-8");
    private static final List<String> BLACKLISTED_URI_PATTERN   = FileUtil.readFileInList(NLPediaSettings.BOA_BASE_DIRECTORY + "dbpedia/" + NLPediaSettings.BOA_LANGUAGE + "/badUris.txt", "UTF-8");
    
    /**
     * 
     * @return
     */
    private static Set<String> createConceptUris() {
        
        Set<String> conceptUris = new HashSet<String>();
        Set<String> badUris = new HashSet<String>();
        badUris.addAll(NtripleUtil.getSubjectsFromNTriple(DBpediaSpotlightDownloadModule.DBPEDIA_REDIRECTS_FILE, ""));
        logger.info("Finished reading redirect file for bad uri detection!");
        badUris.addAll(NtripleUtil.getSubjectsFromNTriple(DBpediaSpotlightDownloadModule.DBPEDIA_DISAMBIGUATIONS_FILE, ""));
        logger.info("Finished reading disambiguations file for bad uri detection!");
        
        // every uri which looks like a good uri and is not in the disambiguations or redirect files is a concept uri
        NxParser n3Parser = NtripleUtil.openNxParser(DBpediaSpotlightDownloadModule.DBPEDIA_LABELS_FILE);
        while (n3Parser.hasNext()) {
            
            Node[] node = n3Parser.next();
            String subjectUri = node[0].toString();
            
            String subjectUriWihtoutPrefix = subjectUri.substring(subjectUri.lastIndexOf("/") + 1);
            
            if ( isGoodUri(subjectUriWihtoutPrefix) && !badUris.contains(subjectUriWihtoutPrefix) ) conceptUris.add(subjectUri);
        }
        logger.info("Concept Uris construction complete! Total of: " + conceptUris.size() + " concept URIs found!");
        return conceptUris;
    }
    
    private static void initStopwords() {
        
        List<String> lowerCaseStopWords = new ArrayList<String>();
        for ( String stopword : STOPWORDS ) {
            
            lowerCaseStopWords.add(stopword.toLowerCase());
        }
        LOWERCASE_STOPWORDS = lowerCaseStopWords;
        logger.info("There were " + LOWERCASE_STOPWORDS.size() + " lowercase stopwords found.");
    }
    
    public static void createSurfaceForms() {
        
        DBpediaSpotlightSurfaceFormGenerator.initStopwords();
        Set<String> conceptUris = DBpediaSpotlightSurfaceFormGenerator.createConceptUris();
        Map<String,Set<String>> surfaceForms = new HashMap<String,Set<String>>();
        
        // first add all uris of the concept uris
        for ( String uri : conceptUris ) {
            if ( !uri.contains("%") ) {
                
                addSurfaceForm(surfaceForms, uri, uri.substring(uri.lastIndexOf("/") + 1));
            }
        }
        logger.info("Finished adding all conceptUris: " + surfaceForms.size());
        
        List<String[]> subjectToObject = NtripleUtil.getSubjectAndObjectsFromNTriple(DBpediaSpotlightDownloadModule.DBPEDIA_DISAMBIGUATIONS_FILE, "");
        subjectToObject.addAll(NtripleUtil.getSubjectAndObjectsFromNTriple(DBpediaSpotlightDownloadModule.DBPEDIA_REDIRECTS_FILE, ""));
        
        for ( String[] subjectAndObject : subjectToObject ) {
            
            String object = subjectAndObject[1];
            String subject = subjectAndObject[0];
            
            if ( conceptUris.contains(object) && !object.contains("%") ) {
                
                addSurfaceForm(surfaceForms, object, subject.substring(subject.lastIndexOf("/") + 1));
            }
        }
        logger.info("Finished generation of surface forms.");
        
        // write the file
        BufferedFileWriter writer = FileUtil.openWriter(SURFACE_FORMS_FILE, "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        for (Map.Entry<String, Set<String>> entry : surfaceForms.entrySet()) {
            
            writer.write(entry.getKey() + "\t" + StringUtils.join(entry.getValue(), "\t"));
        }
        logger.info("Finished writing of surface forms to disk.");
    }
    
    private static void addSurfaceForm(Map<String, Set<String>> surfaceForms, String key, String value) {

        // clean and URL decode, whitespace removal
        String newSurfaceForm =  createCleanSurfaceForm(value);
        if ( newSurfaceForm != null ) {
            
            if ( surfaceForms.containsKey(key) ) surfaceForms.get(key).add(newSurfaceForm);
            else {
                
                Set<String> sfList = new HashSet<String>();
                sfList.add(newSurfaceForm);
                surfaceForms.put(key, sfList);
            }
        }
    }

    private static String createCleanSurfaceForm(String label) {

        try {
            
            String newLabel = URLDecoder.decode(label, "UTF-8");
            newLabel = newLabel.replaceAll("_", " ").replaceAll(" +", " ").trim();
            newLabel = newLabel.replaceAll(" \\(.+?\\)$", "");
            
            return isGoodSurfaceForm(newLabel) ? newLabel : null;
        }
        catch (IllegalArgumentException e) {
            
            String error = "Could not decode label: " + label + " with URLDecoder.";
            logger.error(error);
            return null;
        }
        catch (UnsupportedEncodingException e) {
            
            e.printStackTrace();
            String error = "Could not decode label: " + label + " with encoding UTF-8";
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * 
     * @param uri
     * @return
     */
    private static boolean isGoodUri(String uri) {

        if ( uri.contains("/") || uri.contains("%23") || uri.matches("^[\\W\\d]+$") ) {
            
            logger.debug("Uri: <" + uri + "> is not a good uri! / or %23 or regex");
            return false;
        }
        // prevents things like ^List or ^Disambiguation from being a good uri
        for ( String regex : BLACKLISTED_URI_PATTERN ) {
            
            if (uri.matches(regex)) {
                
                logger.debug("Uri: <" + uri + "> is not a good uri! bad pattern");
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean isGoodSurfaceForm(String surfaceForm) {
        
        if ( surfaceForm.length() > MAXIMUM_SURFACE_FORM_LENGHT 
                || surfaceForm.matches("^[\\W\\d]+$")) {
            
            logger.debug("Surfaceform: " + surfaceForm +  " is not a good surface form because its too long or regex match.");
            return false;
        }
        
        int i = 0;
        for ( String token : surfaceForm.toLowerCase().split(" ") ) {
            
            // current token is not a stopword
            if ( !LOWERCASE_STOPWORDS.contains(token) ) i++;
        }
        // at least one non stop word found
        if ( i > 0 ) return true;
        else {
            
            logger.debug("Surfaceform: " + surfaceForm +  " is not a good surface form because it contains only stop words.");
            return false;
        }
    }
}
