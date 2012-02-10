package de.uni_leipzig.simba.boa.backend.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSettings;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.logging.NLPediaLogger;
import de.uni_leipzig.simba.boa.backend.rdf.entity.TripleTest;
import de.uni_leipzig.simba.boa.backend.util.TimeUtil;


public class LuceneTest {

    
 // initialize logging and settings
    NLPediaSetup setup = null;
    NLPediaLogger logger = null;
    
    public static junit.framework.Test suite() {

        return new JUnit4TestAdapter(LuceneTest.class);
    }

    @Before
    public void setUp() {

        this.setup = new NLPediaSetup(true);
        this.logger = new NLPediaLogger(LuceneTest.class);
    }

    @After
    public void cleanUpStreams() {

        this.setup.destroy();
    }
    
    @Test
    public void testBooleanQuery() throws IOException, ParseException {

        Set<String> list1 = this.createList1();
        Set<String> list2 = this.createList2();
        Set<String> list3 = this.createList3();
        Set<String> list4 = this.createList4();
        Set<String> list5 = this.createList5();
        Set<String> list6 = this.createList6();
        
        QueryParser qp = new QueryParser(Version.LUCENE_34, "sentence", new LowerCaseWhitespaceAnalyzer());
        IndexSearcher searcher = LuceneIndexHelper.getIndexSearcher(NLPediaSettings.BOA_DATA_DIRECTORY + Constants.INDEX_CORPUS_PATH);
        
        assertEquals(this.query1Test(searcher, qp, list1, list2), this.query2Test(searcher, qp, list1, list2));
        assertEquals(this.query1Test(searcher, qp, list3, list4), this.query2Test(searcher, qp, list3, list4));
        assertEquals(this.query1Test(searcher, qp, list5, list6), this.query2Test(searcher, qp, list5, list6));
        
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000 ; i++) {
            
            this.query1Test(searcher, qp, list1, list2);
            this.query1Test(searcher, qp, list3, list4);
            this.query1Test(searcher, qp, list5, list6);
        }
        long end = System.currentTimeMillis() - start;
        System.out.println("Time for combined query: " + TimeUtil.convertMilliSeconds(end) + ". Average: " + (double) end / 1000);
        
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000 ; i++) {
            
            this.query2Test(searcher, qp, list1, list2);
            this.query2Test(searcher, qp, list3, list4);
            this.query2Test(searcher, qp, list5, list6);
        }
        end = System.currentTimeMillis() - start;
        System.out.println("Time for single queries: " + TimeUtil.convertMilliSeconds(end) + ". Average " + (double) end / 1000);
    }
    
    private int query1Test(IndexSearcher searcher, QueryParser qp, Set<String> list1, Set<String> list2 ) throws IOException, ParseException {

        String first =  "sentence:(" + StringUtils.join(escapeList(list1), " OR ") + ")";
        String second = "sentence:(" + StringUtils.join(escapeList(list2), " OR ") + ")";
        String booleanQuery = first + " AND " + second;
        
        ScoreDoc[] docs = searcher.search(qp.parse(booleanQuery), 100).scoreDocs;
        Set<String> sentences = new HashSet<String>();
        for (int i = 0; i < docs.length; i++) {
            
            sentences.add(LuceneIndexHelper.getFieldValueByDocId(searcher, docs[i].doc, "sentence"));
        }
        return sentences.size();
    }
    
    private int query2Test(IndexSearcher searcher, QueryParser qp, Set<String> list1, Set<String> list2) throws IOException, ParseException {

        Set<String> sentences = new HashSet<String>();
        for (String s1 : list1 ) {
            for (String s2 :  list2) {
                
                ScoreDoc[] docs = searcher.search(qp.parse("+sentence:\"" + QueryParser.escape(s1) + "\" && +sentence:\"" + QueryParser.escape(s2) + "\""), 100).scoreDocs;
                
                for (int i = 0; i < docs.length; i++) {
                    
                    sentences.add(LuceneIndexHelper.getFieldValueByDocId(searcher, docs[i].doc, "sentence"));
                }
            }
        }
        return sentences.size();
    }

    private Set<String> createList2() {

        Set<String> list2 = new HashSet<String>();
        list2.add("andrè previn");
        list2.add("andreas ludwig priwin");
        list2.add("andre previn");
        list2.add("sir andre previn");
        list2.add("andrea previn");
        list2.add("sir andré previn");
        list2.add("andré prévin");
        list2.add("andrew preview");
        list2.add("andre");
        list2.add("previn");
        return list2;
    }

    private Set<String> createList1() {
        
        Set<String> list1 =  new HashSet<String>();
        list1.add("miafarrow");
        list1.add("farrow");
        list1.add("mia farrow");
        list1.add("tam farrow");
        list1.add("maria de lourdes villiers farrow");
        
        return list1;
    }
    
    private Set<String> createList3() {
        
        Set<String> list1 =  new HashSet<String>();
        list1.add("storer house");
        list1.add("john storer house");
        list1.add("dr. john storer house");
        
        
        return list1;
    }
    
    private Set<String> createList4() {
        
        Set<String> list1 =  new HashSet<String>();
        list1.add("wright");
        list1.add("frank l. wright");
        list1.add("flw");
        list1.add("wright, frank lloyd");
        list1.add("f.l. wright");
        list1.add("fl wright");
        list1.add("franklin lloyd wright");
        list1.add("wright,frank lloyd");
        list1.add("f. l. wright");
        list1.add("frank lloyd wright");
        list1.add("frank lyold wright");
        list1.add("frank lloyd");
        
        return list1;
    }
    
 private Set<String> createList5() {
        
        Set<String> list1 =  new HashSet<String>();
        list1.add("isabella of valois");
        
        return list1;
    }
    
    private Set<String> createList6() {
        
        Set<String> list1 =  new HashSet<String>();
        list1.add("richard ii");
        list1.add("richard ii of england");
        list1.add("king richard ii");
        list1.add("king richard ii of england");
        list1.add("richard, duke of cornwall");
        list1.add("richard ii, king of england");
        
        return list1;
    }
    
    private static Set<String> escapeList(Set<String> tokens) {
        
        Set<String> labels = new HashSet<String>();
        for ( String label : tokens ) {
            
            labels.add("\"" + QueryParser.escape(label) + "\"");
        }
        return labels;
    }
}
