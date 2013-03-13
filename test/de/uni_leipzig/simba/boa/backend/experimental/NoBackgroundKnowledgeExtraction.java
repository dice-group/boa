/**
 * 
 */
package de.uni_leipzig.simba.boa.backend.experimental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.gerbsen.file.BufferedFileReader;
import com.github.gerbsen.file.BufferedFileWriter;
import com.github.gerbsen.file.BufferedFileWriter.WRITER_WRITE_MODE;

import de.uni_leipzig.simba.boa.backend.Constants;
import de.uni_leipzig.simba.boa.backend.configuration.NLPediaSetup;
import de.uni_leipzig.simba.boa.backend.naturallanguageprocessing.namedentityrecognition.impl.StanfordNLPNamedEntityRecognition;

/**
 * @author gerb
 * 
 */
public class NoBackgroundKnowledgeExtraction {

    static NLPediaSetup setup = new NLPediaSetup(true);

    /**
     * @param args
     */
    public static void main(String[] args) {

        int patternsSize = 0;
        Map<String, Integer> patterns = new HashMap<String, Integer>();

        BufferedFileReader reader = new BufferedFileReader("/Users/gerb/Development/workspaces/experimental/boa/news/en_news_2010.txt", "UTF-8");
        StanfordNLPNamedEntityRecognition nerTagger = new StanfordNLPNamedEntityRecognition();

        BufferedFileWriter newPatternWriter = new BufferedFileWriter("/Users/gerb/Development/workspaces/experimental/boa/news/new_patterns.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        
        int i = 0;
        String line = "";
        while ((line = reader.readLine()) != null) {

            if (++i % 10000 == 0) {
                
                System.out.println("" + (patterns.size() - patternsSize));
                newPatternWriter.write((patterns.size() - patternsSize) + " ");
                newPatternWriter.flush();
                patternsSize = patterns.size();
                printStatistics(i, patterns);
            }

            Set<String> patternsInSentence = createPatterns(nerTagger.getAnnotatedString(line));

            for (String pattern : patternsInSentence) {

                if (patterns.containsKey(pattern))
                    patterns.put(pattern, patterns.get(pattern) + 1);
                else
                    patterns.put(pattern, 1);
            }
        }
        newPatternWriter.write("" + (patterns.size() - patternsSize));
        printStatistics(i, patterns);
        newPatternWriter.close();
    }
    
    private static void printStatistics(int i, Map<String, Integer> patterns) {
        
        System.out.println(i + " lines finished!");
        BufferedFileWriter writerArg1 = new BufferedFileWriter("/Users/gerb/Development/workspaces/experimental/boa/news/en_news_2010_eval.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
//        BufferedFileWriter writerArg2 = new BufferedFileWriter("/Users/gerb/Development/workspaces/experimental/boa/news/en_news_2010_arg2.txt", "UTF-8", WRITER_WRITE_MODE.OVERRIDE);
        writerArg1.write("After " + i + " sentences! Found " + patterns.size() + " patterns!\n\n");
//        writerArg2.write("After " + i + " sentences! Found " + patterns.size() + " patterns!\n\n");
        for (Map.Entry<String, Integer> entry : MapUtil.sortByValue(patterns, true).entrySet()) {

            if (entry.getValue() > 1) writerArg1.write(String.format("%05d", entry.getValue()) + ": " + entry.getKey());
//            if (entry.getKey().startsWith("<") ) writerArg1.write(String.format("%05d", entry.getValue()) + ": " + entry.getKey());
//            if (entry.getKey().endsWith(">") )writerArg2.write(String.format("%05d", entry.getValue()) + ": " + entry.getKey());
        }
        writerArg1.close();
//        writerArg2.close();
    }

    /**
     * 
     * @param annotatedString
     * @return
     */
    private static Set<String> createPatterns(String annotatedString) {

        Set<String> patterns = new HashSet<String>();

        String[] parts = annotatedString.split(" ");
        List<String> newPattern = new ArrayList<String>();
        boolean started = false;

        for (int i = 0; i < parts.length; i++) {

            if (started) {

                for (int j = i + 1; j < parts.length; j++) {

                    if (parts[j].endsWith("_OTHER"))
                        newPattern.add(parts[j].replace("_OTHER", ""));
                    else {
                        
                        newPattern.add("<" + parts[j].substring(parts[j].lastIndexOf("_")+1 ) + ">");
//                        String newPatternString = newPattern.get(newPattern.size() - 2) +" "+ newPattern.get(newPattern.size() - 1);
//                        String newPatternString1 = newPattern.get(0) +" "+ newPattern.get(1);
                        String newPatternString = StringUtils.join(newPattern, " ");
                        newPattern.removeAll(Constants.STOP_WORDS);
                        newPattern.removeAll(Arrays.asList("<MISC>", "<PERSON>", "<PLACE>", "<ORGANIZATION>"));
                        
                        Iterator<String> iter = newPattern.iterator();
                        while ( iter.hasNext() ) {
                            
                            try { 
                                Double d = Double.valueOf(iter.next());
                                iter.remove();
                            } catch (NumberFormatException nfe) {}
                        }
                        if ( !newPattern.isEmpty() ) patterns.add(newPatternString);
//                        if ( !newPatternString.contains("> <"))patterns.add(newPatternString);
//                        if ( !newPatternString1.contains("> <"))patterns.add(newPatternString1);
                        newPattern = new ArrayList<String>();
                        i = j;
                        started = false;
                        break;
                    }
                }
            }
            else {

                if (!parts[i].endsWith("_OTHER")) {
                    
                    newPattern.add("<" + parts[i].substring(parts[i].lastIndexOf("_")+1) + ">");
                    started = true;
                }
                continue;
            }
        }

        return patterns;
    }

    static class MapUtil {

        public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, final boolean reverse) {

            List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

                public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {

                    return !reverse ? (o1.getValue()).compareTo(o2.getValue()) : (o2.getValue()).compareTo(o1.getValue());
                }
            });

            Map<K, V> result = new LinkedHashMap<K, V>();
            for (Map.Entry<K, V> entry : list) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }
}
