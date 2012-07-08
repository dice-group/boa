/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.impl;

import java.io.File;

import de.uni_leipzig.simba.boa.backend.dbpediaspotlight.DBpediaSpotlightSurfaceFormGenerator;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;

/**
 * @author Daniel Gerber <dgerber@informatik.uni-leipzig.de>
 */
public class DBpediaSpotlightSurfaceFormModule extends AbstractPreprocessingModule {

    private final NLPediaLogger logger = new NLPediaLogger(DBpediaSpotlightSurfaceFormModule.class);
    private long dBpediaSpotlightSurfaceFormGenerationFilesTime;
    
    public static final String DBPEDIA_PREFIX = "http://dbpedia.org/resource/";
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "DBpedia Spotlight Surface Form Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {

        long startDBpediaSpotlightSurfaceFormGenerationFiles = System.currentTimeMillis();
        this.logger.info("Starting to generate surface form file.");
        
        DBpediaSpotlightSurfaceFormGenerator surfaceFormGenerator= new DBpediaSpotlightSurfaceFormGenerator();
        surfaceFormGenerator.createSurfaceForms();
        
        this.dBpediaSpotlightSurfaceFormGenerationFilesTime = System.currentTimeMillis() - startDBpediaSpotlightSurfaceFormGenerationFiles;
        this.logger.info("Finished surface form generation in " + TimeUtil.convertMilliSeconds(this.dBpediaSpotlightSurfaceFormGenerationFilesTime));
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Finished surface form generation in " + TimeUtil.convertMilliSeconds(this.dBpediaSpotlightSurfaceFormGenerationFilesTime);
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

        return new File(DBpediaSpotlightSurfaceFormGenerator.SURFACE_FORMS_FILE).exists();
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        // nothing to do here
    }
}
