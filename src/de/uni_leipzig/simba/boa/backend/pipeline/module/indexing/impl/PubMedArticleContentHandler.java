package de.uni_leipzig.simba.boa.backend.pipeline.module.indexing.impl;

import java.util.ArrayList;

import org.apache.lucene.index.IndexWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.sentenceboundarydisambiguation.SentenceBoundaryDisambiguation;

public class PubMedArticleContentHandler extends DefaultWikiIndexingModule implements ContentHandler {

	private String currentValue;
	private StringBuilder builder = new StringBuilder();
	private IndexWriter writer;
	private SentenceBoundaryDisambiguation sbd = NaturalLanguageProcessingToolFactory
			.getInstance().createDefaultSentenceBoundaryDisambiguation();
	private IndexDocument document;
	private boolean pmid;
	private ArrayList<IndexDocument> documents;

	public PubMedArticleContentHandler(IndexWriter writer) {
		this.writer = writer;
		documents=new ArrayList<IndexDocument>();
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		currentValue = new String(ch, start, length);
		document.text.append(currentValue);
	}

	public void endDocument() throws SAXException {
		if (documents.size() == 1000) {
			indexDocuments(writer, documents);
			documents = new ArrayList<IndexDocument>();
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("body")) {
			documents.add(document);
		} else if (qName.equals("p")) {
			document.text.append("\n");
		} else if (qName.equals("title")) {
			document.text.append("\n");
		} else if(qName.equals("article-id")&& pmid){
			document.uri=currentValue;
		}

	}

	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}
	
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void startDocument() throws SAXException {
		document = new IndexDocument(sbd);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if (qName!=null&&qName.equals("body")) {
			builder = new StringBuilder();
		} else if (attributes!=null && qName.equals("article-id")
				&& attributes.getValue("pub-id-type").equals("pmid")) {
			pmid=true;
		}

	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

}
