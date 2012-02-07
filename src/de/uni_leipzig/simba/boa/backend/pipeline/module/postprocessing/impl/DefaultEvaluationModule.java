/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationIndexCreator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.AbstractPostProcessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class DefaultEvaluationModule extends AbstractPostProcessingModule {

    private NLPediaLogger logger = new NLPediaLogger(DefaultEvaluationModule.class);
    
    private long evaluationTime;
    private Double precision;
    private Double recall;
    private Double fmeasure;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default Evaluation Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        long startEvaluation = System.currentTimeMillis();
        this.logger.info("Starting evaluation!");
        this.runEvaluation();
        this.evaluationTime = System.currentTimeMillis() - startEvaluation;
        this.logger.info("Finished evalution in " + TimeUtil.convertMilliSeconds(this.evaluationTime) + ". P:" + precision + " R: " + recall + " F: " + fmeasure);
    }

    private void runEvaluation() {

        Set<String> sentences = this.loadEvaluationSentences();
        EvaluationIndexCreator.createGoldStandardIndex(sentences);
        
    }

    public static void main(String[] args) {

        NLPediaSetup setup = new NLPediaSetup(true);
        DefaultEvaluationModule.loadEvaluationSentences();
    }
    
    private static Set<String> loadEvaluationSentences() {

        List<String> evaluationFiles = FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH + "Evaluation_3_Upmeier.txt", "UTF-8");
        evaluationFiles.addAll(FileUtil.readFileInList(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH + "Evaluation_3_Haack.txt", "UTF-8"));
        
        // clean this list of sentences
        List<String> cleanedEvaluationFiles = new ArrayList<String>();
        for (String line : evaluationFiles) {
            
            if ( line.startsWith("#") ) {
                                    
                if ( line.contains("http://dbpedia.org/ontology/") ) {
                    
                    line = line.replace("#", "").trim();
                }
                cleanedEvaluationFiles.add(line);
            }
            else if ( !line.trim().equals("") ) cleanedEvaluationFiles.add(line);
        }
        
        // now we can create the mapping
        Map<Triple,String> tripleToSentences = new HashMap<Triple,String>();
        Iterator<String> evalIterater = cleanedEvaluationFiles.iterator();
        
//        for ( String s : cleanedEvaluationFiles) System.out.println(s);
        
        String currentUri = "";
        
        while (evalIterater.hasNext() ) {
            
            String firstLine = evalIterater.next();
            
            if ( firstLine.startsWith("http://") ) { 
                
                currentUri = firstLine; 
                firstLine = evalIterater.next();
            }
            String secondLine = evalIterater.next();
            
            System.out.println(currentUri);
            System.out.println(firstLine);
            System.out.println(secondLine);
            
//            firstLine = firstLine.substring(firstLine.indexOf(".") + 1);
            
        }
        
        
        return null;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Finished evalution in " + TimeUtil.convertMilliSeconds(this.evaluationTime) + ". P:" + precision + " R: " + recall + " F: " + fmeasure;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#updateModuleInterchangeObject()
     */
    @Override
    public void updateModuleInterchangeObject() {

        // nothing to do here
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#isDataAlreadyAvailable()
     */
    @Override
    public boolean isDataAlreadyAvailable() {

        return false; // always run eval
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
    
    private class Triple {
        
        private String subjectUri;
        private String propertyUri;
        private String objectUri;
        
        
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {

            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((objectUri == null) ? 0 : objectUri.hashCode());
            result = prime * result + ((propertyUri == null) ? 0 : propertyUri.hashCode());
            result = prime * result + ((subjectUri == null) ? 0 : subjectUri.hashCode());
            return result;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {

            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Triple other = (Triple) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (objectUri == null) {
                if (other.objectUri != null)
                    return false;
            }
            else
                if (!objectUri.equals(other.objectUri))
                    return false;
            if (propertyUri == null) {
                if (other.propertyUri != null)
                    return false;
            }
            else
                if (!propertyUri.equals(other.propertyUri))
                    return false;
            if (subjectUri == null) {
                if (other.subjectUri != null)
                    return false;
            }
            else
                if (!subjectUri.equals(other.subjectUri))
                    return false;
            return true;
        }
        
        private DefaultEvaluationModule getOuterType() {

            return DefaultEvaluationModule.this;
        }
    }
}
