/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;
import com.github.gerbsen.rdf.NtripleUtil;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.dbpediaspotlight.DBpediaSpotlightSurfaceFormGenerator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.interchangeobject.ModuleInterchangeObject;
import de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;
import edu.stanford.nlp.util.StreamGobbler;


/**
 * The purpose of this model is that it takes the data generated by the 
 * dbpedia extraction framework and then filters out all information
 * which describes resources which are just redirect or disambiguations.
 * Those don't need to be in the database where we get our BOA background
 * knowledge from. Also this class handles the import of the filtered 
 * data into a local virtuoso via isql and a command line call!
 * 
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DBpediaImportModule extends AbstractPreprocessingModule {

    static NLPediaSetup setup = new NLPediaSetup(true);
    private final NLPediaLogger logger = new NLPediaLogger(DBpediaImportModule.class);
    private long duration;
    
    public static final String DBPEDIA_MAPPING_PROPERTIES_FILE  = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH + "mappingbased_properties_"+NLPediaSettings.BOA_LANGUAGE+".nt";
    public static final String DBPEDIA_LABELS_FILE              = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH + "labels_"+NLPediaSettings.BOA_LANGUAGE+".nt";
    public static final String DBPEDIA_INSTANCE_TYPES_FILE      = NLPediaSettings.BOA_DATA_DIRECTORY + Constants.DBPEDIA_DUMP_PATH + "instance_types_"+NLPediaSettings.BOA_LANGUAGE+".nt";
    
    @Override
    public String getName() {

        return "DBpedia Import Module";
    }
    
    public static void main(String[] args) {

        DBpediaImportModule module = new DBpediaImportModule();
        module.run();
    }

    @Override
    public void run() {

        long start = System.currentTimeMillis();
        this.filterDbpedia();
        this.importDbpedia();
        duration = System.currentTimeMillis() - start; 
    }

    /**
     * Please make sure virtuoso has read/write access to the working directory!
     */
    private void importDbpedia() {

        try {
            
            String ontologyImportString = "/usr/local/bin/isql 1111 dba dba \"EXEC=TTLP_MT(file_to_string_output('" + NLPediaSettings.BOA_BASE_DIRECTORY + Constants.BACKGROUND_KNOWLEDGE_PATH + "dbpedia_3.7.owl'), '', '"+NLPediaSettings.getSetting("importGraph")+"', 255)\"";
            Process ontologyImport = Runtime.getRuntime().exec(ontologyImportString);
            
            System.out.println(ontologyImportString+"\n");
            
            StreamGobbler errorGobbler = new 
                    StreamGobbler(ontologyImport.getErrorStream(), new PrintWriter(System.out));            
                
                // any output?
                StreamGobbler outputGobbler = new 
                    StreamGobbler(ontologyImport.getInputStream(), new PrintWriter(System.out));
                    
                // kick them off
                errorGobbler.start();
                outputGobbler.start();
            
            if ( ontologyImport.waitFor() != 0 ) {
                
//                BufferedReader input = new BufferedReader(new InputStreamReader(ontologyImport.getInputStream()));
                
                PrintStream prtStrm = System.out;
                prtStrm = new PrintStream(ontologyImport.getOutputStream());
                prtStrm.println();
                throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!" + ontologyImport);
            }
            this.logger.info("Import of dbpedia ontology file into virtuoso complete!");
            
//            Process mappingImport = Runtime.getRuntime().exec("/usr/local/bin/isql 1111 dba dba " +
//                    "\"EXEC=TTLP_MT(file_to_string_output('" + DBPEDIA_MAPPING_PROPERTIES_FILE.replace(".nt", "_filtered.nt") + "'), '', '"+NLPediaSettings.getSetting("importGraph")+"', 255)\"");
//            if ( mappingImport.waitFor() != 0 ) throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!");
//            this.logger.info("Import of mapping file into virtuoso complete!");
//            
//            Process labelImport = Runtime.getRuntime().exec("/usr/local/bin/isql 1111 dba dba " +
//                    "\"EXEC=TTLP_MT(file_to_string_output('" + DBPEDIA_LABELS_FILE.replace(".nt", "_filtered.nt") + "'), '', '"+NLPediaSettings.getSetting("importGraph")+"', 255)\"");
//            if ( labelImport.waitFor() != 0 ) throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!");
//            this.logger.info("Import of labels file into virtuoso complete!");
//            
//            Process typesImport = Runtime.getRuntime().exec("/usr/local/bin/isql 1111 dba dba " +
//                    "\"EXEC=TTLP_MT(file_to_string_output('" + DBPEDIA_INSTANCE_TYPES_FILE.replace(".nt", "_filtered.nt") + "'), '', '"+NLPediaSettings.getSetting("importGraph")+"', 255)\"");
//            if ( typesImport.waitFor() != 0 ) throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!");
//            this.logger.info("Import of types file into virtuoso complete!");
        }
        catch (IOException e) {
            
            throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!", e);
        }
        catch (InterruptedException e) {
            
            throw new RuntimeException("Please make sure virtuoso has read/write access to the working directory!", e);
        }
    }

    /**
     * Creates filtered versions of dbpedia. also the filtering is only applied 
     * if a file could not be found, meaning was not filtered in a previous run.
     *  
     */
    private void filterDbpedia() {

        // load surface forms, every uri which has surface forms is a good uri
        DBpediaSpotlightSurfaceFormGenerator generator = new DBpediaSpotlightSurfaceFormGenerator();
        Set<String> resourceUris = new HashSet<String>();//generator.createSurfaceForms().keySet();
        
        if ( !new File(DBPEDIA_MAPPING_PROPERTIES_FILE.replace(".nt", "_filtered.nt")).exists() ) {
        
            // filter mappings
            this.logger.info("Start filtering of mappings file!");
            BufferedFileWriter writer = new BufferedFileWriter(DBPEDIA_MAPPING_PROPERTIES_FILE.replace(".nt", "_filtered.nt"), "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
            NxParser nxp = NtripleUtil.openNxParser(DBPEDIA_MAPPING_PROPERTIES_FILE);
            while (nxp.hasNext()) {

                Node[] ns = nxp.next();
                if ( resourceUris.contains(ns[0].toString()) ) writer.write(String.format("%s %s %s .", ns[0].toN3(), ns[1].toN3(), ns[2].toN3()));
            }
            writer.close();
        }
        else this.logger.info("Skipping filtering of mappings file!");
        
        if ( !new File(DBPEDIA_LABELS_FILE.replace(".nt", "_filtered.nt")).exists() ) {
            
            // filter labels
            this.logger.info("Start filtering of labels file!");
            BufferedFileWriter writer = new BufferedFileWriter(DBPEDIA_LABELS_FILE.replace(".nt", "_filtered.nt"), "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
            NxParser nxp = NtripleUtil.openNxParser(DBPEDIA_LABELS_FILE);
            while (nxp.hasNext()) {

                Node[] ns = nxp.next();
                if ( resourceUris.contains(ns[0].toString()) ) writer.write(String.format("%s %s %s .", ns[0].toN3(), ns[1].toN3(), ns[2].toN3()));
            }
            writer.close();
        }
        else this.logger.info("Skipping filtering of labels file!");
        
        if ( !new File(DBPEDIA_INSTANCE_TYPES_FILE.replace(".nt", "_filtered.nt")).exists() ) {
            
            // filter instances
            this.logger.info("Start filtering of types file!");
            BufferedFileWriter writer = new BufferedFileWriter(DBPEDIA_INSTANCE_TYPES_FILE.replace(".nt", "_filtered.nt"), "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
            NxParser nxp = NtripleUtil.openNxParser(DBPEDIA_INSTANCE_TYPES_FILE);
            while (nxp.hasNext()) {

                Node[] ns = nxp.next();
                if ( resourceUris.contains(ns[0].toString()) ) writer.write(String.format("%s %s %s .", ns[0].toN3(), ns[1].toN3(), ns[2].toN3()));
            }
            writer.close();
        }
        else this.logger.info("Skipping filtering of typesfile!");
    }

    @Override
    public String getReport() {

        return "Filtered DBpedia Knowledge and imported it into Virtuoso in " + TimeUtil.convertMilliSeconds(duration);
    }

    @Override
    public void updateModuleInterchangeObject() {

        // nothing to do here
    }

    @Override
    public boolean isDataAlreadyAvailable() {

        // well, we could do some sort of sparql query but this is rather complicated 
        return false;
    }

    @Override
    public void loadAlreadyAvailableData() {

        // TODO not possible
    }
}
