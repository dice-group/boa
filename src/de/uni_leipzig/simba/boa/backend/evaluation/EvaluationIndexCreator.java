package de.uni_leipzig.simba.boa.backend.evaluation;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;

public class EvaluationIndexCreator {

    private static NLPediaLogger logger = new NLPediaLogger(EvaluationIndexCreator.class);
    
    public static Directory createGoldStandardIndex(Set<String> sentences) {

        String error = "There was a problem create the gold standard index!";
        Directory index = new RAMDirectory();
        
        try {

            // create the index writer configuration and create a new index writer
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
            indexWriterConfig.setOpenMode(OpenMode.CREATE);
            IndexWriter writer = new IndexWriter(index, indexWriterConfig);

            logger.info("Adding " + sentences.size() + " sentences to evaluation index!");

            for (String sentence : sentences) {

                Document doc = new Document();
                doc.add(new Field("sentence", sentence, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.NO));
                writer.addDocument(doc);
            }
            writer.optimize();
            writer.close();
        }
        catch (CorruptIndexException e) {
            
            logger.fatal(error, e);
            e.printStackTrace();
            throw new RuntimeException(error, e);
        }
        catch (LockObtainFailedException e) {
            
            logger.fatal(error, e);
            e.printStackTrace();
            throw new RuntimeException(error, e);
        }
        catch (IOException e) {
            
            logger.fatal(error, e);
            e.printStackTrace();
            throw new RuntimeException(error, e);
        }
        
        return index;
    }
}
