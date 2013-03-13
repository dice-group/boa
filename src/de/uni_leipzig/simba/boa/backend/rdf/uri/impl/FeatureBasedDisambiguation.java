/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uni_leipzig.simba.boa.backend.rdf.uri.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

import com.github.gerbsen.encoding.Encoder;
import com.github.gerbsen.encoding.Encoder.Encoding;
import com.github.gerbsen.lucene.LuceneManager;
import com.github.gerbsen.math.Frequency;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.lucene.LuceneIndexHelper;

/**
 *
 */
public class FeatureBasedDisambiguation {

    public static Logger logger = java.util.logging.Logger.getLogger(FeatureBasedDisambiguation.class.getName());
//    public LuceneDbpediaManager dbpediaManager;
//    public LuceneBoaManager boaManager = new LuceneBoaManager();
	private Map<String,Map<String,Integer>> contextEntityCache = new HashMap<String, Map<String, Integer>>();
	private Map<String,List<String>> uriCandidatesCache = new HashMap<String, List<String>>();
	private Map<String,Double> aprioriScoreCache = new HashMap<String, Double>();
	private Map<String,Set<String>> surfaceFormsCache = new HashMap<String, Set<String>>();
	
	public Frequency score = new Frequency();
	public Frequency apriori = new Frequency();
	public Frequency local = new Frequency();
	public Frequency global = new Frequency();
	public Frequency stringsim = new Frequency();
	
	 DecimalFormat df = new DecimalFormat("#.###");
	private Double contextGlobalParameter = 0.1;
	private Double contextLocalParameter = 0.2;
	private Double aprioriParameter = 0.5;
	private Double stringSimParameter = 0.2;
	private double urlScoreThreshold = 0.2;
	
	private IndexSearcher boaSearcher;
	private IndexSearcher dbpediaSearcher;
	
