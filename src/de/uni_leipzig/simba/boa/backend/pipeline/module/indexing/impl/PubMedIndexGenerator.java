package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper.LuceneIndexType;
import de.uni_leipzig.simba.boa.backend.pipeline.module.preprocessing.AbstractPreprocessingModule;

public class PubMedIndexGenerator extends AbstractPreprocessingModule {
	private final static Logger log = LoggerFactory.getLogger(PubMedIndexGenerator.class);
	private File PUBMED_DIR = new File(NLPediaSettings.BOA_DATA_DIRECTORY+"pubmed/raw/");

	private final String INDEX_DIRECTORY = NLPediaSettings.BOA_DATA_DIRECTORY+Constants.INDEX_CORPUS_PATH;
	private final int RAM_BUFFER_MAX_SIZE = NLPediaSettings
			.getIntegerSetting("ramBufferMaxSizeInMb");
	private final boolean OVERWRITE_INDEX = this.overrideData;
	private XMLReader xmlReader;
	private int indexDocumentCount= 0;

	public void initParser(IndexWriter writer) {
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setEntityResolver(new DummyEntityResolver());
			xmlReader
					.setContentHandler(new PubMedArticleContentHandler(writer));
		} catch (SAXException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @return the dir
	 */
	public File getDir() {
		return PUBMED_DIR;
	}

	/**
	 * @param dir
	 *            the dir to set
	 */
	public void setDir(File dir) {
		this.PUBMED_DIR = dir;
	}

	@Override
	public String getName() {
		return "PubMed Index Generator";
	}

	private void parseFiles() {
		for (File file : PUBMED_DIR.listFiles()) {
			if(file.getName().contains("Korean")){
				continue;
			}
			for (File article : file.listFiles(new FilenameFilter() {

				public boolean accept(File file, String name) {
					if (name.endsWith(".nxml")) {
						return true;
					}
					return false;
				}
			})) {
				FileReader reader;
				try {
					reader = new FileReader(
							article);
					InputSource inputSource = new InputSource(reader);
					xmlReader.parse(inputSource);
					indexDocumentCount++;
				} catch (FileNotFoundException e) {
					log.error("Error by parsing file "+article.getAbsolutePath()+" "+e);
				} catch (IOException e) {
					log.error("Error by parsing file "+article.getAbsolutePath()+" "+e);
				} catch (SAXException e) {
					log.error("Error by parsing file "+article.getAbsolutePath()+" "+e);
				}
				if(indexDocumentCount % 1000 ==0){
					log.info(""+indexDocumentCount+" documents indexed");
				}
			}
		}
	}

	@Override
	public void run() {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_34, new LowerCaseWhitespaceAnalyzer());
		indexWriterConfig.setRAMBufferSizeMB(RAM_BUFFER_MAX_SIZE);
		indexWriterConfig
				.setOpenMode(OVERWRITE_INDEX
						|| !LuceneIndexHelper.isIndexExisting(INDEX_DIRECTORY) ? OpenMode.CREATE
						: OpenMode.APPEND);
		IndexWriter writer = LuceneIndexHelper.createIndex(INDEX_DIRECTORY,
				indexWriterConfig, LuceneIndexType.DIRECTORY_INDEX);
		this.initParser(writer);
		this.parseFiles();
	}

	@Override
	public String getReport() {
		return "A total of " + indexDocumentCount
				+ " documents has been indexed!";
	}

	@Override
	public void updateModuleInterchangeObject() {
		// nothing todo;

	}

	@Override
	public boolean isDataAlreadyAvailable() {
		return LuceneIndexHelper.isIndexExisting(INDEX_DIRECTORY);
	}

	@Override
	public void loadAlreadyAvailableData() {
		// TODO Auto-generated method stub

	}

}
