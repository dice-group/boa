package de.uni_leipzig.simba.boa.backend.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.backgroundknowledge.BackgroundKnowledge;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.lucene.LowerCaseWhitespaceAnalyzer;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.NaturalLanguageProcessingToolFactory;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.partofspeechtagger.PartOfSpeechTagger;
import de.uni_leipzig.simba.boa.backend.search.PatternSearcher;
import de.uni_leipzig.simba.boa.backend.search.result.SearchResult;

/**
 * 
 * @author Daniel Gerber
 */
public class DefaultPatternSearcher implements PatternSearcher {

    static NLPediaSetup s = new NLPediaSetup(true);
    
    protected final static int MAX_PATTERN_CHUNK_LENGTH  = NLPediaSettings.getIntegerSetting("maxPatternLenght");
    private final static int MIN_PATTERN_CHUNK_LENGTH  = NLPediaSettings.getIntegerSetting("minPatternLenght");
    private final static int MAX_NUMBER_OF_DOCUMENTS   = NLPediaSettings.getIntegerSetting("maxNumberOfDocuments");

    protected PartOfSpeechTagger posTagger;

    private final Analyzer analyzer;
    protected IndexSearcher indexSearcher;
    private final QueryParser parser;
    
    private final NLPediaLogger logger = new NLPediaLogger(DefaultPatternSearcher.class);