    public FeatureBasedDisambiguation() {
    	
        this.boaSearcher         = LuceneIndexHelper.getIndexSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH);
        this.dbpediaSearcher	 = LuceneIndexHelper.getIndexSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_RESOURCES_PATH);
	}
    
    /**
     * 
     */
    public void close() {
    	
    	try {
			this.boaSearcher.close();
			this.dbpediaSearcher.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**
     * Check for URIs that contain entry as substring
     *
     * @param label Label of entity
     * @return List of mapping URIs. Score is 1 (perfect match)
     */
    public List<String> getUriCandidates(String label) {

    	if ( !this.uriCandidatesCache.containsKey(label) ) this.uriCandidatesCache.put(label,this.getUriForSurfaceForm(label, dbpediaSearcher));
    	return this.uriCandidatesCache.get(label);
    }

    /**
     * Computes to a-priori score based on a single uri
     *
     * @param uri URI of the resource
     * @return A-priori score
     */
    public double getAprioriScore(String uri) {
    	
    	if ( !aprioriScoreCache.containsKey(uri) ) this.aprioriScoreCache.put(uri, getAprioriScoreForUri(uri, dbpediaSearcher)); 
    	return this.aprioriScoreCache.get(uri);
    }

    public String getUri(String label, String secondEntity, List<String> contextEntitiesInArticle) {
    	
    	List<String> uris = getUriCandidates(StringUtils.countMatches(label, ".") > 1 ? label.replace(".", "") : label);
    	
        // if we dont find uri candidates, we need to generate our own based on dbpedia style
        if (uris.isEmpty()) 
            return Constants.BOA_PREFIX + Encoder.urlEncode(label.replace(" ", "_"), Encoding.UTF_8);
        else {
            
            Map<String,List<Double>> urlsToScores = new HashMap<String, List<Double>>();
            double contextGlobalMax = 0D;
            double contextLocalMax = 0D;
            double aprioriMax = 0D;
            
            for (String u : uris) {
            	
            	double[] contextScore	= getContextScore(u, secondEntity, contextEntitiesInArticle);
            	double aprioriScore		= getAprioriScore(u);
            	urlsToScores.put(u, Arrays.asList(contextScore[0], contextScore[1], aprioriScore, getStringSimilarityScore(label, u)));
            	contextGlobalMax = Math.max(contextGlobalMax, contextScore[0]);
            	contextLocalMax = Math.max(contextLocalMax, contextScore[1]);
            	aprioriMax = Math.max(aprioriMax, aprioriScore);
            }
            	
            double max = 0, score;
            String uri = "";
            
            for ( Map.Entry<String, List<Double>> scoreEntry : urlsToScores.entrySet() ) {
            	
            	Double contextGlobal	= this.contextGlobalParameter * (scoreEntry.getValue().get(0) / contextGlobalMax);
            	Double contextLocal		= this.contextLocalParameter * (scoreEntry.getValue().get(1) / contextLocalMax);
            	Double apriori			= this.aprioriParameter * (scoreEntry.getValue().get(2) / aprioriMax);
            	Double stringsim		= this.stringSimParameter * (scoreEntry.getValue().get(3));
            	
//            	System.out.println("Global: " + contextGlobal + " Local:"+ contextLocal +" Apriori:" + apriori + " Stringsim:" + stringsim);
            	
            	contextGlobal = contextGlobal.isNaN() || contextGlobal.isInfinite() ? 0 : contextGlobal;
            	apriori = apriori.isNaN() || apriori.isInfinite() ? 0 : apriori;
            	stringsim = stringsim.isNaN() || stringsim.isInfinite() ? 0 : stringsim;
            	contextLocal = contextLocal.isNaN() || contextLocal.isInfinite() ? 0 : contextLocal;
            	
            	score = (apriori + contextGlobal + stringsim + contextLocal) / 4;
            	
            	this.apriori.addValue(df.format(apriori));
            	this.local.addValue(df.format(contextLocal));
            	this.global.addValue(df.format(contextGlobal));
            	this.stringsim.addValue(df.format(stringsim));
            	this.score.addValue(df.format(score));
            	
                if (score >= max) {
                	
                    max = score;
                    uri = scoreEntry.getKey();
                }
            }
            
            if ( max < this.urlScoreThreshold  ) return Constants.NON_GOOD_URL_FOUND;
            return uri;
        }
    }
    
    private double getStringSimilarityScore(String label, String uri) {
		
    	AbstractStringMetric metric = new QGramsDistance();
    	double max = 0D;
    	for ( String surfaceForm : getSurfaceFormsForUri(uri, dbpediaSearcher)) {
    		
    		double sim = metric.getSimilarity(label, surfaceForm);
//    		System.out.println(label + " " + surfaceForm + " " +sim);
    		max = Math.max(max, sim);
    	}
//    	System.out.println(max);
		return max;
	}

	private double[] getContextScore(String uriCandidate, String secondEntity, List<String> contextEntitiesInArticle) {

    	if ( !this.contextEntityCache.containsKey(uriCandidate) ) 
    		this.contextEntityCache.put(uriCandidate, getContextNamedEntities(uriCandidate, boaSearcher));
    	
    	Set<String> contextInArticle = new HashSet<String>(contextEntitiesInArticle);
    	return new double[]{
    			getJaccardSimilarity(contextEntityCache.get(uriCandidate).keySet(), contextInArticle), 
    			this.contextEntityCache.get(uriCandidate).containsKey(secondEntity) ? this.contextEntityCache.get(uriCandidate).get(secondEntity) : 0 };
	}
    
	/**
     * 
     * @param setOne
     * @param setTwo
     * @return
     */
    public static double getJaccardSimilarity(Set<String> setOne, Set<String> setTwo) {
        
        Set<String> copyOfSetOne = new HashSet<String>(setOne);
        copyOfSetOne.retainAll(setTwo);
        double z = (double) copyOfSetOne.size();
        double jaccard = z / (setOne.size() + setTwo.size() - z);
        return jaccard;         
    }
    
    /**
     * 
     * @param label
     * @param secondEntity
     * @return
     */
	public String getUri(String label, String secondEntity) {
		
		return getUri(label, secondEntity, new ArrayList<String>());
	}
	
	/**
	 * 
	 * @param uri
	 * @param searcher
	 * @return
	 */
	public Map<String,Integer> getContextNamedEntities(String uri, IndexSearcher searcher) {
    	
    	TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
    	LuceneManager.query(searcher, new TermQuery(new Term(Constants.BOA_LUCENE_FIELD_URI, uri)), collector);
        
    	Frequency f = new Frequency();
    	
    	for ( ScoreDoc hit : collector.topDocs().scoreDocs ) 
    		for ( String entity : LuceneManager.getDocumentByNumber(searcher.getIndexReader(), hit.doc).getValues(Constants.BOA_LUCENE_FIELD_ENTITY))
    			if ( !entity.contains("`") ) f.addValue(entity);

    	Map<String,Integer> namedEntities = new HashMap<String,Integer>();
    	Iterator<Comparable<?>> iter = f.valuesIterator();
    	while ( iter.hasNext() ) {
    		String value = (String) iter.next();
    		namedEntities.put(value, new Long(f.getCount(value)).intValue());
    	}
    	
        return namedEntities;
	}
	
	/**
	 * 
	 * @param label
	 * @param searcher
	 * @return
	 */
    public List<String> getUriForSurfaceForm(String label, IndexSearcher searcher) {
    	
    	Query phraseQuery = new TermQuery(new Term(Constants.DBPEDIA_LUCENE_FIELD_SURFACE_FORM,  label));
    	
    	TopFieldDocs docs = LuceneManager.query(searcher, phraseQuery, new Sort(new SortField(Constants.DBPEDIA_LUCENE_FIELD_DISAMBIGUATION_SCORE, SortField.DOUBLE, true)), 100);
    	List<String> uriCandidates = new ArrayList<String>();
    	
        for ( ScoreDoc hit : docs.scoreDocs ) 
        	uriCandidates.add(LuceneManager.getDocumentByNumber(
        			searcher.getIndexReader(), hit.doc).get(Constants.DBPEDIA_LUCENE_FIELD_URI));
        	
        return uriCandidates;
	}
    
    /**
     * 
     * @param uri
     * @param dbpediaSearcher2 
     * @return
     */
    public Set<String> getSurfaceFormsForUri(String uri, IndexSearcher searcher) {
    	
    	if ( !this.surfaceFormsCache.containsKey(uri) ) {
    	
    		TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);
        	LuceneManager.query(searcher, new TermQuery(new Term(Constants.DBPEDIA_LUCENE_FIELD_URI, uri)), collector);
        	Set<String> surfaceForms = new HashSet<String>();
        	
            for ( ScoreDoc hit : collector.topDocs().scoreDocs ) 
            	surfaceForms.addAll(Arrays.asList(LuceneManager.getDocumentByNumber(
            			searcher.getIndexReader(), hit.doc).getValues(Constants.DBPEDIA_LUCENE_FIELD_SURFACE_FORM)));
            
            this.surfaceFormsCache.put(uri, surfaceForms);
    	}
        return this.surfaceFormsCache.get(uri);
	}
    
    /**
     * 
     * @param uri
     * @return
     */
	public double getAprioriScoreForUri(String uri, IndexSearcher searcher) {
		
    	TopScoreDocCollector collector = TopScoreDocCollector.create(1, true);
        LuceneManager.query(searcher, new TermQuery(new Term(Constants.DBPEDIA_LUCENE_FIELD_URI, uri)), collector);

        return Double.valueOf(LuceneManager.getDocumentByNumber(searcher.getIndexReader(), collector.topDocs().scoreDocs[0].doc)
        		.get(Constants.DBPEDIA_LUCENE_FIELD_DISAMBIGUATION_SCORE));
	}
}
