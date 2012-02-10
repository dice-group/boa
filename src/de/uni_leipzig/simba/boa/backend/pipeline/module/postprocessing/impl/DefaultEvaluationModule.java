/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.lucene.store.Directory;

import de.danielgerber.file.BufferedFileWriter;
import de.danielgerber.file.BufferedFileWriter.WRITER_WRITE_MODE;
import de.danielgerber.file.FileUtil;
import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationManager;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationIndexCreator;
import de.uni_leipzig.simba.boa.backend.evaluation.EvaluationResult;
import de.uni_leipzig.simba.boa.backend.evaluation.PrecisionRecallFMeasure;
import de.uni_leipzig.simba.boa.backend.evaluation.comparator.EvaluationResultComparator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.persistance.serialization.SerializationManager;
import de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.AbstractPostProcessingModule;
import de.uni_leipzig.simba.boa.backend.rdf.entity.Triple;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DefaultEvaluationModule extends AbstractPostProcessingModule {

    private NLPediaLogger logger = new NLPediaLogger(DefaultEvaluationModule.class);
    
    private long evaluationTime;
    private Double maxPrecision;
    private Double maxRecall;
    private Double maxFmeasure;

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default EvaluationManager Module";
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
        this.logger.info("Finished evalution in " + TimeUtil.convertMilliSeconds(this.evaluationTime) + ". P:" + maxPrecision + " R: " + maxRecall + " F: " + maxFmeasure);
    }

    private void runEvaluation() {

        EvaluationManager evalManager = new EvaluationManager();
        
        Map<Triple,String> triplesToSentences = evalManager.loadEvaluationSentences();
        Directory index = EvaluationIndexCreator.createGoldStandardIndex(new HashSet<String>(triplesToSentences.values()));
        List<EvaluationResult> results = new ArrayList<EvaluationResult>();
        
        // we want to see if the knowledge creation threshold, i.e. the pattern score produced by the NN matters
        for ( double patternScoreThreshold : Arrays.asList(0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D) ) {

            NLPediaSettings.setSetting("pattern.score.threshold.create.knowledge", String.valueOf(patternScoreThreshold));
            
            for ( int contextLookAhead : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7) ) {
                
                NLPediaSettings.setSetting("contextLookAhead", String.valueOf((contextLookAhead)));
                
                // filter out triples which might occur randomly, due to bad patterns
                for ( double tripleScoreThreshold : Arrays.asList(0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D) ) {
                    
                    NLPediaSettings.setSetting("triple.score.threshold.create.knowledge", String.valueOf(tripleScoreThreshold));
                    
                    Set<Triple> testData                            = evalManager.loadBoaResults(index, this.moduleInterchangeObject.getPatternMappings());
                    PrecisionRecallFMeasure precisionRecallFMeasure = new PrecisionRecallFMeasure(triplesToSentences.keySet(), testData);
                    
                    double precision    = precisionRecallFMeasure.getPrecision();
                    double recall       = precisionRecallFMeasure.getRecall();
                    double fMeasure     = precisionRecallFMeasure.getFMeasure();
                    
                    maxPrecision    = Math.max(maxPrecision,    precision);
                    maxRecall       = Math.max(maxRecall,       recall);
                    maxFmeasure     = Math.max(maxFmeasure,     fMeasure);
                    
                    results.add(new EvaluationResult().setContextLookAhead(contextLookAhead).setFMeasure(precisionRecallFMeasure.getFMeasure()).
                            setFoundTriples(testData.size()).setPatternThreshold(patternScoreThreshold).setPrecision(precision).setRecall(recall).
                            setTripleTreshold(tripleScoreThreshold).setAvailableTriples(triplesToSentences.keySet().size()));
                }
            }
        }
        // sort by fmeasure
        Collections.sort(results, new EvaluationResultComparator());
        // write evaluations results to file
        BufferedFileWriter writer = FileUtil.openWriter(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.EVALUATION_PATH + "Boa-Evaluation.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        for ( EvaluationResult result : results) {
            
            writer.write(result.toString());
        }
        writer.close();
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Finished evalution in " + TimeUtil.convertMilliSeconds(this.evaluationTime) + ". P:" + maxPrecision + " R: " + maxRecall + " F: " + maxFmeasure;
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

        for (File file : FileUtils.listFiles(new File(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.PATTERN_MAPPINGS_PATH), FileFilterUtils.suffixFileFilter(".bin"), null)) {
            
            PatternMapping mapping = SerializationManager.getInstance().deserializePatternMapping(file.getAbsolutePath());
            this.moduleInterchangeObject.getPatternMappings().add(mapping);
        }
    }
}