    public DefaultPatternSearcher() {

        // create index searcher in read only mode
        this.analyzer         = new LowerCaseWhitespaceAnalyzer();
        this.parser           = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);
    }
    
    public DefaultPatternSearcher(String indexDir) {

        this.analyzer = new LowerCaseWhitespaceAnalyzer();

        // create index searcher in read only mode
        this.indexSearcher = LuceneIndexHelper.openIndexSearcher(LuceneIndexHelper.openIndex(indexDir), true);
        this.parser = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);
    }
    
    public DefaultPatternSearcher(Directory indexDir) {

        this.analyzer = new LowerCaseWhitespaceAnalyzer();

        // create index searcher in read only mode
        this.indexSearcher = LuceneIndexHelper.openIndexSearcher(indexDir, true);
        this.parser = new QueryParser(Version.LUCENE_34, "sentence", this.analyzer);
    }
    
    @Override
    public void setIndex(Directory index) {

        this.indexSearcher = LuceneIndexHelper.openIndexSearcher(index, true);
    }
    
    public void init(){
    	
    	if ( this.indexSearcher == null ) {
    		
    		this.indexSearcher    = LuceneIndexHelper.getIndexSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH);
    	}
    }
    
    /**
     * Returns the sentence index by the given id.
     * 
     * @param id
     * @return
     */
    public String getSentencesByID(Integer id) {

        try {

        	init();
            return this.indexSearcher.doc(id).get("sentence");
        }
        catch (CorruptIndexException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Returns the sentences from the index with the given ids. Uses the method
     * DefaultPatternSearcher.getSentencesByID() to query the index
     * 
     * @param listOfIds
     * @return
     */
    public List<String> getSentencesByIds(List<Integer> ids) {

        List<String> sentences = new ArrayList<String>();
        for (Integer id : ids) {

            sentences.add(this.getSentencesByID(id));
        }
        return sentences;
    }

    /**
     * Query the index with two search terms.
     * 
     * @param string
     * @param string2
     * @return 
     * @throws ParseException
     *             , IOException
     */
    public Collection<SearchResult> queryBackgroundKnowledge(BackgroundKnowledge backgroundKnowledge) {

    	init();
        List<SearchResult> results = new ArrayList<SearchResult>();

        Set<String> firstLabels =  backgroundKnowledge.getSubjectSurfaceForms();
        Set<String> secondLabels = backgroundKnowledge.getObjectSurfaceForms();
        
        // combine the list to make processing a little easier
        Set<String> allLabels = new HashSet<String>(firstLabels);
        allLabels.addAll(secondLabels);
        
        if ( firstLabels.size() == 0 || secondLabels.size() == 0 ) { 
            
            this.logger.debug("Surface forms were empty, first: " + firstLabels + " second: " + secondLabels);
            return results;
        }
        
        // nested boolean query: (label1 or label2 or label3) and (label4 or label5)
        Query q = this.parseQuery("sentence:(" + StringUtils.join(escapeList(firstLabels), " OR ") + ")" +  
                                    " AND " +
                                  "sentence:(" + StringUtils.join(escapeList(secondLabels), " OR ") + ")");
        
        Map<Integer,Set<String>> luceneDocIdsToPatterns = new HashMap<Integer,Set<String>>();
        
        // go through all sentences and surface form combinations 
        for ( ScoreDoc hit : this.searchIndexWithoutFilter(q, MAX_NUMBER_OF_DOCUMENTS) ) {
            
            String sentence     = this.getSentenceFromIndex(hit);
            
            for (String firstLabel : firstLabels) {
                for (String secondLabel : secondLabels) {
                    
                    List<String> currentMatches = findMatchedText(sentence, firstLabel, secondLabel);
                    
                    if (!currentMatches.isEmpty()) 
                        this.addSearchResults(results, currentMatches, firstLabel.trim(), 
                                secondLabel.trim(), backgroundKnowledge, sentence, hit.doc, allLabels, luceneDocIdsToPatterns);
                }
            }
        }
        logger.debug("Found " + results.size() + " results!");
        
        return results;
    }
    
    /**
     * 
     * @param hits
     * @return
     */
    protected Set<String> getSentencesFromIndex(ScoreDoc[] hits) {
        
        Set<String> sentences = new HashSet<String>();
        
        // collect all sentences
        for ( int n = 0 ; n < hits.length; n++){
            
            sentences.add(hits[n].doc + " " + LuceneIndexHelper.getFieldValueByDocId(this.indexSearcher, hits[n].doc, "sentence"));
        }
        return sentences;
    }
    
    /**
     * 
     * @param hit
     * @return
     */
    protected String getSentenceFromIndex(ScoreDoc hit) {
        
        return LuceneIndexHelper.getFieldValueByDocId(this.indexSearcher, hit.doc, "sentence");
    }

    /**
     * 
     * @param sentence
     * @param firstLabel
     * @param secondLabel
     * @return
     */
    protected List<String> findMatchedText(final String sentence, final String firstLabel, final String secondLabel){
        
        final String sentenceLowerCase    = sentence.toLowerCase();
        List<String> currentMatches = new ArrayList<String>();
        
        // subject comes first
        String[] match1 = StringUtils.substringsBetween(sentenceLowerCase, firstLabel, secondLabel);
        if (match1 != null) {

            for (int j = 0; j < match1.length; j++) 
                currentMatches.add("?D? " + match1[j].trim() + " ?R?");
        }
        // object comes first
        String[] match2 = StringUtils.substringsBetween(sentenceLowerCase, secondLabel, firstLabel);
        if (match2 != null) {

            for (int j = 0; j < match2.length; j++) 
                currentMatches.add("?R? " + match2[j].trim() + " ?D?");
        }

        return currentMatches;
    }
    
    /**
     * 
     * @param q
     * @param maxNumberOfDocuments
     * @return
     */
    private ScoreDoc[] searchIndexWithoutFilter(Query q, int maxNumberOfDocuments) {

        try {
            init();
            return indexSearcher.search(q, maxNumberOfDocuments).scoreDocs;
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not query index for query: \"" + q.toString() + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Create the search results
    
     * @param results 
     * @param currentMatches
     * @param allLabels 
     * @param triple
     * @param hit
     * @param isSubject
     */
    private void addSearchResults(List<SearchResult> results, List<String> currentMatches, String subjectLabel, 
            String objectLabel, BackgroundKnowledge backgroundKnowledge, String sentenceNormalCase, 
            Integer sentenceId, Set<String> allLabels, Map<Integer, Set<String>> luceneDocIdsToPatterns) {

        for (String match : currentMatches) {

        	try {
        		
        		String nlr = this.getCorrectCaseNLR(sentenceNormalCase.toLowerCase(), sentenceNormalCase, match, allLabels);
                
                // we already have the pattern for the same sentence in the list
                if ( luceneDocIdsToPatterns.containsKey(sentenceId) && luceneDocIdsToPatterns.get(sentenceId).contains(nlr) ) 
                    continue;
                // the pattern was not found so far in this sentence
                else {
                    
                    // but only for those who are suitable
                    if ( !match.isEmpty() && this.isPatternSuitable(nlr) ) {

                        SearchResult result = new SearchResult();
                        result.setProperty(backgroundKnowledge.getProperty().getUri());
                        result.setSentence(sentenceId);
                        result.setNaturalLanguageRepresentation(nlr);
                        // the subject of the triple is the domain of the property so,
                        // replace every occurrence with ?D?
                        if (nlr.startsWith("?D?")) {
                            result.setFirstLabel(subjectLabel);
                            result.setSecondLabel(objectLabel);
                        }
                        else {
                            result.setFirstLabel(objectLabel);
                            result.setSecondLabel(subjectLabel);
                        }
                        results.add(result);
                        
                        if ( luceneDocIdsToPatterns.containsKey(sentenceId) ) luceneDocIdsToPatterns.get(sentenceId).add(nlr);
                        else luceneDocIdsToPatterns.put(sentenceId, new HashSet<String>(Arrays.asList(nlr)));
                    }
                }
        	}
        	catch ( StringIndexOutOfBoundsException aioobe) {
        		
        		logger.warn("Could not get correct case NLR for: \""  + match + "\" in sentence: " + sentenceNormalCase);
        	}
        }
    }

    protected String getCorrectCaseNLR(String lowerCase, String normalCase, String pattern, Set<String> allLabels) {

        String firstVariable = pattern.substring(0, 3);
        String secondVariable = pattern.substring(pattern.length() - 3, pattern.length());

        // remove ?D? and ?R?
        pattern = pattern.substring(0, pattern.length() - 3).substring(3).trim();
        // get the start of the pattern and go until its end
        int start = lowerCase.indexOf(pattern);
        int end = start + pattern.length();

        String nlr = normalCase.substring(start, end);
        
        // sometimes the surface form is part of the pattern, so we need to cut this out
        for ( String label : allLabels ) { label = label.toLowerCase();
            for (String part : label.split(" ") ) {
                
                    // starts with the part of the label
                    // remove the label and only match complete words (" " ensures that a word ends)
                    if ( nlr.regionMatches(true, 0, part + " ", 0, part.length() + 1) )
                        nlr = nlr.substring(part.length());
                    
                    // ends with the part
                    if ( nlr.matches("(?i).*" + Pattern.quote(" " + part)))
                        nlr = nlr.replaceAll("(?i)" + Pattern.quote(" " + part) + "$", "");
            }
        }
        return firstVariable + " " + nlr.trim() + " " + secondVariable;
    }
    
    public static void main(String[] args) throws ParseException {

        DefaultPatternSearcher searcher = new DefaultPatternSearcher();
        System.out.println(searcher.getSentencesByID(25608892));
    }
    
    private static Set<String> escapeList(Set<String> tokens) {
        
        Set<String> labels = new HashSet<String>();
        for ( String label : tokens ) {
            
            labels.add("\"" + QueryParser.escape(label) + "\"");
        }
        return labels;
    }

    public boolean isPatternSuitable(String naturalLanguageRepresentation) {

        String patternWithoutVariables = naturalLanguageRepresentation.substring(0, naturalLanguageRepresentation.length() - 3).substring(3).trim();

        // patterns are only allowed to have 256 characters
        if (patternWithoutVariables.trim().length() < 2 || naturalLanguageRepresentation.length() > 256 || naturalLanguageRepresentation.isEmpty())
            return false;

        // pattern need to start with either ?D? or ?R? and have to end with ?D?
        // or ?R?
        if ((!naturalLanguageRepresentation.startsWith("?D?") && !naturalLanguageRepresentation.startsWith("?R?"))
                || (!naturalLanguageRepresentation.endsWith("?D?") && !naturalLanguageRepresentation.endsWith("?R?")))
            return false;
        
        // patterns need to have only one domain and only one range
        if (StringUtils.countMatches(naturalLanguageRepresentation, "?D?") != 1 || StringUtils.countMatches(naturalLanguageRepresentation, "?R?") != 1)
            return false;

        // patterns need to be bigger/equal than min chunk size and
        // smaller/equal then max chunk size
        // true or correct if the number of stop-words in the pattern is not
        // equal to the number of tokens
        Set<String> naturalLanguageRepresentationChunks = new HashSet<String>(Arrays.asList(patternWithoutVariables.toLowerCase().trim().split(" ")));
        if (naturalLanguageRepresentationChunks.size() > MAX_PATTERN_CHUNK_LENGTH || naturalLanguageRepresentationChunks.size() < MIN_PATTERN_CHUNK_LENGTH || patternWithoutVariables.trim().isEmpty())
            return false;
        
        // patterns containing only stop-words can't be used, because they are
        // way to general
		// @author Maciej Janicki -- this feature can be switched on/off in settings
//		if (NLPediaSettings.getBooleanSetting("removeStopWordsFromPatterns")) {
			naturalLanguageRepresentationChunks.removeAll(Constants.STOP_WORDS);
			if (naturalLanguageRepresentationChunks.size() == 0)
				return false;
//		}
		
        // patterns shall not start with "and" or "and ," because this is the
        // conjunction of sentences and does not carry meaning
        if (patternWithoutVariables.startsWith("and ") || patternWithoutVariables.startsWith("and,") || patternWithoutVariables.startsWith("and ,"))
            return false;
        
        return true;
    }

    /**
     * Closes the current pattern searcher.
     */
    public void close() {

        try {
            
            this.indexSearcher.close();
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not close index!";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    public Set<String> getExactMatchSentences(String keyphrase, int maxNumberOfDocuments) {

        ScoreDoc[] hits = this.searchIndexWithoutFilter(this.parseQuery("+sentence:\"" + QueryParser.escape(keyphrase) + "\""), maxNumberOfDocuments);
        TreeSet<String> list = new TreeSet<String>();

        // reverse order because longer sentences come last, longer sentences
        // most likely contain less it,he,she
        for (int i = hits.length - 1; i >= 0; i--) {

            // get the indexed string and put it in the result
            list.add(this.getIndexDocument(hits[i], "sentence"));
        }
        return list;
    }
    
    public Map<String,String> getExactMatchSentencesTagged(String keyphrase, int maxNumberOfDocuments) {

        ScoreDoc[] hits = this.searchIndexWithoutFilter(this.parseQuery("+sentence:\"" + QueryParser.escape(keyphrase) + "\""), maxNumberOfDocuments);
        Map<String,String> list = new LinkedHashMap<String,String>();

        // reverse order because longer sentences come last, longer sentences
        // most likely contain less it,he,she
        for (int i = hits.length - 1; i >= 0; i--) {

            // get the indexed string and put it in the result
            list.put(this.getIndexDocument(hits[i], "sentence"), this.getIndexDocument(hits[i], "ner"));
        }
        return list;
    }
    
    public Set<String> getExactMatchSentencesForLabels(String label1, String label2, int maxNumberOfDocuments) {
        
        Query query = this.parseQuery("+sentence:\"" + QueryParser.escape(label1) + "\" && +sentence:\"" + QueryParser.escape(label2) + "\"");
        ScoreDoc[] hits = this.searchIndex(query, null, maxNumberOfDocuments);
        TreeSet<String> list = new TreeSet<String>();
        
        // reverse order because longer sentences come last, longer sentences
        // most likely contain less it,he,she
        for (int i = hits.length - 1; i >= 0; i--) {

            // get the indexed string and put it in the result
            list.add(this.getIndexDocument(hits[i], "sentence"));
        }
        return list;
    }
    
    /**
     * Get's the documents value for field "fieldname" from the index.
     * 
     * @param document - the found document
     * @param fieldname - the fieldname which should be used to get the document value
     * @return the documents value for the fieldname
     */
    private String getIndexDocument(ScoreDoc document, String fieldname) {

        try {
            
            return indexSearcher.doc(document.doc).get(fieldname);
        }
        catch (CorruptIndexException e) {
            
            e.printStackTrace();
            String error = "Could not get document " + document.doc + "(field:"+fieldname+") from index.";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not get document " + document.doc + "(field:"+fieldname+") from index.";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * Method to query a Lucene Index.
     * 
     * @param query - the query to query
     * @param filter - a Lucene filter
     * @param maxNumberOfDocuments - max number of returned documents
     * @return a list of all matching documents
     */
    private ScoreDoc[] searchIndex(Query query, Filter filter, int maxNumberOfDocuments) {
        
        try {
            
            return indexSearcher.search(query, filter, maxNumberOfDocuments).scoreDocs;
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not query index for query: \"" + query.toString() + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * Query an index to know how many hits we have 
     * 
     * @param query - the query to execute
     * @param collector - the document collector
     */
    @Override
    public int getTotalHits(String pattern) {

        try {
            
            Query query = this.parseQuery("+sentence:\"" + QueryParser.escape(pattern) + "\"");
            TotalHitCountCollector thcc = new TotalHitCountCollector();
            indexSearcher.search(query, thcc);
            
            return thcc.getTotalHits();
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not get total his for pattern: \"" + pattern.toString() + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    /**
     * Method to parse a given string to a Lucene query
     * 
     * @param query - the query to parse
     * @return returns a parsed query for the given string
     * @throws RuntimeException if something goes wrong
     */
    private Query parseQuery(String query){
        
        try {
            
            return this.parser.parse(query);
        }
        catch (ParseException e) {
            
            e.printStackTrace();
            String error = "Could not parse query: \"" + query + "\"";
            this.logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    
    public Set<String> getFieldValueByIds(Set<Integer> ids, String fieldname) {

        Set<String> sentences = new HashSet<String>();
        for (Integer id : ids) {

            sentences.add(getFieldValueByDocId(this.indexSearcher, id, fieldname));
        }
        return sentences;
    }
    
    public String getFieldValueByDocId(IndexSearcher searcher, Integer id, String fieldName) {

        try {

            return searcher.doc(id).get(fieldName);
        }
        catch (CorruptIndexException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            throw new RuntimeException(error, e);
        }
        catch (IOException e) {
            
            e.printStackTrace();
            String error = "Could not get document with id: " + id + " from index.";
            throw new RuntimeException(error, e);
        }
    }

	public Collection<? extends String> getSentencesWithLimit(Set<Integer> foundInSentences, int maxNumberOfEvaluationSentences) {
		
		Set<String> sentences = new HashSet<String>();
        for (Integer id : foundInSentences) {

            sentences.add(getFieldValueByDocId(this.indexSearcher, id, "sentence"));
            if ( sentences.size() >= maxNumberOfEvaluationSentences) break;
        }
        return sentences;
	}
}
