/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.entity.pattern.Pattern;
import de.uni_leipzig.simba.boa.backend.entity.pattern.PatternMapping;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.pipeline.module.postprocessing.AbstractPostProcessingModule;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


/**
 * @author gerb
 *
 */
public class DefaultPatternIndexCreationModule extends AbstractPostProcessingModule {

    private final NLPediaLogger logger          = new NLPediaLogger(DefaultPatternIndexCreationModule.class);  
    
    protected String PATTERN_INDEX_DIRECTORY    = NLPediaSettings.BOA_DATA_DIRECTORY + "index/pattern/default/";
    private final int RAM_BUFFER_MAX_SIZE       = NLPediaSettings.getInstance().getIntegerSetting("ramBufferMaxSizeInMb");
    
    // the index where the patterns get scored
    protected Directory index;
    
    // for the report
    private long indexCreationTime  = 0;
    private long documentCount      = 0;
    
    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getName()
     */
    @Override
    public String getName() {

        return "Default Pattern Index Creation Module";
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#run()
     */
    @Override
    public void run() {
        
        long startIndexCreationTime = System.currentTimeMillis();
        this.logger.info("Starting to create pattern index!");
        
        // create the index writer configuration and create a new index writer
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
        indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_MAX_SIZE);
        indexWriterConfig.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = LuceneIndexHelper.createIndex(PATTERN_INDEX_DIRECTORY, indexWriterConfig);
        
        // index all the pattern in default settings
        for (PatternMapping mapping : this.moduleInterchangeObject.getPatternMappings()) {
            for (Pattern pattern : mapping.getPatterns()) {
                
                this.addPatternToIndex(writer, mapping, pattern);
            }
        }
        LuceneIndexHelper.closeIndexWriter(writer);
        
        this.indexCreationTime = System.currentTimeMillis() - startIndexCreationTime;
        this.logger.info("Creating pattern index took " + TimeUtil.convertMilliSeconds(this.indexCreationTime) + " with " + documentCount + " documents");
    }

    /**
     * 
     * @param writer
     * @param pattern
     */
    private void addPatternToIndex(IndexWriter writer, PatternMapping mapping, Pattern pattern) {
        
        this.documentCount++;
        LuceneIndexHelper.indexDocument(writer, this.createLuceneDocument(mapping, pattern));
    }

    /**
     * You need to override this method if you want to index a pattern
     * in a different way.
     * 
     * @param pattern
     * @return
     */
    protected Document createLuceneDocument(PatternMapping mapping, Pattern pattern) {

        Document doc = new Document();
        doc.add(new Field("uri",            mapping.getProperty().getUri(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("nlr-var",        pattern.getNaturalLanguageRepresentation(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("nlr-no-var",     pattern.getNaturalLanguageRepresentationWithoutVariables(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new NumericField("score",   Field.Store.YES, true).setDoubleValue(pattern.getScore()));
        
        return doc;
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#getReport()
     */
    @Override
    public String getReport() {

        return "Creating pattern index took " + TimeUtil.convertMilliSeconds(this.indexCreationTime) + " with " + documentCount + " documents";
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

        if ( this.moduleInterchangeObject.getPatternMappings() == null 
                || this.moduleInterchangeObject.getPatternMappings().size() == 0 ) 
            throw new RuntimeException("Indexing can not work without patterns! Interchange object contains no patterns!");
        
        return LuceneIndexHelper.isIndexExisting(PATTERN_INDEX_DIRECTORY);
    }

    /* (non-Javadoc)
     * @see de.uni_leipzig.simba.boa.backend.pipeline.module.PipelineModule#loadAlreadyAvailableData()
     */
    @Override
    public void loadAlreadyAvailableData() {

        this.index = LuceneIndexHelper.openIndex(PATTERN_INDEX_DIRECTORY);
    }
}
