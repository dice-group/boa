package de.uni_leipzig.simba.boa.backend.nlp.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CoreferenceResolution {

	public static void main(String[] args) throws IOException {

		Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        String text = "He was educated in Luanda, the capital of Angola, which was then ruled by Portugal. As a young man in 1957 he went to Paris, where he met Mário Pinto de Andrade, another Angolan poet and politician, who helped with his political ideas. In the 1960s they helped create the group called the Popular Movement for the Liberation of Angola (MPLA--from the Portuguese name Movimento Popular de Libertação de Angola).";
        
        Annotation document = new Annotation(text);

        pipeline.annotate(document);
        Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
        
        System.out.println("Document:\t" + document.toString());
        System.out.println("Graph:\t" + graph);
        
        pipeline.xmlPrint(document, new FileOutputStream("/Users/gerb/Downloads/24-07-2011/stanford-corenlp-2011-06-19/out.xml"));
        
        for(Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {

            CorefChain c =   entry.getValue();                
            System.out.println("ClusterId: " + entry.getKey());
            CorefMention cm = c.getRepresentativeMention();
            System.out.println("Representative Mention: " + cm.position); 

            List<CorefMention> cms = c.getCorefMentions();
            System.out.println("Mentions:  ");
            for (CorefMention it : cms ){
            	
                System.out.println(it + "|"); 
            } 
          }
	}
}
